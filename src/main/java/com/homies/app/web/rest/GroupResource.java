package com.homies.app.web.rest;

import com.homies.app.domain.Group;
import com.homies.app.repository.GroupRepository;
import com.homies.app.security.AuthoritiesConstants;
import com.homies.app.security.SecurityUtils;
import com.homies.app.service.AuxiliarServices.ManageUserOfGroupAuxService;
import com.homies.app.service.AuxiliarServices.ManageGroupService;
import com.homies.app.service.GroupQueryService;
import com.homies.app.service.GroupService;
import com.homies.app.service.criteria.GroupCriteria;
import com.homies.app.service.AuxiliarServices.CreateGroupsAuxService;
import com.homies.app.web.rest.errors.BadRequestAlertException;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;

import com.homies.app.web.rest.errors.Group.GroupWasNotSpecifyIdGroup;
import com.homies.app.web.rest.errors.Group.GroupWasNotSpecifyLogin;
import com.homies.app.web.rest.vm.ManageGroupVM;
import com.homies.app.web.rest.vm.CreateGroupVM;
import com.homies.app.web.rest.vm.UpdateGroupVM;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private final GroupRepository groupRepository;
    @Autowired
    private final GroupQueryService groupQueryService;
    @Autowired
    private final CreateGroupsAuxService createGroupsAux;
    @Autowired
    private final ManageUserOfGroupAuxService manageUserOfGroupAuxService;
    @Autowired
    private final ManageGroupService manageGroupService;

    public GroupResource(GroupService groupService,
                         GroupRepository groupRepository,
                         GroupQueryService groupQueryService,
                         CreateGroupsAuxService createGroupsAux,
                         ManageUserOfGroupAuxService manageUserOfGroupAuxService,
                         ManageGroupService manageGroupService) {
        this.groupService = groupService;
        this.groupRepository = groupRepository;
        this.groupQueryService = groupQueryService;
        this.createGroupsAux = createGroupsAux;
        this.manageUserOfGroupAuxService = manageUserOfGroupAuxService;
        this.manageGroupService = manageGroupService;
    }

    /**
     * {@code POST  /groups} : Create a new group.
     *
     * @param group the group to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new group, or with status {@code 400 (Bad Request)} if the group has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/groups")
    public ResponseEntity<Group> createGroup(@Valid @RequestBody CreateGroupVM group) throws URISyntaxException {
        log.debug("REST request to save Group : {}", group);
        Group newGrop = createGroupsAux.createNewGroup(group);

        if (newGrop != null)
            return new ResponseEntity<>(newGrop, HttpStatus.CREATED);

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
     * @throws URISyntaxException,UserPrincipalNotFoundException
     */
    @PostMapping("/groups/add-user")
    public ResponseEntity<Group> addUserToGroup(@Valid @RequestBody ManageGroupVM manageGroupVM)
        throws URISyntaxException, UserPrincipalNotFoundException {

        reviewData(manageGroupVM);

        Optional<Group> result = manageGroupService.addUserToGroup(manageGroupVM);

        /*Optional<Group> result = manageUserOfGroupAuxService.addUserToGroup(manageGroupVM);
         */
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

        Optional<Group> result = manageGroupService.removeUserFromGroup(manageGroupVM);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.get().getUserData().toString())
        );
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
    ) {
        reviewData(manageGroupVM);

        Optional<Group> result = manageGroupService.changeAdminOfGroup(manageGroupVM);

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
        log.warn(addUser.toString());

/*        if (addUser.getIdAdminGroup() == null) {
            throw new GroupWasNotSpecifyId();
        }*/
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
     * @param group the group to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated group,
     * or with status {@code 400 (Bad Request)} if the group is not valid,
     * or with status {@code 500 (Internal Server Error)} if the group couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/groups/{id}")
    public ResponseEntity<Group> updateGroup(
        @PathVariable @NotNull Long id,
        @Valid @RequestBody UpdateGroupVM group
    ) throws URISyntaxException {
        log.debug("REST request to update Group : {}, {}", id, group);
        group.setIdGroup(id);

        ManageGroupVM mg = new ManageGroupVM();
        mg.setIdGroup(id);
        mg.setIdAdminGroup(null);
        mg.setLogin(SecurityUtils.getCurrentUserLogin().get());

        //Group result = manageUserOfGroupAuxService.updateGroup(group);
        Optional<Group> result = manageGroupService.updateGroup(mg, group);

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
        log.debug("REST request to get Groups by criteria: {}", criteria);
        Page<Group> page = groupQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

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
        log.debug("REST request to count Groups by criteria: {}", criteria);

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
        log.debug("REST request to get Group : {}", id);
        Optional<Group> group = groupService.findOne(id);

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
        log.debug("REST request to delete Group : {}", manageGroupVM);

        Optional<Group> result = manageGroupService.deleteGroup(manageGroupVM);

        if (result.isPresent()) {
            throw new BadRequestAlertException("Group cannot be deleted", ENTITY_NAME, "groupcannotbedeleted");
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
