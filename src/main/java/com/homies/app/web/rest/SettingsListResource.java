package com.homies.app.web.rest;

import com.homies.app.domain.SettingsList;
import com.homies.app.repository.SettingsListRepository;
import com.homies.app.service.SettingsListQueryService;
import com.homies.app.service.SettingsListService;
import com.homies.app.service.criteria.SettingsListCriteria;
import com.homies.app.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
 * REST controller for managing {@link com.homies.app.domain.SettingsList}.
 */
@CrossOrigin(origins = "/localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class SettingsListResource {

    private final Logger log = LoggerFactory.getLogger(SettingsListResource.class);

    private static final String ENTITY_NAME = "settingsList";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SettingsListService settingsListService;

    private final SettingsListRepository settingsListRepository;

    private final SettingsListQueryService settingsListQueryService;

    public SettingsListResource(
        SettingsListService settingsListService,
        SettingsListRepository settingsListRepository,
        SettingsListQueryService settingsListQueryService
    ) {
        this.settingsListService = settingsListService;
        this.settingsListRepository = settingsListRepository;
        this.settingsListQueryService = settingsListQueryService;
    }

    /**
     * {@code POST  /settings-lists} : Create a new settingsList.
     *
     * @param settingsList the settingsList to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new settingsList, or with status {@code 400 (Bad Request)} if the settingsList has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/settings-lists")
    public ResponseEntity<SettingsList> createSettingsList(@RequestBody SettingsList settingsList) throws URISyntaxException {
        log.debug("REST request to save SettingsList : {}", settingsList);
        if (settingsList.getId() != null) {
            throw new BadRequestAlertException("A new settingsList cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SettingsList result = settingsListService.save(settingsList);
        return ResponseEntity
            .created(new URI("/api/settings-lists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /settings-lists/:id} : Updates an existing settingsList.
     *
     * @param id the id of the settingsList to save.
     * @param settingsList the settingsList to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated settingsList,
     * or with status {@code 400 (Bad Request)} if the settingsList is not valid,
     * or with status {@code 500 (Internal Server Error)} if the settingsList couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/settings-lists/{id}")
    public ResponseEntity<SettingsList> updateSettingsList(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SettingsList settingsList
    ) throws URISyntaxException {
        log.debug("REST request to update SettingsList : {}, {}", id, settingsList);
        if (settingsList.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, settingsList.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!settingsListRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SettingsList result = settingsListService.save(settingsList);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, settingsList.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /settings-lists/:id} : Partial updates given fields of an existing settingsList, field will ignore if it is null
     *
     * @param id the id of the settingsList to save.
     * @param settingsList the settingsList to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated settingsList,
     * or with status {@code 400 (Bad Request)} if the settingsList is not valid,
     * or with status {@code 404 (Not Found)} if the settingsList is not found,
     * or with status {@code 500 (Internal Server Error)} if the settingsList couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/settings-lists/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SettingsList> partialUpdateSettingsList(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SettingsList settingsList
    ) throws URISyntaxException {
        log.debug("REST request to partial update SettingsList partially : {}, {}", id, settingsList);
        if (settingsList.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, settingsList.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!settingsListRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SettingsList> result = settingsListService.partialUpdate(settingsList);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, settingsList.getId().toString())
        );
    }

    /**
     * {@code GET  /settings-lists} : get all the settingsLists.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of settingsLists in body.
     */
    @GetMapping("/settings-lists")
    public ResponseEntity<List<SettingsList>> getAllSettingsLists(
        SettingsListCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get SettingsLists by criteria: {}", criteria);
        Page<SettingsList> page = settingsListQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /settings-lists/count} : count all the settingsLists.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/settings-lists/count")
    public ResponseEntity<Long> countSettingsLists(SettingsListCriteria criteria) {
        log.debug("REST request to count SettingsLists by criteria: {}", criteria);
        return ResponseEntity.ok().body(settingsListQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /settings-lists/:id} : get the "id" settingsList.
     *
     * @param id the id of the settingsList to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the settingsList, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/settings-lists/{id}")
    public ResponseEntity<SettingsList> getSettingsList(@PathVariable Long id) {
        log.debug("REST request to get SettingsList : {}", id);
        Optional<SettingsList> settingsList = settingsListService.findOne(id);
        return ResponseUtil.wrapOrNotFound(settingsList);
    }

    /**
     * {@code DELETE  /settings-lists/:id} : delete the "id" settingsList.
     *
     * @param id the id of the settingsList to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/settings-lists/{id}")
    public ResponseEntity<Void> deleteSettingsList(@PathVariable Long id) {
        log.debug("REST request to delete SettingsList : {}", id);
        settingsListService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
