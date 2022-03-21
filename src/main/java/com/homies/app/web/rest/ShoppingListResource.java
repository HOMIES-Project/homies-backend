package com.homies.app.web.rest;

import com.homies.app.domain.ShoppingList;
import com.homies.app.repository.ShoppingListRepository;
import com.homies.app.service.ShoppingListQueryService;
import com.homies.app.service.ShoppingListService;
import com.homies.app.service.criteria.ShoppingListCriteria;
import com.homies.app.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.homies.app.domain.ShoppingList}.
 */
@CrossOrigin(origins = "/localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class ShoppingListResource {

    private final Logger log = LoggerFactory.getLogger(ShoppingListResource.class);

    private static final String ENTITY_NAME = "shoppingList";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ShoppingListService shoppingListService;

    private final ShoppingListRepository shoppingListRepository;

    private final ShoppingListQueryService shoppingListQueryService;

    public ShoppingListResource(
        ShoppingListService shoppingListService,
        ShoppingListRepository shoppingListRepository,
        ShoppingListQueryService shoppingListQueryService
    ) {
        this.shoppingListService = shoppingListService;
        this.shoppingListRepository = shoppingListRepository;
        this.shoppingListQueryService = shoppingListQueryService;
    }

    /**
     * {@code POST  /shopping-lists} : Create a new shoppingList.
     *
     * @param shoppingList the shoppingList to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new shoppingList, or with status {@code 400 (Bad Request)} if the shoppingList has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/shopping-lists")
    public ResponseEntity<ShoppingList> createShoppingList(@Valid @RequestBody ShoppingList shoppingList) throws URISyntaxException {
        log.debug("REST request to save ShoppingList : {}", shoppingList);
        if (shoppingList.getId() != null) {
            throw new BadRequestAlertException("A new shoppingList cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ShoppingList result = shoppingListService.save(shoppingList);
        return ResponseEntity
            .created(new URI("/api/shopping-lists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /shopping-lists/:id} : Updates an existing shoppingList.
     *
     * @param id the id of the shoppingList to save.
     * @param shoppingList the shoppingList to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shoppingList,
     * or with status {@code 400 (Bad Request)} if the shoppingList is not valid,
     * or with status {@code 500 (Internal Server Error)} if the shoppingList couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/shopping-lists/{id}")
    public ResponseEntity<ShoppingList> updateShoppingList(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ShoppingList shoppingList
    ) throws URISyntaxException {
        log.debug("REST request to update ShoppingList : {}, {}", id, shoppingList);
        if (shoppingList.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shoppingList.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shoppingListRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ShoppingList result = shoppingListService.save(shoppingList);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, shoppingList.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /shopping-lists/:id} : Partial updates given fields of an existing shoppingList, field will ignore if it is null
     *
     * @param id the id of the shoppingList to save.
     * @param shoppingList the shoppingList to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shoppingList,
     * or with status {@code 400 (Bad Request)} if the shoppingList is not valid,
     * or with status {@code 404 (Not Found)} if the shoppingList is not found,
     * or with status {@code 500 (Internal Server Error)} if the shoppingList couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/shopping-lists/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ShoppingList> partialUpdateShoppingList(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ShoppingList shoppingList
    ) throws URISyntaxException {
        log.debug("REST request to partial update ShoppingList partially : {}, {}", id, shoppingList);
        if (shoppingList.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shoppingList.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shoppingListRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ShoppingList> result = shoppingListService.partialUpdate(shoppingList);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, shoppingList.getId().toString())
        );
    }

    /**
     * {@code GET  /shopping-lists} : get all the shoppingLists.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of shoppingLists in body.
     */
    @GetMapping("/shopping-lists")
    public ResponseEntity<List<ShoppingList>> getAllShoppingLists(
        ShoppingListCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get ShoppingLists by criteria: {}", criteria);
        Page<ShoppingList> page = shoppingListQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /shopping-lists/count} : count all the shoppingLists.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/shopping-lists/count")
    public ResponseEntity<Long> countShoppingLists(ShoppingListCriteria criteria) {
        log.debug("REST request to count ShoppingLists by criteria: {}", criteria);
        return ResponseEntity.ok().body(shoppingListQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /shopping-lists/:id} : get the "id" shoppingList.
     *
     * @param id the id of the shoppingList to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the shoppingList, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/shopping-lists/{id}")
    public ResponseEntity<ShoppingList> getShoppingList(@PathVariable Long id) {
        log.debug("REST request to get ShoppingList : {}", id);
        Optional<ShoppingList> shoppingList = shoppingListService.findOne(id);
        return ResponseUtil.wrapOrNotFound(shoppingList);
    }

    /**
     * {@code DELETE  /shopping-lists/:id} : delete the "id" shoppingList.
     *
     * @param id the id of the shoppingList to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/shopping-lists/{id}")
    public ResponseEntity<Void> deleteShoppingList(@PathVariable Long id) {
        log.debug("REST request to delete ShoppingList : {}", id);
        shoppingListService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
