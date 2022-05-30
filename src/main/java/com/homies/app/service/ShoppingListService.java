package com.homies.app.service;

import com.homies.app.domain.ShoppingList;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link ShoppingList}.
 */
public interface ShoppingListService {
    /**
     * Save a shoppingList.
     *
     * @param shoppingList the entity to save.
     * @return the persisted entity.
     */
    ShoppingList save(ShoppingList shoppingList);

    /**
     * Partially updates a shoppingList.
     *
     * @param shoppingList the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ShoppingList> partialUpdate(ShoppingList shoppingList);

    /**
     * Get all the shoppingLists.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ShoppingList> findAll(Pageable pageable);

    /**
     * Get the "id" shoppingList.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ShoppingList> findOne(Long id);

    /**
     * Delete the "id" shoppingList.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
