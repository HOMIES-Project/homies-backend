package com.homies.app.service;

import com.homies.app.domain.TaskList;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link TaskList}.
 */
public interface TaskListService {
    /**
     * Save a taskList.
     *
     * @param taskList the entity to save.
     * @return the persisted entity.
     */
    TaskList save(TaskList taskList);

    /**
     * Partially updates a taskList.
     *
     * @param taskList the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TaskList> partialUpdate(TaskList taskList);

    /**
     * Get all the taskLists.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TaskList> findAll(Pageable pageable);

    /**
     * Get the "id" taskList.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TaskList> findOne(Long id);

    /**
     * Delete the "id" taskList.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
