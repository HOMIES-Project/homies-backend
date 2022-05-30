package com.homies.app.service.impl;

import com.homies.app.domain.UserPending;
import com.homies.app.repository.UserPendingRepository;
import com.homies.app.service.UserPendingService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link UserPending}.
 */
@Service
@Transactional
public class UserPendingServiceImpl implements UserPendingService {

    private final Logger log = LoggerFactory.getLogger(UserPendingServiceImpl.class);

    private final UserPendingRepository userPendingRepository;

    public UserPendingServiceImpl(UserPendingRepository userPendingRepository) {
        this.userPendingRepository = userPendingRepository;
    }

    @Override
    public UserPending save(UserPending userPending) {
        log.debug("Request to save UserPending : {}", userPending);
        return userPendingRepository.save(userPending);
    }

    @Override
    public Optional<UserPending> partialUpdate(UserPending userPending) {
        log.debug("Request to partially update UserPending : {}", userPending);

        return userPendingRepository
            .findById(userPending.getId())
            .map(existingUserPending -> {
                if (userPending.getPending() != null) {
                    existingUserPending.setPending(userPending.getPending());
                }
                if (userPending.getPaid() != null) {
                    existingUserPending.setPaid(userPending.getPaid());
                }

                return existingUserPending;
            })
            .map(userPendingRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserPending> findAll(Pageable pageable) {
        log.debug("Request to get all UserPendings");
        return userPendingRepository.findAll(pageable);
    }

    public Page<UserPending> findAllWithEagerRelationships(Pageable pageable) {
        return userPendingRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserPending> findOne(Long id) {
        log.debug("Request to get UserPending : {}", id);
        return userPendingRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete UserPending : {}", id);
        userPendingRepository.deleteById(id);
    }
}
