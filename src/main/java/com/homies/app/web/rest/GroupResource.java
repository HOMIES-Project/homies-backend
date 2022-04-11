package com.homies.app.web.rest;

import com.homies.app.domain.Group;
import com.homies.app.repository.GroupRepository;
import com.homies.app.service.AuxiliarServices.ManageUserOfGroupAuxService;
import com.homies.app.service.GroupQueryService;
import com.homies.app.service.GroupService;
import com.homies.app.service.criteria.GroupCriteria;
import com.homies.app.service.AuxiliarServices.CreateGroupsAuxService;
import com.homies.app.web.rest.errors.BadRequestAlertException;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.homies.app.web.rest.errors.Group.GroupWasNotSpecifyId;
import com.homies.app.web.rest.errors.Group.GroupWasNotSpecifyIdGroup;
import com.homies.app.web.rest.errors.Group.GroupWasNotSpecifyLogin;
import com.homies.app.web.rest.vm.AddUserToGroupVM;
import com.homies.app.web.rest.errors.GroupAlreadyUsedException;
import com.homies.app.web.rest.vm.CreateGroupVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
 * REST controller for managing {@link com.homies.app.domain.Group}.
 */
@RestController
@RequestMapping("/api")
public class GroupResource {

    private final Logger log = LoggerFactory.getLogger(GroupResource.class);

    private static final String ENTITY_NAME = "homiesGroup";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GroupService groupService;

    private final GroupRepository groupRepository;

    private final GroupQueryService groupQueryService;

    private final CreateGroupsAuxService createGroupsAux;

    private final ManageUserOfGroupAuxService addUserToGroupAuxService;

    public GroupResource(GroupService groupService,
                         GroupRepository groupRepository,
                         GroupQueryService groupQueryService,
                         CreateGroupsAuxService createGroupsAux,
                         ManageUserOfGroupAuxService addUserToGroupAuxService) {
        this.groupService = groupService;
        this.groupRepository = groupRepository;
        this.groupQueryService = groupQueryService;
        this.createGroupsAux = createGroupsAux;
        this.addUserToGroupAuxService = addUserToGroupAuxService;
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

        throw new GroupAlreadyUsedException();
    }

    /** make it posibble to add user to groups
     *
     * @param addUser parameters to change
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated group,
     * @throws URISyntaxException
     */
    @PostMapping("/groups/add-user")
    public ResponseEntity<Group> addUserToGroup(@Valid @RequestBody AddUserToGroupVM addUser)
        throws URISyntaxException{

        log.warn(addUser.toString());
        if (addUser.getIdAdminGroup() == null) {
            throw new GroupWasNotSpecifyId();
        }
        log.warn("################ => hay user admin");
        if (addUser.getIdGroup() == null) {
            throw  new GroupWasNotSpecifyIdGroup();
        }
        log.warn("################ => hay grupo");
        if (addUser.getLogin().isEmpty()) {
            throw new GroupWasNotSpecifyLogin();
        }

        log.warn("################ => Crear grupo");
        Optional<Group> result = addUserToGroupAuxService.addUserToGroup(addUser);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.get().getUserData().toString())
        );
    }

    /** make it possible to delete user to groups
     *
     * @param addUser parameters to change
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated group,
     * @throws URISyntaxException
     */
    @PostMapping("/groups/delete-user")
    public ResponseEntity<Group> deleteUserToGroup(@Valid @RequestBody AddUserToGroupVM addUser)
        throws URISyntaxException{

        log.warn(addUser.toString());
        if (addUser.getIdAdminGroup() == null) {
            throw new GroupWasNotSpecifyId();
        }
        log.warn("################ => hay user admin");
        if (addUser.getIdGroup() == null) {
            throw new GroupWasNotSpecifyIdGroup();
        }
        log.warn("################ => hay grupo");
        if (addUser.getLogin().isEmpty()) {
            throw new GroupWasNotSpecifyLogin();
        }

        log.warn("################ => Crear grupo");
        Optional<Group> result = addUserToGroupAuxService.deleteUserToTheGroup(addUser);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.get().getUserData().toString())
        );
    }

    /** make it possible to change userAdmin to group
     *
     * @param addUser parameters to change
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated group,
     * @throws URISyntaxException
     */
    @PostMapping("/groups/change-admin")
    public ResponseEntity<Group> changeUserAdminToGroup(@Valid @RequestBody AddUserToGroupVM addUser)
        throws URISyntaxException{

        log.warn(addUser.toString());
        if (addUser.getIdAdminGroup() == null) {
            throw new GroupWasNotSpecifyId();
        }
        log.warn("################ => hay user admin");
        if (addUser.getIdGroup() == null) {
            throw new GroupWasNotSpecifyIdGroup();
        }
        log.warn("################ => hay grupo");
        if (addUser.getLogin().isEmpty()) {
            throw new GroupWasNotSpecifyLogin();
        }

        log.warn("################ => Crear grupo");
        Optional<Group> result = addUserToGroupAuxService.changeUserAdminOfTheGroup(addUser);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.get().getUserData().toString())
        );
    }

    /**
     * {@code PUT  /groups/:id} : Updates an existing group.
     *
     * @param id the id of the group to save.
     * @param group the group to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated group,
     * or with status {@code 400 (Bad Request)} if the group is not valid,
     * or with status {@code 500 (Internal Server Error)} if the group couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/groups/{id}")
    public ResponseEntity<Group> updateGroup(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Group group)
        throws URISyntaxException {
        log.debug("REST request to update Group : {}, {}", id, group);
        if (group.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, group.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        if (!groupRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Group result = groupService.save(group);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, group.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /groups/:id} : Partial updates given fields of an existing group, field will ignore if it is null
     *
     * @param id the id of the group to save.
     * @param group the group to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated group,
     * or with status {@code 400 (Bad Request)} if the group is not valid,
     * or with status {@code 404 (Not Found)} if the group is not found,
     * or with status {@code 500 (Internal Server Error)} if the group couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/groups/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Group> partialUpdateGroup(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Group group
    ) throws URISyntaxException {
        log.debug("REST request to partial update Group partially : {}, {}", id, group);
        if (group.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, group.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!groupRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Group> result = groupService.partialUpdate(group);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, group.getId().toString())
        );
    }



    /**
     * {@code GET  /groups} : get all the groups.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of groups in body.
     */
    @GetMapping("/groups")
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
     * {@code GET  /groups/count} : count all the groups.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/groups/count")
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
     * @param id the id of the group to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/groups/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        log.debug("REST request to delete Group : {}", id);
        groupService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
