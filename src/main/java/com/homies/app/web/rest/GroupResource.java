package com.homies.app.web.rest;

import com.homies.app.domain.Group;
import com.homies.app.security.AuthoritiesConstants;
import com.homies.app.security.SecurityUtils;
import com.homies.app.service.AuxiliarServices.ManageUserAndGroupsAuxService;
import com.homies.app.service.GroupQueryService;
import com.homies.app.service.GroupService;
import com.homies.app.service.criteria.GroupCriteria;
import com.homies.app.service.AuxiliarServices.CreateGroupsAuxService;
import com.homies.app.web.rest.errors.BadRequestAlertException;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.homies.app.web.rest.errors.Group.GroupWasNotSpecifyIdGroup;
import com.homies.app.web.rest.errors.Group.GroupWasNotSpecifyLogin;
import com.homies.app.web.rest.vm.ManageGroupVM;
import com.homies.app.web.rest.vm.CreateGroupVM;
import com.homies.app.web.rest.vm.UpdateGroupVM;


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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.homies.app.domain.Group}.
 */
@RestController
@RequestMapping("/api")
public class GroupResource {

    private final Logger log = LoggerFactory.getLogger(GroupResource.class);

    private static final String ENTITY_NAME = "homiesGroup";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;
    @Autowired
    private final GroupService groupService;
    @Autowired
    private final GroupQueryService groupQueryService;
    @Autowired
    private final CreateGroupsAuxService createGroupsAux;
    @Autowired
    private final ManageUserAndGroupsAuxService manageUserAndGroupsAuxService;
    @Autowired
    private final CacheManager cacheManager;

    public GroupResource(GroupService groupService,
                         GroupQueryService groupQueryService,
                         CreateGroupsAuxService createGroupsAux,
                         ManageUserAndGroupsAuxService manageUserAndGroupsAuxService,
                         CacheManager cacheManager
    ) {
        this.groupService = groupService;
        this.groupQueryService = groupQueryService;
        this.createGroupsAux = createGroupsAux;
        this.manageUserAndGroupsAuxService = manageUserAndGroupsAuxService;
        this.cacheManager = cacheManager;
    }

