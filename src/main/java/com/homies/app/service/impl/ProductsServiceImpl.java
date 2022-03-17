package com.homies.app.service.impl;

import com.homies.app.domain.Products;
import com.homies.app.repository.ProductsRepository;
import com.homies.app.service.ProductsService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Products}.
 */
@Service
@Transactional
public class ProductsServiceImpl implements ProductsService {

    private final Logger log = LoggerFactory.getLogger(ProductsServiceImpl.class);

    private final ProductsRepository productsRepository;

    public ProductsServiceImpl(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    @Override
    public Products save(Products products) {
        log.debug("Request to save Products : {}", products);
        return productsRepository.save(products);
    }

    @Override
    public Optional<Products> partialUpdate(Products products) {
        log.debug("Request to partially update Products : {}", products);

        return productsRepository
            .findById(products.getId())
            .map(existingProducts -> {
                if (products.getName() != null) {
                    existingProducts.setName(products.getName());
                }
                if (products.getPrice() != null) {
                    existingProducts.setPrice(products.getPrice());
                }
                if (products.getPhoto() != null) {
                    existingProducts.setPhoto(products.getPhoto());
                }
                if (products.getPhotoContentType() != null) {
                    existingProducts.setPhotoContentType(products.getPhotoContentType());
                }
                if (products.getUnits() != null) {
                    existingProducts.setUnits(products.getUnits());
                }
                if (products.getTypeUnit() != null) {
                    existingProducts.setTypeUnit(products.getTypeUnit());
                }
                if (products.getNote() != null) {
                    existingProducts.setNote(products.getNote());
                }
                if (products.getDataCreated() != null) {
                    existingProducts.setDataCreated(products.getDataCreated());
                }
                if (products.getShoppingDate() != null) {
                    existingProducts.setShoppingDate(products.getShoppingDate());
                }
                if (products.getPurchased() != null) {
                    existingProducts.setPurchased(products.getPurchased());
                }
                if (products.getUserCreated() != null) {
                    existingProducts.setUserCreated(products.getUserCreated());
                }

                return existingProducts;
            })
            .map(productsRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Products> findAll(Pageable pageable) {
        log.debug("Request to get all Products");
        return productsRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Products> findOne(Long id) {
        log.debug("Request to get Products : {}", id);
        return productsRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Products : {}", id);
        productsRepository.deleteById(id);
    }
}
