package com.homies.app.service.impl;

import com.homies.app.domain.ShoppingList;
import com.homies.app.repository.ShoppingListRepository;
import com.homies.app.service.ShoppingListService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ShoppingList}.
 */
@Service
@Transactional
public class ShoppingListServiceImpl implements ShoppingListService {

    private final Logger log = LoggerFactory.getLogger(ShoppingListServiceImpl.class);

    private final ShoppingListRepository shoppingListRepository;

    public ShoppingListServiceImpl(ShoppingListRepository shoppingListRepository) {
        this.shoppingListRepository = shoppingListRepository;
    }

    @Override
    public ShoppingList save(ShoppingList shoppingList) {
        log.debug("Request to save ShoppingList : {}", shoppingList);
        return shoppingListRepository.save(shoppingList);
    }

    @Override
    public Optional<ShoppingList> partialUpdate(ShoppingList shoppingList) {
        log.debug("Request to partially update ShoppingList : {}", shoppingList);

        return shoppingListRepository
            .findById(shoppingList.getId())
            .map(existingShoppingList -> {
                if (shoppingList.getName() != null) {
                    existingShoppingList.setName(shoppingList.getName());
                }
                if (shoppingList.getTotal() != null) {
                    existingShoppingList.setTotal(shoppingList.getTotal());
                }

                return existingShoppingList;
            })
            .map(shoppingListRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShoppingList> findAll(Pageable pageable) {
        log.debug("Request to get all ShoppingLists");
        return shoppingListRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ShoppingList> findOne(Long id) {
        log.debug("Request to get ShoppingList : {}", id);
        return shoppingListRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete ShoppingList : {}", id);
        shoppingListRepository.deleteById(id);
    }
}
