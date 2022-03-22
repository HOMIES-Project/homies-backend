package com.homies.app.web.rest;

import com.homies.app.domain.Spending;
import com.homies.app.repository.SpendingRepository;
import com.homies.app.service.SpendingQueryService;
import com.homies.app.service.SpendingService;
import com.homies.app.service.criteria.SpendingCriteria;
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

import static com.homies.app.config.Constants.CROSS_ORIGIN;

/**
 * REST controller for managing {@link com.homies.app.domain.Spending}.
 */
@CrossOrigin(origins = CROSS_ORIGIN, maxAge = 3600)
@RestController
@RequestMapping("/api")
public class SpendingResource {

    private final Logger log = LoggerFactory.getLogger(SpendingResource.class);

    private static final String ENTITY_NAME = "spending";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SpendingService spendingService;

    private final SpendingRepository spendingRepository;

    private final SpendingQueryService spendingQueryService;

    public SpendingResource(
        SpendingService spendingService,
        SpendingRepository spendingRepository,
        SpendingQueryService spendingQueryService
    ) {
        this.spendingService = spendingService;
        this.spendingRepository = spendingRepository;
        this.spendingQueryService = spendingQueryService;
    }

    /**
     * {@code POST  /spendings} : Create a new spending.
     *
     * @param spending the spending to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new spending, or with status {@code 400 (Bad Request)} if the spending has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/spendings")
    public ResponseEntity<Spending> createSpending(@Valid @RequestBody Spending spending) throws URISyntaxException {
        log.debug("REST request to save Spending : {}", spending);
        if (spending.getId() != null) {
            throw new BadRequestAlertException("A new spending cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Spending result = spendingService.save(spending);
        return ResponseEntity
            .created(new URI("/api/spendings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /spendings/:id} : Updates an existing spending.
     *
     * @param id the id of the spending to save.
     * @param spending the spending to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated spending,
     * or with status {@code 400 (Bad Request)} if the spending is not valid,
     * or with status {@code 500 (Internal Server Error)} if the spending couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/spendings/{id}")
    public ResponseEntity<Spending> updateSpending(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Spending spending
    ) throws URISyntaxException {
        log.debug("REST request to update Spending : {}, {}", id, spending);
        if (spending.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, spending.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!spendingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Spending result = spendingService.save(spending);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, spending.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /spendings/:id} : Partial updates given fields of an existing spending, field will ignore if it is null
     *
     * @param id the id of the spending to save.
     * @param spending the spending to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated spending,
     * or with status {@code 400 (Bad Request)} if the spending is not valid,
     * or with status {@code 404 (Not Found)} if the spending is not found,
     * or with status {@code 500 (Internal Server Error)} if the spending couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/spendings/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Spending> partialUpdateSpending(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Spending spending
    ) throws URISyntaxException {
        log.debug("REST request to partial update Spending partially : {}, {}", id, spending);
        if (spending.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, spending.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!spendingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Spending> result = spendingService.partialUpdate(spending);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, spending.getId().toString())
        );
    }

    /**
     * {@code GET  /spendings} : get all the spendings.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of spendings in body.
     */
    @GetMapping("/spendings")
    public ResponseEntity<List<Spending>> getAllSpendings(
        SpendingCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Spendings by criteria: {}", criteria);
        Page<Spending> page = spendingQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /spendings/count} : count all the spendings.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/spendings/count")
    public ResponseEntity<Long> countSpendings(SpendingCriteria criteria) {
        log.debug("REST request to count Spendings by criteria: {}", criteria);
        return ResponseEntity.ok().body(spendingQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /spendings/:id} : get the "id" spending.
     *
     * @param id the id of the spending to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the spending, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/spendings/{id}")
    public ResponseEntity<Spending> getSpending(@PathVariable Long id) {
        log.debug("REST request to get Spending : {}", id);
        Optional<Spending> spending = spendingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(spending);
    }

    /**
     * {@code DELETE  /spendings/:id} : delete the "id" spending.
     *
     * @param id the id of the spending to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/spendings/{id}")
    public ResponseEntity<Void> deleteSpending(@PathVariable Long id) {
        log.debug("REST request to delete Spending : {}", id);
        spendingService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
