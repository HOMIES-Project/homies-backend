package com.homies.app.web.rest;

import com.homies.app.domain.Task;
import com.homies.app.domain.TaskList;
import com.homies.app.repository.TaskListRepository;
import com.homies.app.service.AuxiliarServices.ManageListTaskAuxService;
import com.homies.app.service.TaskListQueryService;
import com.homies.app.service.TaskListService;
import com.homies.app.service.criteria.TaskListCriteria;
import com.homies.app.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.homies.app.web.rest.vm.CreateTaskVM;
import com.homies.app.web.rest.vm.GetGroupTaskListVM;
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
 * REST controller for managing {@link com.homies.app.domain.TaskList}.
 */
@RestController
@RequestMapping("/api")
public class TaskListResource {

    private final Logger log = LoggerFactory.getLogger(TaskListResource.class);

    private static final String ENTITY_NAME = "taskList";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TaskListService taskListService;

    private final TaskListRepository taskListRepository;

    private final TaskListQueryService taskListQueryService;

    private final ManageListTaskAuxService manageListTaskAuxService;

    public TaskListResource(
        TaskListService taskListService,
        TaskListRepository taskListRepository,
        TaskListQueryService taskListQueryService,
        ManageListTaskAuxService manageListTaskAuxService
    ) {
        this.taskListService = taskListService;
        this.taskListRepository = taskListRepository;
        this.taskListQueryService = taskListQueryService;
        this.manageListTaskAuxService = manageListTaskAuxService;
    }

    /**
     * {@code POST  /task-lists} : Create a new taskList.
     *
     * @param taskList the taskList to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new taskList, or with status {@code 400 (Bad Request)} if the taskList has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/task-lists")
    public ResponseEntity<TaskList> createTaskList(@Valid @RequestBody TaskList taskList) throws URISyntaxException {
        log.debug("REST request to save TaskList : {}", taskList);
        if (taskList.getId() != null) {
            throw new BadRequestAlertException("A new taskList cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TaskList result = taskListService.save(taskList);
        return ResponseEntity
            .created(new URI("/api/task-lists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /task-lists/:id} : Updates an existing taskList.
     *
     * @param id the id of the taskList to save.
     * @param taskList the taskList to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taskList,
     * or with status {@code 400 (Bad Request)} if the taskList is not valid,
     * or with status {@code 500 (Internal Server Error)} if the taskList couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/task-lists/{id}")
    public ResponseEntity<TaskList> updateTaskList(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TaskList taskList
    ) throws URISyntaxException {
        log.debug("REST request to update TaskList : {}, {}", id, taskList);
        if (taskList.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskList.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskListRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TaskList result = taskListService.save(taskList);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, taskList.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /task-lists/:id} : Partial updates given fields of an existing taskList, field will ignore if it is null
     *
     * @param id the id of the taskList to save.
     * @param taskList the taskList to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taskList,
     * or with status {@code 400 (Bad Request)} if the taskList is not valid,
     * or with status {@code 404 (Not Found)} if the taskList is not found,
     * or with status {@code 500 (Internal Server Error)} if the taskList couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/task-lists/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TaskList> partialUpdateTaskList(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TaskList taskList
    ) throws URISyntaxException {
        log.debug("REST request to partial update TaskList partially : {}, {}", id, taskList);
        if (taskList.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskList.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskListRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TaskList> result = taskListService.partialUpdate(taskList);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, taskList.getId().toString())
        );
    }

    /**
     * {@code GET  /task-lists} : get all the taskLists.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of taskLists in body.
     */
    @GetMapping("/task-lists")
    public ResponseEntity<List<TaskList>> getAllTaskLists(
        TaskListCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get TaskLists by criteria: {}", criteria);
        Page<TaskList> page = taskListQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /task-lists/count} : count all the taskLists.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/task-lists/count")
    public ResponseEntity<Long> countTaskLists(TaskListCriteria criteria) {
        log.debug("REST request to count TaskLists by criteria: {}", criteria);
        return ResponseEntity.ok().body(taskListQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /task-lists/:id} : get the "id" taskList.
     *
     * @param id the id of the taskList to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the taskList, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/task-lists/{id}")
    public ResponseEntity<TaskList> getTaskList(@PathVariable Long id) {
        if (id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        log.debug("REST request to get TaskList : {}", id);
        Optional<TaskList> result = taskListService.findOne(id);
        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.get().getNameList())
        );
    }

    /*
     * {@code GET  /task-lists-user}
     *
     * @param getGroupTaskListVM the id of the taskList to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the taskList, or with status {@code 404 (Not Found)}.

    @GetMapping("/task-lists-user")
    public ResponseEntity<List<Task>> getTaskListUser(@Valid @RequestBody GetGroupTaskListVM getGroupTaskListVM) {
        List<Task> result = manageListTaskAuxService.getTaskUserTaskList(getGroupTaskListVM);
        return ResponseEntity.ok().body(result);
    }*/

    @GetMapping("/task-lists-user/{id}/{login}")
    public ResponseEntity<List<Task>> getTaskListUser(@PathVariable Long id, @PathVariable String login) {
        List<Task> result = manageListTaskAuxService.getTaskUserTaskList(id, login);
        return ResponseEntity.ok().body(result);
    }

    /**
     * {@code DELETE  /task-lists/:id} : delete the "id" taskList.
     *
     * @param id the id of the taskList to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/task-lists/{id}")
    public ResponseEntity<Void> deleteTaskList(@PathVariable Long id) {
        log.debug("REST request to delete TaskList : {}", id);
        taskListService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