    /**
     * {@code POST  /groups} : Create a new group.
     *
     * @param createGroupVM the group to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new group, or with status {@code 400 (Bad Request)} if the group has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/groups")
    public ResponseEntity<Group> createGroup(@Valid @RequestBody CreateGroupVM createGroupVM) throws URISyntaxException {
        Group newGrop = createGroupsAux.createNewGroup(createGroupVM);

        log.warn("@@@@ Homies::REST request to save Group : {}", createGroupVM.toString());
        if (newGrop != null)
            return new ResponseEntity<>(newGrop, HttpStatus.CREATED);

        clearCache();

        assert false;
        return ResponseEntity
            .created(new URI("/api/groups/" + newGrop.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, newGrop.getId().toString()))
            .body(newGrop);
    }

    /**
     * make it posibble to add user to groups
     *
     * @param manageGroupVM parameters to change
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated group,
     */
    @PostMapping("/groups/add-user")
    public ResponseEntity<Group> addUserToGroup(@Valid @RequestBody ManageGroupVM manageGroupVM)
        throws URISyntaxException, UserPrincipalNotFoundException {

        reviewData(manageGroupVM);
        log.warn("@@@@ Homies::REST request to add user to group : {}", manageGroupVM.toString());

        Optional<Group> result = manageUserAndGroupsAuxService.addUserToGroup(manageGroupVM);

        clearCache();

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.get().getUserData().toString())
        );
    }

    /**
     * make it possible to delete user to groups
     *
     * @param manageGroupVM parameters to change
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated group,
     */
    @PostMapping("/groups/delete-user")
    public ResponseEntity<Group> deleteUserToGroup(
        @Valid @RequestBody ManageGroupVM manageGroupVM) {

        reviewData(manageGroupVM);

        log.warn("@@@@ Homies::REST request to delete user to group : {}", manageGroupVM.toString());
        Optional<Group> result = manageUserAndGroupsAuxService.deleteUserToTheGroup(manageGroupVM);

        clearCache();

        if(manageGroupVM.getLogin().equals(groupService.findOne(manageGroupVM.getIdGroup()).get().getUserAdmin().getUser().getLogin())){
            return ResponseEntity.noContent().build();
        } else {
            return ResponseUtil.wrapOrNotFound(
                result,
                HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.get().getUserData().toString())
            );
        }
    }

    /**
     * make it possible to change userAdmin to group
     *
     * @param manageGroupVM parameters to change
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated group,
     */
    @PostMapping("/groups/change-admin")
    public ResponseEntity<Group> changeUserAdminToGroup(
        @Valid @RequestBody ManageGroupVM manageGroupVM
    ){
        reviewData(manageGroupVM);

        log.warn("@@@@ Homies::REST request to change userAdmin to group : {}", manageGroupVM.toString());
        Optional<Group> result = manageUserAndGroupsAuxService.changeUserAdminOfTheGroup(manageGroupVM);

        clearCache();

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.get().getUserData().toString())
        );
    }

    /**
     * Methode for validations data
     *
     * @param addUser request
     */
    private void reviewData(@Valid @NotNull ManageGroupVM addUser) {
        log.warn("@@@@ Homies::REST request to add user to group : {}", addUser.toString());

        if (addUser.getIdGroup() == null) {
            throw new GroupWasNotSpecifyIdGroup();
        }
        if (addUser.getLogin().isEmpty()) {
            throw new GroupWasNotSpecifyLogin();
        }
    }

    /**
     * {@code PUT  /groups/:id} : Updates an existing group.
     *
     * @param id    the id of the group to save.
     * @param updateGroupVM the group to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated group,
     * or with status {@code 400 (Bad Request)} if the group is not valid,
     * or with status {@code 500 (Internal Server Error)} if the group couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/groups/{id}")
    public ResponseEntity<Group> updateGroup(
        @PathVariable @NotNull Long id,
        @Valid @RequestBody UpdateGroupVM updateGroupVM
    ) throws URISyntaxException {
        updateGroupVM.setIdGroup(id);

        log.warn("@@@@ Homies::REST request to update Group : {}, {}", id, updateGroupVM.toString());
        Optional<Group> result = manageUserAndGroupsAuxService.updateGroup(updateGroupVM);

        clearCache();

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, id.toString())
        );
    }

    /**
     * {@code GET  /groups} : get all the groups. ONLY FOR ADMINS
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of groups in body.
     */
    @GetMapping("/groups")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<List<Group>> getAllGroups(
        GroupCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        Page<Group> page = groupQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        log.warn("@@@@ Homies::REST request to get Groups by criteria: {}", criteria.toString());

        clearCache();

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /groups/count} : count all the groups. ONLY FOR ADMINS
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/groups/count")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Long> countGroups(GroupCriteria criteria) {
        log.warn("@@@@ Homies::REST request to count Groups by criteria: {}", criteria.toString());

        clearCache();

        return ResponseEntity.ok().body(groupQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /groups/:id} : get the "id" group.
     *
     * @param id the id of the group to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the group, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/groups/{id}")
    public ResponseEntity<Group> getGroup(@PathVariable Long id) {
        Optional<Group> group = groupService.findOne(id);

        log.warn("@@@@ Homies::REST request to get Group : {}", id);

        clearCache();

        return ResponseUtil.wrapOrNotFound(group);
    }

    /**
     * {@code DELETE  /groups/:id} : delete the "id" group.
     *
     * @param id the VM of the group to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/groups/{id}")
    public ResponseEntity<Group> deleteGroup(@PathVariable @NotNull Long id) {
        ManageGroupVM manageGroupVM = new ManageGroupVM();
        manageGroupVM.setIdGroup(id);
        manageGroupVM.setLogin(SecurityUtils.getCurrentUserLogin().get());

        log.warn("@@@@ Homies::REST request to delete Group : {}", id);
        Optional<Group> result = manageUserAndGroupsAuxService.deleteGroup(manageGroupVM);

        clearCache();

        if (result.isPresent()) {
            throw new BadRequestAlertException("Group cannot be deleted", ENTITY_NAME, "groupcannotbedeleted");
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    private void clearCache() {
        for(String name:cacheManager.getCacheNames()){
            Objects.requireNonNull(cacheManager.getCache(name)).clear();            // clear cache by name
        }
    }
}
