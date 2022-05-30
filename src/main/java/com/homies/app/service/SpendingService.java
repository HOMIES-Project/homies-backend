package com.homies.app.service;

import com.homies.app.domain.Spending;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Spending}.
 */
public interface SpendingService {
    /**
     * Save a spending.
     *
     * @param spending the entity to save.
     * @return the persisted entity.
     */
    Spending save(Spending spending);

    /**
     * Partially updates a spending.
     *
     * @param spending the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Spending> partialUpdate(Spending spending);

    /**
     * Get all the spendings.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Spending> findAll(Pageable pageable);

    /**
     * Get the "id" spending.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Spending> findOne(Long id);

    /**
     * Delete the "id" spending.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
