package com.homies.app.web.rest;

import com.homies.app.domain.UserName;
import com.homies.app.repository.UserNameRepository;
import com.homies.app.service.UserNameQueryService;
import com.homies.app.service.UserNameService;
import com.homies.app.service.criteria.UserNameCriteria;
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
 * REST controller for managing {@link com.homies.app.domain.UserName}.
 */
@RestController
@RequestMapping("/api")
public class UserNameResource {

    private final Logger log = LoggerFactory.getLogger(UserNameResource.class);

    private static final String ENTITY_NAME = "homiesUserName";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserNameService userNameService;

    private final UserNameRepository userNameRepository;

    private final UserNameQueryService userNameQueryService;

    public UserNameResource(
        UserNameService userNameService,
        UserNameRepository userNameRepository,
        UserNameQueryService userNameQueryService
    ) {
        this.userNameService = userNameService;
        this.userNameRepository = userNameRepository;
        this.userNameQueryService = userNameQueryService;
    }

    /**
     * {@code POST  /user-names} : Create a new userName.
     *
     * @param userName the userName to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userName, or with status {@code 400 (Bad Request)} if the userName has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-names")
    public ResponseEntity<UserName> createUserName(@Valid @RequestBody UserName userName) throws URISyntaxException {
        log.debug("REST request to save UserName : {}", userName);
        if (userName.getId() != null) {
            throw new BadRequestAlertException("A new userName cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserName result = userNameService.save(userName);
        return ResponseEntity
            .created(new URI("/api/user-names/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-names/:id} : Updates an existing userName.
     *
     * @param id the id of the userName to save.
     * @param userName the userName to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userName,
     * or with status {@code 400 (Bad Request)} if the userName is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userName couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-names/{id}")
    public ResponseEntity<UserName> updateUserName(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserName userName
    ) throws URISyntaxException {
        log.debug("REST request to update UserName : {}, {}", id, userName);
        if (userName.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userName.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userNameRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UserName result = userNameService.save(userName);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userName.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /user-names/:id} : Partial updates given fields of an existing userName, field will ignore if it is null
     *
     * @param id the id of the userName to save.
     * @param userName the userName to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userName,
     * or with status {@code 400 (Bad Request)} if the userName is not valid,
     * or with status {@code 404 (Not Found)} if the userName is not found,
     * or with status {@code 500 (Internal Server Error)} if the userName couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-names/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserName> partialUpdateUserName(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserName userName
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserName partially : {}, {}", id, userName);
        if (userName.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userName.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userNameRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserName> result = userNameService.partialUpdate(userName);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userName.getId().toString())
        );
    }

    /**
     * {@code GET  /user-names} : get all the userNames.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userNames in body.
     */
    @GetMapping("/user-names")
    public ResponseEntity<List<UserName>> getAllUserNames(
        UserNameCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get UserNames by criteria: {}", criteria);
        Page<UserName> page = userNameQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /user-names/count} : count all the userNames.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/user-names/count")
    public ResponseEntity<Long> countUserNames(UserNameCriteria criteria) {
        log.debug("REST request to count UserNames by criteria: {}", criteria);
        return ResponseEntity.ok().body(userNameQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /user-names/:id} : get the "id" userName.
     *
     * @param id the id of the userName to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userName, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-names/{id}")
    public ResponseEntity<UserName> getUserName(@PathVariable Long id) {
        log.debug("REST request to get UserName : {}", id);
        Optional<UserName> userName = userNameService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userName);
    }

    /**
     * {@code DELETE  /user-names/:id} : delete the "id" userName.
     *
     * @param id the id of the userName to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-names/{id}")
    public ResponseEntity<Void> deleteUserName(@PathVariable Long id) {
        log.debug("REST request to delete UserName : {}", id);
        userNameService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
