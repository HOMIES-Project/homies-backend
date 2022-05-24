package com.homies.app.web.rest;

import com.homies.app.domain.User;
import com.homies.app.repository.UserRepository;
import com.homies.app.security.SecurityUtils;
import com.homies.app.service.MailService;
import com.homies.app.service.UserService;
import com.homies.app.service.dto.AdminUserDTO;
import com.homies.app.service.dto.PasswordChangeDTO;
import com.homies.app.service.AuxiliarServices.CreateUserDataForUserAuxService;
import com.homies.app.web.rest.errors.*;
import com.homies.app.web.rest.auxiliary.JSONResetPasswordAux;
import com.homies.app.web.rest.errors.User.LoginAlreadyUsedException;
import com.homies.app.web.rest.vm.JSONEmailVM;
import com.homies.app.web.rest.vm.KeyAndPasswordVM;
import com.homies.app.web.rest.vm.ManagedUserVM;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.homies.app.config.Constants.CROSS_ORIGIN;
import static com.homies.app.web.rest.errors.ErrorConstants.EMAIL_NOT_EXIST_TYPE;

/**
 * REST controller for managing the current user's account.
 */
@CrossOrigin(origins = CROSS_ORIGIN, maxAge = 3600)
@RestController
@RequestMapping("/api")
public class AccountResource {

    private static class AccountResourceException extends RuntimeException {

        private AccountResourceException(String message) {
            super(message);
        }
    }

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final UserService userService;
    @Autowired
    private final MailService mailService;
    @Autowired
    private final CreateUserDataForUserAuxService createUserDataForUserAux;
    @Autowired
    private final CacheManager cacheManager;

