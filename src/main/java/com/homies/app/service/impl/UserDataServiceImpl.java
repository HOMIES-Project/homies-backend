package com.homies.app.service.impl;

import com.homies.app.domain.UserData;
import com.homies.app.repository.UserDataRepository;
import com.homies.app.repository.UserRepository;
import com.homies.app.service.UserDataService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link UserData}.
 */
@Service
@Transactional
public class UserDataServiceImpl implements UserDataService {

    private final Logger log = LoggerFactory.getLogger(UserDataServiceImpl.class);

    private final UserDataRepository userDataRepository;

    private final UserRepository userRepository;

    public UserDataServiceImpl(UserDataRepository userDataRepository, UserRepository userRepository) {
        this.userDataRepository = userDataRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UserData save(UserData userData) {
        log.debug("Request to save UserData : {}", userData);
        Long userId = userData.getUser().getId();
        userRepository.findById(userId).ifPresent(userData::user);
        return userDataRepository.save(userData);
    }

    @Override
    public Optional<UserData> partialUpdate(UserData userData) {
        log.debug("Request to partially update UserData : {}", userData);

        return userDataRepository
            .findById(userData.getId())
            .map(existingUserData -> {
                if (userData.getPhoto() != null) {
                    existingUserData.setPhoto(userData.getPhoto());
                }
                if (userData.getPhotoContentType() != null) {
                    existingUserData.setPhotoContentType(userData.getPhotoContentType());
                }
                if (userData.getPhone() != null) {
                    existingUserData.setPhone(userData.getPhone());
                }
                if (userData.getPremium() != null) {
                    existingUserData.setPremium(userData.getPremium());
                }
                if (userData.getBirthDate() != null) {
                    existingUserData.setBirthDate(userData.getBirthDate());
                }
                if (userData.getAddDate() != null) {
                    existingUserData.setAddDate(userData.getAddDate());
                }

                return existingUserData;
            })
            .map(userDataRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserData> findAll(Pageable pageable) {
        log.debug("Request to get all UserData");
        return userDataRepository.findAll(pageable);
    }

    public Page<UserData> findAllWithEagerRelationships(Pageable pageable) {
        return userDataRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserData> findOne(Long id) {
        log.debug("Request to get UserData : {}", id);
        return userDataRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete UserData : {}", id);
        userDataRepository.deleteById(id);
    }
}
