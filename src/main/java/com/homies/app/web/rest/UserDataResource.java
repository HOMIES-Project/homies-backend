package com.homies.app.web.rest;

import com.homies.app.domain.UserData;
import com.homies.app.repository.UserDataRepository;
import com.homies.app.security.AuthoritiesConstants;
import com.homies.app.service.AuxiliarServices.ManageUserAndGroupsAuxService;
import com.homies.app.service.UserDataQueryService;
import com.homies.app.service.UserDataService;
import com.homies.app.service.criteria.UserDataCriteria;
import com.homies.app.service.AuxiliarServices.UserEditingAuxService;
import com.homies.app.web.rest.errors.BadRequestAlertException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.homies.app.web.rest.vm.UserEditingVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.homies.app.domain.UserData}.
 */
@RestController
@RequestMapping("/api")
public class UserDataResource {

    private final Logger log = LoggerFactory.getLogger(UserDataResource.class);

    private static final String ENTITY_NAME = "homiesUserData";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;
    @Autowired
    private final UserDataService userDataService;
    @Autowired
    private final UserDataRepository userDataRepository;
    @Autowired
    private final UserDataQueryService userDataQueryService;
    @Autowired
    private final UserEditingAuxService userEditingAux;
    @Autowired
    private final ManageUserAndGroupsAuxService manageUserAndGroupsAuxService;
    @Autowired
    private final CacheManager cacheManager;

    public UserDataResource(
        UserDataService userDataService,
        UserDataRepository userDataRepository,
        UserDataQueryService userDataQueryService,
        UserEditingAuxService userEditingAux,
        ManageUserAndGroupsAuxService manageUserAndGroupsAuxService,
        CacheManager cacheManager
    ) {
        this.userDataService = userDataService;
        this.userDataRepository = userDataRepository;
        this.userDataQueryService = userDataQueryService;
        this.userEditingAux = userEditingAux;
        this.manageUserAndGroupsAuxService = manageUserAndGroupsAuxService;
        this.cacheManager = cacheManager;
    }

    /**
     * {@code POST  /user-data} : Create a new userData.
     *
     * @param userData the userData to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userData, or with status {@code 400 (Bad Request)} if the userData has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-data")
    @RequestMapping("/user-data")
    public ResponseEntity<UserData> createUserData(@Valid @RequestBody UserData userData) throws URISyntaxException {
        if (userData.getId() != null) {
            throw new BadRequestAlertException("A new userData cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(userData.getUser())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        log.warn("@@@@ Homies::REST request to save UserData : {}", userData);
        UserData result = userDataService.save(userData);

        for(String name:cacheManager.getCacheNames()){
            Objects.requireNonNull(cacheManager.getCache(name)).clear();            // clear cache by name
        }

        return ResponseEntity
            .created(new URI("/api/user-data/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * EndPoint to update user information (user/userData)
     *
     * @param id for specific user to update
     * @param user VM with data needed
     * @return 200 if the update was ok or 400 if update was not possible.
     */
    @PutMapping("/user-data/{id}")
    @RequestMapping("/user-data/{id}")
    public ResponseEntity<?> editFieldCompleteUser(
        @PathVariable Long id,
        @Valid @RequestBody UserEditingVM user)
        throws URISyntaxException {

        if (id == null) {
            return ResponseEntity.badRequest().body("Error. Was not specify user id.");
        } else {
            user.setId(id);
        }
        if (userDataService.findOne(user.getId()).isEmpty())
            return ResponseEntity.badRequest().body("Error. User not found.");

        log.warn("@@@@ Homies::REST request to update UserData : {}", user.toString());
        UserData updateUser = userEditingAux.updateUser(user);

        for(String name:cacheManager.getCacheNames()){
            Objects.requireNonNull(cacheManager.getCache(name)).clear();            // clear cache by name
        }

        if (updateUser != null) {
            return ResponseEntity.ok().body(updateUser);
        } else {
            return ResponseEntity.badRequest().body("Error. User not update.");
        }
    }




    /**
     * {@code PATCH  /user-data/:id} : Partial updates given fields of an existing userData, field will ignore if it is null
     *
     * @param id the id of the userData to save.
     * @param userData the userData to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userData,
     * or with status {@code 400 (Bad Request)} if the userData is not valid,
     * or with status {@code 404 (Not Found)} if the userData is not found,
     * or with status {@code 500 (Internal Server Error)} if the userData couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-data/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @RequestMapping(value = "/user-data/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserData> partialUpdateUserData(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserData userData
    ) throws URISyntaxException {
        if (userData.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userData.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userDataRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        log.warn("@@@@ Homies::REST request to update UserData : {}", userData.toString());
        Optional<UserData> result = userDataService.partialUpdate(userData);

        for(String name:cacheManager.getCacheNames()){
            Objects.requireNonNull(cacheManager.getCache(name)).clear();            // clear cache by name
        }

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userData.getId().toString())
        );
    }

    /**
     * {@code GET  /user-data} : get all the userData. ONLY FOR ADMINS
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userData in body.
     */
    @GetMapping("/user-data")
    @RequestMapping("/user-data")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<List<UserData>> getAllUserData(
        UserDataCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        Page<UserData> page = userDataQueryService.findByCriteria(criteria, pageable);

        log.warn("@@@@ Homies::REST request to get UserData by criteria: {}", criteria.toString());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        for(String name:cacheManager.getCacheNames()){
            Objects.requireNonNull(cacheManager.getCache(name)).clear();            // clear cache by name
        }

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /user-data/count} : count all the userData.  ONLY FOR ADMINS
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/user-data/count")
    @RequestMapping("/user-data/count")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Long> countUserData(UserDataCriteria criteria) {
        log.warn("@@@@ Homies::REST request to count UserData by criteria: {}", criteria.toString());

        for(String name:cacheManager.getCacheNames()){
            Objects.requireNonNull(cacheManager.getCache(name)).clear();            // clear cache by name
        }

        return ResponseEntity.ok().body(userDataQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /user-data/:id} : get the "id" userData.
     *
     * @param id the id of the userData to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userData, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-data/{id}")
    @RequestMapping("/user-data/{id}")
    public ResponseEntity<UserData> getUserData(@PathVariable Long id) {
        Optional<UserData> userData = userDataService.findOne(id);

        log.warn("@@@@ Homies::REST request to get UserData : {}", id);

        for(String name:cacheManager.getCacheNames()){
            Objects.requireNonNull(cacheManager.getCache(name)).clear();            // clear cache by name
        }

        return ResponseUtil.wrapOrNotFound(userData);
    }

    /**
     * {@code DELETE  /user-data/:id} : delete the "id" userData.
     *
     * @param id the id of the userData to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-data/{id}")
    @RequestMapping("/user-data/{id}")
    public ResponseEntity<Void> deleteUserData(
        @PathVariable Long id
    ) throws Exception {
        if (id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        log.debug("REST request to delete UserData : {}", id);
        manageUserAndGroupsAuxService.deleteUserAndRelationships(id);

        for(String name:cacheManager.getCacheNames()){
            Objects.requireNonNull(cacheManager.getCache(name)).clear();            // clear cache by name
        }

        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
