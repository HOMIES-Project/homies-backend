package com.homies.app.web.rest;

import com.homies.app.domain.UserPending;
import com.homies.app.repository.UserPendingRepository;
import com.homies.app.service.UserPendingQueryService;
import com.homies.app.service.UserPendingService;
import com.homies.app.service.criteria.UserPendingCriteria;
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
 * REST controller for managing {@link com.homies.app.domain.UserPending}.
 */
@CrossOrigin(origins = "/localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class UserPendingResource {

    private final Logger log = LoggerFactory.getLogger(UserPendingResource.class);

    private static final String ENTITY_NAME = "userPending";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserPendingService userPendingService;

    private final UserPendingRepository userPendingRepository;

    private final UserPendingQueryService userPendingQueryService;

    public UserPendingResource(
        UserPendingService userPendingService,
        UserPendingRepository userPendingRepository,
        UserPendingQueryService userPendingQueryService
    ) {
        this.userPendingService = userPendingService;
        this.userPendingRepository = userPendingRepository;
        this.userPendingQueryService = userPendingQueryService;
    }

    /**
     * {@code POST  /user-pendings} : Create a new userPending.
     *
     * @param userPending the userPending to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userPending, or with status {@code 400 (Bad Request)} if the userPending has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-pendings")
    public ResponseEntity<UserPending> createUserPending(@Valid @RequestBody UserPending userPending) throws URISyntaxException {
        log.debug("REST request to save UserPending : {}", userPending);
        if (userPending.getId() != null) {
            throw new BadRequestAlertException("A new userPending cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserPending result = userPendingService.save(userPending);
        return ResponseEntity
            .created(new URI("/api/user-pendings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-pendings/:id} : Updates an existing userPending.
     *
     * @param id the id of the userPending to save.
     * @param userPending the userPending to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userPending,
     * or with status {@code 400 (Bad Request)} if the userPending is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userPending couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-pendings/{id}")
    public ResponseEntity<UserPending> updateUserPending(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserPending userPending
    ) throws URISyntaxException {
        log.debug("REST request to update UserPending : {}, {}", id, userPending);
        if (userPending.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userPending.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userPendingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UserPending result = userPendingService.save(userPending);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userPending.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /user-pendings/:id} : Partial updates given fields of an existing userPending, field will ignore if it is null
     *
     * @param id the id of the userPending to save.
     * @param userPending the userPending to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userPending,
     * or with status {@code 400 (Bad Request)} if the userPending is not valid,
     * or with status {@code 404 (Not Found)} if the userPending is not found,
     * or with status {@code 500 (Internal Server Error)} if the userPending couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-pendings/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserPending> partialUpdateUserPending(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserPending userPending
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserPending partially : {}, {}", id, userPending);
        if (userPending.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userPending.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userPendingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserPending> result = userPendingService.partialUpdate(userPending);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userPending.getId().toString())
        );
    }

    /**
     * {@code GET  /user-pendings} : get all the userPendings.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userPendings in body.
     */
    @GetMapping("/user-pendings")
    public ResponseEntity<List<UserPending>> getAllUserPendings(
        UserPendingCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get UserPendings by criteria: {}", criteria);
        Page<UserPending> page = userPendingQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /user-pendings/count} : count all the userPendings.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/user-pendings/count")
    public ResponseEntity<Long> countUserPendings(UserPendingCriteria criteria) {
        log.debug("REST request to count UserPendings by criteria: {}", criteria);
        return ResponseEntity.ok().body(userPendingQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /user-pendings/:id} : get the "id" userPending.
     *
     * @param id the id of the userPending to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userPending, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-pendings/{id}")
    public ResponseEntity<UserPending> getUserPending(@PathVariable Long id) {
        log.debug("REST request to get UserPending : {}", id);
        Optional<UserPending> userPending = userPendingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userPending);
    }

    /**
     * {@code DELETE  /user-pendings/:id} : delete the "id" userPending.
     *
     * @param id the id of the userPending to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-pendings/{id}")
    public ResponseEntity<Void> deleteUserPending(@PathVariable Long id) {
        log.debug("REST request to delete UserPending : {}", id);
        userPendingService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
