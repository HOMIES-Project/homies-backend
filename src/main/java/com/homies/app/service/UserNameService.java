package com.homies.app.service;

import com.homies.app.domain.UserName;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link UserName}.
 */
public interface UserNameService {
    /**
     * Save a userName.
     *
     * @param userName the entity to save.
     * @return the persisted entity.
     */
    UserName save(UserName userName);

    /**
     * Partially updates a userName.
     *
     * @param userName the entity to update partially.
     * @return the persisted entity.
     */
    Optional<UserName> partialUpdate(UserName userName);

    /**
     * Get all the userNames.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<UserName> findAll(Pageable pageable);

    /**
     * Get the "id" userName.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UserName> findOne(Long id);

    /**
     * Delete the "id" userName.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
