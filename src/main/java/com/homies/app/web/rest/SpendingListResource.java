package com.homies.app.web.rest;

import com.homies.app.domain.SpendingList;
import com.homies.app.repository.SpendingListRepository;
import com.homies.app.service.SpendingListQueryService;
import com.homies.app.service.SpendingListService;
import com.homies.app.service.criteria.SpendingListCriteria;
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
 * REST controller for managing {@link com.homies.app.domain.SpendingList}.
 */
@RestController
@RequestMapping("/api")
public class SpendingListResource {

    private final Logger log = LoggerFactory.getLogger(SpendingListResource.class);

    private static final String ENTITY_NAME = "spendingList";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SpendingListService spendingListService;

    private final SpendingListRepository spendingListRepository;

    private final SpendingListQueryService spendingListQueryService;

    public SpendingListResource(
        SpendingListService spendingListService,
        SpendingListRepository spendingListRepository,
        SpendingListQueryService spendingListQueryService
    ) {
        this.spendingListService = spendingListService;
        this.spendingListRepository = spendingListRepository;
        this.spendingListQueryService = spendingListQueryService;
    }

    /**
     * {@code POST  /spending-lists} : Create a new spendingList.
     *
     * @param spendingList the spendingList to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new spendingList, or with status {@code 400 (Bad Request)} if the spendingList has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/spending-lists")
    public ResponseEntity<SpendingList> createSpendingList(@Valid @RequestBody SpendingList spendingList) throws URISyntaxException {
        log.debug("REST request to save SpendingList : {}", spendingList);
        if (spendingList.getId() != null) {
            throw new BadRequestAlertException("A new spendingList cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SpendingList result = spendingListService.save(spendingList);
        return ResponseEntity
            .created(new URI("/api/spending-lists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /spending-lists/:id} : Updates an existing spendingList.
     *
     * @param id the id of the spendingList to save.
     * @param spendingList the spendingList to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated spendingList,
     * or with status {@code 400 (Bad Request)} if the spendingList is not valid,
     * or with status {@code 500 (Internal Server Error)} if the spendingList couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/spending-lists/{id}")
    public ResponseEntity<SpendingList> updateSpendingList(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SpendingList spendingList
    ) throws URISyntaxException {
        log.debug("REST request to update SpendingList : {}, {}", id, spendingList);
        if (spendingList.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, spendingList.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!spendingListRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SpendingList result = spendingListService.save(spendingList);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, spendingList.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /spending-lists/:id} : Partial updates given fields of an existing spendingList, field will ignore if it is null
     *
     * @param id the id of the spendingList to save.
     * @param spendingList the spendingList to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated spendingList,
     * or with status {@code 400 (Bad Request)} if the spendingList is not valid,
     * or with status {@code 404 (Not Found)} if the spendingList is not found,
     * or with status {@code 500 (Internal Server Error)} if the spendingList couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/spending-lists/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SpendingList> partialUpdateSpendingList(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SpendingList spendingList
    ) throws URISyntaxException {
        log.debug("REST request to partial update SpendingList partially : {}, {}", id, spendingList);
        if (spendingList.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, spendingList.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!spendingListRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SpendingList> result = spendingListService.partialUpdate(spendingList);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, spendingList.getId().toString())
        );
    }

    /**
     * {@code GET  /spending-lists} : get all the spendingLists.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of spendingLists in body.
     */
    @GetMapping("/spending-lists")
    public ResponseEntity<List<SpendingList>> getAllSpendingLists(
        SpendingListCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get SpendingLists by criteria: {}", criteria);
        Page<SpendingList> page = spendingListQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /spending-lists/count} : count all the spendingLists.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/spending-lists/count")
    public ResponseEntity<Long> countSpendingLists(SpendingListCriteria criteria) {
        log.debug("REST request to count SpendingLists by criteria: {}", criteria);
        return ResponseEntity.ok().body(spendingListQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /spending-lists/:id} : get the "id" spendingList.
     *
     * @param id the id of the spendingList to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the spendingList, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/spending-lists/{id}")
    public ResponseEntity<SpendingList> getSpendingList(@PathVariable Long id) {
        log.debug("REST request to get SpendingList : {}", id);
        Optional<SpendingList> spendingList = spendingListService.findOne(id);
        return ResponseUtil.wrapOrNotFound(spendingList);
    }

    /**
     * {@code DELETE  /spending-lists/:id} : delete the "id" spendingList.
     *
     * @param id the id of the spendingList to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/spending-lists/{id}")
    public ResponseEntity<Void> deleteSpendingList(@PathVariable Long id) {
        log.debug("REST request to delete SpendingList : {}", id);
        spendingListService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
