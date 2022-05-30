package com.homies.app.web.rest;

import com.homies.app.domain.Products;
import com.homies.app.repository.ProductsRepository;
import com.homies.app.service.AuxiliarServices.CreateProductAuxService;
import com.homies.app.service.AuxiliarServices.ManageProductAuxService;
import com.homies.app.service.ProductsQueryService;
import com.homies.app.service.ProductsService;
import com.homies.app.service.criteria.ProductsCriteria;
import com.homies.app.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.homies.app.web.rest.errors.Group.GroupWasNotSpecifyIdGroup;
import com.homies.app.web.rest.errors.Task.TaskWasNotSpecifyIdTask;
import com.homies.app.web.rest.errors.User.UserWasNotSpecifyLogin;
import com.homies.app.web.rest.vm.AddProductVM;
import com.homies.app.web.rest.vm.UpdateProductVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.homies.app.domain.Products}.
 */
@RestController
@RequestMapping("/api")
public class ProductsResource {

    private final Logger log = LoggerFactory.getLogger(ProductsResource.class);

    private static final String ENTITY_NAME = "products";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;
    @Autowired
    private final ProductsService productsService;
    @Autowired
    private final ProductsRepository productsRepository;
    @Autowired
    private final ProductsQueryService productsQueryService;
    @Autowired
    private final CreateProductAuxService createProductAuxService;
    @Autowired
    private final ManageProductAuxService manageProductAuxService;
    @Autowired
    private final CacheManager cacheManager;
    public ProductsResource(
        ProductsService productsService,
        ProductsRepository productsRepository,
        ProductsQueryService productsQueryService,
        CreateProductAuxService createProductAuxService,
        ManageProductAuxService manageProductAuxService,
        CacheManager cacheManager
    ) {
        this.productsService = productsService;
        this.productsRepository = productsRepository;
        this.productsQueryService = productsQueryService;
        this.createProductAuxService = createProductAuxService;
        this.manageProductAuxService = manageProductAuxService;
        this.cacheManager = cacheManager;
    }

    /**
     * {@code POST  /products} : Create a new products.
     *
     * @param product the products to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new products, or with status {@code 400 (Bad Request)} if the products has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/products")
    public ResponseEntity<Products> createProducts(@Valid @RequestBody AddProductVM product) throws URISyntaxException {
        log.warn("REST request to save Products : {}", product.toString());

        Products newProduct = createProductAuxService.createNewProduct(product);

        if (newProduct != null)
            return new ResponseEntity<>(newProduct, HttpStatus.CREATED);

        clearCache();

        return ResponseEntity
            .created(new URI("/api/products/" + newProduct.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, newProduct.getId().toString()))
            .body(newProduct);
    }

    @PutMapping("/products/update-products")
    public ResponseEntity<Products> updateProduct(@Valid @RequestBody UpdateProductVM updateProductVM)
        throws URISyntaxException {
        log.warn("REST request to update Products : {}", updateProductVM.toString());
        if (updateProductVM.getIdProduct() == null) {
            throw new TaskWasNotSpecifyIdTask();
        }
        if (updateProductVM.getIdGroup() == null) {
            throw new GroupWasNotSpecifyIdGroup();
        }
        if (updateProductVM.getLogin() == null) {
            throw new UserWasNotSpecifyLogin();
        }

        Optional<Products> result = manageProductAuxService.updateProduct(updateProductVM);

        clearCache();

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.get().getName())
        );
    }

    @PutMapping("/product/cancel")
    public ResponseEntity<Products> updateProductCancel(@Valid @RequestBody UpdateProductVM updateProductVM)
        throws URISyntaxException {
        log.warn("REST request to update Products : {}", updateProductVM.toString());
        if (updateProductVM.getIdProduct() == null) {
            throw new TaskWasNotSpecifyIdTask();
        }
        if (updateProductVM.getIdGroup() == null) {
            throw new GroupWasNotSpecifyIdGroup();
        }
        if (updateProductVM.getLogin() == null) {
            throw new UserWasNotSpecifyLogin();
        }

        Optional<Products> result = manageProductAuxService.updateProductCancel(updateProductVM);

        clearCache();

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.get().getName())
        );
    }

    /**
     * {@code PATCH  /products/:id} : Partial updates given fields of an existing products, field will ignore if it is null
     *
     * @param id the id of the products to save.
     * @param products the products to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated products,
     * or with status {@code 400 (Bad Request)} if the products is not valid,
     * or with status {@code 404 (Not Found)} if the products is not found,
     * or with status {@code 500 (Internal Server Error)} if the products couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/products/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Products> partialUpdateProducts(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Products products
    ) throws URISyntaxException {
        log.warn("REST request to partial update Products partially : {}, {}", id, products.toString());
        if (products.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, products.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Products> result = productsService.partialUpdate(products);

        clearCache();

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, products.getId().toString())
        );
    }

    /**
     * {@code GET  /products} : get all the products.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of products in body.
     */
    @GetMapping("/products")
    public ResponseEntity<List<Products>> getAllProducts(
        ProductsCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.warn("REST request to get Products by criteria: {}", criteria.toString());
        Page<Products> page = productsQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        clearCache();

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /products/count} : count all the products.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/products/count")
    public ResponseEntity<Long> countProducts(ProductsCriteria criteria) {
        log.warn("REST request to count Products by criteria: {}", criteria.toString());

        clearCache();

        return ResponseEntity.ok().body(productsQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /products/:id} : get the "id" products.
     *
     * @param id the id of the products to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the products, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<Products> getProducts(@PathVariable Long id) {
        log.warn("REST request to get Products : {}", id);
        Optional<Products> products = productsService.findOne(id);

        clearCache();

        return ResponseUtil.wrapOrNotFound(products);
    }

    /**
     * {@code DELETE  /products/:id} : delete the "id" products.
     *
     * @param id the id of the products to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProducts(@PathVariable Long id) {
        log.warn("REST request to delete Products : {}", id);
        //productsService.delete(id);

        boolean result = manageProductAuxService.deleteProducts(id);

        clearCache();

        if (!result) {
            throw new BadRequestAlertException("The product could not be removed", ENTITY_NAME, "TheProductCouldNotBeRemoved");
        }
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    private void clearCache() {
        for(String name:cacheManager.getCacheNames()){
            Objects.requireNonNull(cacheManager.getCache(name)).clear();            // clear cache by name
        }
    }
}
