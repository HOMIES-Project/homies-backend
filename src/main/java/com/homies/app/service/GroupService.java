package com.homies.app.service;

import com.homies.app.domain.Group;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Group}.
 */
public interface GroupService {
    /**
     * Save a group.
     *
     * @param group the entity to save.
     * @return the persisted entity.
     */
    Group save(Group group);

    /**
     * Partially updates a group.
     *
     * @param group the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Group> partialUpdate(Group group);

    /**
     * Get all the groups.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Group> findAll(Pageable pageable);

    /**
     * Get all the groups with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Group> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" group.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Group> findOne(Long id);

    /**
     * Get the "name" group.
     *
     * @param groupName the name of entity.
     * @return the entity.
     */
    Optional<Group> findOne(String groupName);

    /**
     * Delete the "id" group.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
