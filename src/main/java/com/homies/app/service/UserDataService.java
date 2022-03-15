package com.homies.app.service;

import com.homies.app.domain.UserData;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link UserData}.
 */
public interface UserDataService {
    /**
     * Save a userData.
     *
     * @param userData the entity to save.
     * @return the persisted entity.
     */
    UserData save(UserData userData);

    /**
     * Partially updates a userData.
     *
     * @param userData the entity to update partially.
     * @return the persisted entity.
     */
    Optional<UserData> partialUpdate(UserData userData);

    /**
     * Get all the userData.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<UserData> findAll(Pageable pageable);
    /**
     * Get all the UserData where UserName is {@code null}.
     *
     * @return the {@link List} of entities.
     */
    List<UserData> findAllWhereUserNameIsNull();

    /**
     * Get the "id" userData.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UserData> findOne(Long id);

    /**
     * Delete the "id" userData.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
