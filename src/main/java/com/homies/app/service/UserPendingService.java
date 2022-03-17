package com.homies.app.service;

import com.homies.app.domain.UserPending;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link UserPending}.
 */
public interface UserPendingService {
    /**
     * Save a userPending.
     *
     * @param userPending the entity to save.
     * @return the persisted entity.
     */
    UserPending save(UserPending userPending);

    /**
     * Partially updates a userPending.
     *
     * @param userPending the entity to update partially.
     * @return the persisted entity.
     */
    Optional<UserPending> partialUpdate(UserPending userPending);

    /**
     * Get all the userPendings.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<UserPending> findAll(Pageable pageable);

    /**
     * Get the "id" userPending.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UserPending> findOne(Long id);

    /**
     * Delete the "id" userPending.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
