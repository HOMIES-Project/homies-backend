package com.homies.app.service;

import com.homies.app.domain.SpendingList;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link SpendingList}.
 */
public interface SpendingListService {
    /**
     * Save a spendingList.
     *
     * @param spendingList the entity to save.
     * @return the persisted entity.
     */
    SpendingList save(SpendingList spendingList);

    /**
     * Partially updates a spendingList.
     *
     * @param spendingList the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SpendingList> partialUpdate(SpendingList spendingList);

    /**
     * Get all the spendingLists.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SpendingList> findAll(Pageable pageable);

    /**
     * Get the "id" spendingList.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SpendingList> findOne(Long id);

    /**
     * Delete the "id" spendingList.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
