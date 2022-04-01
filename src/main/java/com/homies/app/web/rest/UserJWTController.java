package com.homies.app.web.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.homies.app.domain.UserData;
import com.homies.app.security.jwt.JWTFilter;
import com.homies.app.security.jwt.TokenProvider;
import com.homies.app.service.UserDataQueryService;
import com.homies.app.service.UserDataService;
import com.homies.app.service.UserService;
import com.homies.app.web.rest.vm.LoginVM;
import javax.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static com.homies.app.config.Constants.CROSS_ORIGIN;

/**
 * Controller to authenticate users.
 */
@CrossOrigin(origins = CROSS_ORIGIN, maxAge = 3600)
@RestController
@RequestMapping("/api")
public class UserJWTController {

    private final TokenProvider tokenProvider;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final UserDataQueryService userDataQueryService;

    public UserJWTController(TokenProvider tokenProvider,
                             AuthenticationManagerBuilder authenticationManagerBuilder,
                             UserDataQueryService userDataQueryService) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userDataQueryService = userDataQueryService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginVM loginVM) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            loginVM.getUsername(),
            loginVM.getPassword()
        );

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication, loginVM.isRememberMe());
        HttpHeaders httpHeaders = new HttpHeaders();
        UserData userData = userDataQueryService.getByUser_Login(authentication.getName()).get();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        return new ResponseEntity<>(new JWTToken(jwt, userData.getId()), httpHeaders, HttpStatus.OK);
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    static class JWTToken {

        private String idToken;
        private Long id;

        JWTToken(String idToken, Long id) {
            this.idToken = idToken;
            this.id = id;
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("id")
        Long getId() {
            return id;
        }

        void setId(Long id) {
            this.id = id;
        }
    }
}
