package com.homies.app.service.impl;

import com.homies.app.domain.UserName;
import com.homies.app.repository.UserNameRepository;
import com.homies.app.service.UserNameService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link UserName}.
 */
@Service
@Transactional
public class UserNameServiceImpl implements UserNameService {

    private final Logger log = LoggerFactory.getLogger(UserNameServiceImpl.class);

    private final UserNameRepository userNameRepository;

    public UserNameServiceImpl(UserNameRepository userNameRepository) {
        this.userNameRepository = userNameRepository;
    }

    @Override
    public UserName save(UserName userName) {
        log.debug("Request to save UserName : {}", userName);
        return userNameRepository.save(userName);
    }

    @Override
    public Optional<UserName> partialUpdate(UserName userName) {
        log.debug("Request to partially update UserName : {}", userName);

        return userNameRepository
            .findById(userName.getId())
            .map(existingUserName -> {
                if (userName.getEmail() != null) {
                    existingUserName.setEmail(userName.getEmail());
                }
                if (userName.getNick() != null) {
                    existingUserName.setNick(userName.getNick());
                }
                if (userName.getPassword() != null) {
                    existingUserName.setPassword(userName.getPassword());
                }
                if (userName.getName() != null) {
                    existingUserName.setName(userName.getName());
                }
                if (userName.getSurname() != null) {
                    existingUserName.setSurname(userName.getSurname());
                }
                if (userName.getPhoto() != null) {
                    existingUserName.setPhoto(userName.getPhoto());
                }
                if (userName.getPhotoContentType() != null) {
                    existingUserName.setPhotoContentType(userName.getPhotoContentType());
                }
                if (userName.getPhone() != null) {
                    existingUserName.setPhone(userName.getPhone());
                }
                if (userName.getPremium() != null) {
                    existingUserName.setPremium(userName.getPremium());
                }
                if (userName.getBirthDate() != null) {
                    existingUserName.setBirthDate(userName.getBirthDate());
                }
                if (userName.getAddDate() != null) {
                    existingUserName.setAddDate(userName.getAddDate());
                }
                if (userName.getToken() != null) {
                    existingUserName.setToken(userName.getToken());
                }

                return existingUserName;
            })
            .map(userNameRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserName> findAll(Pageable pageable) {
        log.debug("Request to get all UserNames");
        return userNameRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserName> findOne(Long id) {
        log.debug("Request to get UserName : {}", id);
        return userNameRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete UserName : {}", id);
        userNameRepository.deleteById(id);
    }
}
