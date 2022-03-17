package com.homies.app.repository;

import com.homies.app.domain.ShoppingList;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ShoppingList entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long>, JpaSpecificationExecutor<ShoppingList> {}