    public AccountResource(UserRepository userRepository,
                           UserService userService,
                           MailService mailService,
                           CreateUserDataForUserAuxService createUserDataForUserAux,
                           CacheManager cacheManager) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.mailService = mailService;
        this.createUserDataForUserAux = createUserDataForUserAux;
        this.cacheManager = cacheManager;
    }

    /**
     * {@code POST  /register} : register the user.
     *
     * @param managedUserVM the managed user View Model.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already used.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {
        if (isPasswordLengthInvalid(managedUserVM.getPassword())) {
            throw new InvalidPasswordException();
        }
        log.warn("@@@@ Homies::REST Registering user {}", managedUserVM.getLogin());
        User user = userService.registerUser(managedUserVM, managedUserVM.getPassword());
        createUserDataForUserAux.createUserData(user.getId());
        mailService.sendActivationEmail(user);

        for(String name:cacheManager.getCacheNames()){
            Objects.requireNonNull(cacheManager.getCache(name)).clear();            // clear cache by name
        }

    }

    /**
     * Resend account activation email
     *
     * @param email in JSON format
     * @Response 205
     */
    @PostMapping("/email")
    @ResponseStatus(HttpStatus.RESET_CONTENT)
    public void reSendEmailOfActivation(@Valid @RequestBody JSONEmailVM email) {

        Optional<User> user = userService.getUserForEmail(email.getEmail());
        log.warn("@@@@ Homies::REST resend mail for account activation: {}", email.getEmail());
        mailService.sendActivationEmail(user.get());

        for(String name:cacheManager.getCacheNames()){
            Objects.requireNonNull(cacheManager.getCache(name)).clear();            // clear cache by name
        }
    }

    /**
     * {@code GET  /activate} : activate the registered user.
     *
     * @param key the activation key.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be activated.
     */
    @GetMapping("/activate")
    public void activateAccount(@RequestParam(value = "key") String key) {
        Optional<User> user = userService.activateRegistration(key);
        log.warn("@@@@ Homies::REST activate account: {}", key);
        if (!user.isPresent()) {
            throw new AccountResourceException("No user was found for this activation key");
        }

        for(String name:cacheManager.getCacheNames()){
            Objects.requireNonNull(cacheManager.getCache(name)).clear();            // clear cache by name
        }
    }

    /**
     * {@code GET  /authenticate} : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request.
     * @return the login if the user is authenticated.
     */
    @GetMapping("/authenticate")
    public String isAuthenticated(HttpServletRequest request) {
        log.warn("@@@@ Homies::REST authenticate user: {}", request.getRemoteUser());

        for(String name:cacheManager.getCacheNames()){
            Objects.requireNonNull(cacheManager.getCache(name)).clear();            // clear cache by name
        }

        return request.getRemoteUser();
    }

    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping("/account")
    public AdminUserDTO getAccount() {

        for(String name:cacheManager.getCacheNames()){
            Objects.requireNonNull(cacheManager.getCache(name)).clear();            // clear cache by name
        }

        return userService
            .getUserWithAuthorities()
            .map(AdminUserDTO::new)
            .orElseThrow(() -> new AccountResourceException("User could not be found"));
    }

    /**
     * {@code POST  /account} : update the current user information.
     *
     * @param userDTO the current user information.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user login wasn't found.
     */
    @PostMapping("/account")
    public void saveAccount(@Valid @RequestBody AdminUserDTO userDTO) {
        String userLogin = SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new AccountResourceException("Current user login not found"));
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userLogin))) {
            throw new EmailAlreadyUsedException();
        }
        Optional<User> user = userRepository.findOneByLogin(userLogin);
        if (!user.isPresent()) {
            throw new AccountResourceException("User could not be found");
        }
        userService.getUser(
            userDTO.getFirstName(),
            userDTO.getLastName(),
            userDTO.getEmail(),
            userDTO.getLangKey(),
            userDTO.getImageUrl()
        );

        for(String name:cacheManager.getCacheNames()){
            Objects.requireNonNull(cacheManager.getCache(name)).clear();            // clear cache by name
        }
    }

    /**
     * {@code POST  /account/change-password} : changes the current user's password.
     *
     * @param passwordChangeDto current and new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the new password is incorrect.
     */
    @PostMapping(path = "/account/change-password")
    public void changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        if (isPasswordLengthInvalid(passwordChangeDto.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        log.warn("@@@@ Homies::REST change password: {}", passwordChangeDto.getCurrentPassword());
        userService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());

        for(String name:cacheManager.getCacheNames()){
            Objects.requireNonNull(cacheManager.getCache(name)).clear();            // clear cache by name
        }
    }

    /**
     * {@code POST   /account/reset-password/init} : Send an email to reset the password of the user.
     *
     * @param email the mail of the user.
     */
    @PostMapping(path = "/account/reset-password/init")
    public ResponseEntity<String> requestPasswordReset(@Valid @RequestBody JSONEmailVM email) {
        Optional<User> user = userService.requestPasswordReset(email.getEmail());

        if (user.isPresent()) {
            log.warn("@@@@ Homies::REST request password reset: {}", email.getEmail());
            log.warn(email.getEmail());
            JSONResetPasswordAux key = new JSONResetPasswordAux(user.get().getResetKey());
            log.warn("key= " + key);
            mailService.sendPasswordResetMail(user.get());

            for(String name:cacheManager.getCacheNames()){
                Objects.requireNonNull(cacheManager.getCache(name)).clear();            // clear cache by name
            }

            return new ResponseEntity(HttpStatus.ACCEPTED, HttpStatus.OK);
        } else {

            for(String name:cacheManager.getCacheNames()){
                Objects.requireNonNull(cacheManager.getCache(name)).clear();            // clear cache by name
            }

            log.warn(EMAIL_NOT_EXIST_TYPE.toString());
            throw new EmailNotExistException();
        }
    }

    /**
     * {@code POST   /account/reset-password/finish} : Finish to reset the password of the user.
     *
     * @param keyAndPassword the generated key and the new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the password could not be reset.
     */
    @PostMapping(path = "/account/reset-password/finish")
    public void finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (isPasswordLengthInvalid(keyAndPassword.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        log.warn("@@@@ Homies::REST finish password reset: {}", keyAndPassword.getKey());
        Optional<User> user = userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());

        if (user.isEmpty()) {
            throw new AccountResourceException("No user was found for this reset key");
        }

        for(String name:cacheManager.getCacheNames()){
            Objects.requireNonNull(cacheManager.getCache(name)).clear();            // clear cache by name
        }
    }

    private static boolean isPasswordLengthInvalid(String password) {
        return (
            StringUtils.isEmpty(password) ||
            password.length() < ManagedUserVM.PASSWORD_MIN_LENGTH ||
            password.length() > ManagedUserVM.PASSWORD_MAX_LENGTH
        );
    }
}
