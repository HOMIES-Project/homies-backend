package com.homies.app.web.rest;

import com.homies.app.domain.Task;
import com.homies.app.repository.TaskRepository;
import com.homies.app.service.AuxiliarServices.CreateTaskAuxService;
import com.homies.app.service.AuxiliarServices.ManageTaskAuxService;
import com.homies.app.service.TaskQueryService;
import com.homies.app.service.TaskService;
import com.homies.app.service.criteria.TaskCriteria;
import com.homies.app.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.homies.app.web.rest.errors.Group.GroupWasNotSpecifyIdGroup;
import com.homies.app.web.rest.errors.Task.TaskDoesNotExist;
import com.homies.app.web.rest.errors.Task.TaskWasNotSpecifyIdTask;
import com.homies.app.web.rest.errors.Task.TaskWasNotSpecifyUser;
import com.homies.app.web.rest.errors.TaskList.TaskListWasNotSpecifyTaskListId;
import com.homies.app.web.rest.errors.User.UserWasNotSpecifyLogin;
import com.homies.app.web.rest.vm.AddUserToTaskVM;

import com.homies.app.web.rest.vm.CreateTaskVM;
import com.homies.app.web.rest.vm.UpdateTaskVM;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.homies.app.domain.Task}.
 */
@RestController
@RequestMapping("/api")
public class TaskResource {

    private final Logger log = LoggerFactory.getLogger(TaskResource.class);
    private static final String ENTITY_NAME = "task";
    @Value("${jhipster.clientApp.name}")
    private String applicationName;
    @Autowired
    private final TaskService taskService;
    @Autowired
    private final TaskRepository taskRepository;
    @Autowired
    private final TaskQueryService taskQueryService;
    @Autowired
    private final CreateTaskAuxService createTaskAuxService;
    @Autowired
    private final ManageTaskAuxService manageTaskAuxService;
    @Autowired
    private final CacheManager cacheManager;
    public TaskResource(TaskService taskService,
                        TaskRepository taskRepository,
                        TaskQueryService taskQueryService,
                        CreateTaskAuxService createTaskAuxService,
                        ManageTaskAuxService manageTaskAuxService,
                        CacheManager cacheManager) {
        this.taskService = taskService;
        this.taskRepository = taskRepository;
        this.taskQueryService = taskQueryService;
        this.createTaskAuxService = createTaskAuxService;
        this.manageTaskAuxService = manageTaskAuxService;
        this.cacheManager = cacheManager;
    }

    /**
     * {@code POST  /tasks} : Create a new task.
     *
     * @param task the task to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new task, or with status {@code 400 (Bad Request)} if the task has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tasks")
    public ResponseEntity<Task> createTask(@Valid @RequestBody CreateTaskVM task) throws URISyntaxException {
        log.warn("REST request to save Task : {}", task.toString());

        Task newTask = createTaskAuxService.createNewTask(task);

        if (newTask != null)
            return new ResponseEntity<>(newTask, HttpStatus.CREATED);

        clearCache();

        return ResponseEntity
            .created(new URI("/api/tasks/" + newTask.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, newTask.getId().toString()))
            .body(newTask);
    }

    private void clearCache() {
        for(String name:cacheManager.getCacheNames()){
            Objects.requireNonNull(cacheManager.getCache(name)).clear();            // clear cache by name
        }
    }

    /** make it posibble to add user to task
     *
     * @param addUserToTaskVM parameters to change
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated group,
     * @throws URISyntaxException
     */
    @PostMapping("/tasks/add-user")
    public ResponseEntity<Task> addUserToTask(@Valid @RequestBody AddUserToTaskVM addUserToTaskVM)
    throws URISyntaxException{
        log.warn("REST request to add user to task : {}", addUserToTaskVM.toString());
        if(addUserToTaskVM.getIdTask() == null){
            throw new TaskWasNotSpecifyIdTask();
        }
        if(addUserToTaskVM.getLogin().isEmpty()){
            throw new UserWasNotSpecifyLogin();
        }
        if(addUserToTaskVM.getIdList() == null){
            throw new TaskListWasNotSpecifyTaskListId();
        }

        Optional<Task> result = manageTaskAuxService.addUserToTask(addUserToTaskVM);

        clearCache();

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.get().getUserAssigneds().toString())
        );
    };

    @PostMapping("/task/delete-user")
    public ResponseEntity<Task> deleteUserToTask(@Valid @RequestBody AddUserToTaskVM addUserToTaskVM)
        throws URISyntaxException{
        log.warn("REST request to delete user to task : {}", addUserToTaskVM.toString());
        if(addUserToTaskVM.getIdTask() == null){
            throw new TaskWasNotSpecifyIdTask();
        }
        if(addUserToTaskVM.getLogin().isEmpty()){
            throw new UserWasNotSpecifyLogin();
        }
        if(addUserToTaskVM.getIdList() == null){
            throw new TaskListWasNotSpecifyTaskListId();
        }

        Optional<Task> result = manageTaskAuxService.deleteUserToTask(addUserToTaskVM);

        clearCache();

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.get().getUserAssigneds().toString())
        );
    }
    @PutMapping("/tasks/update-tasks")
    public ResponseEntity<Task> updateTask(@Valid @RequestBody UpdateTaskVM updateTaskVM)
        throws URISyntaxException {
        log.warn("REST request to update task : {}", updateTaskVM.toString());
        if (updateTaskVM.getIdTask() == null) {
            throw new TaskWasNotSpecifyIdTask();
        }
        if (updateTaskVM.getIdGroup() == null) {
            throw new GroupWasNotSpecifyIdGroup();
        }
        if (updateTaskVM.getLogin() == null) {
            throw new UserWasNotSpecifyLogin();
        }

        Optional<Task> result = manageTaskAuxService.updateTask(updateTaskVM);

        clearCache();

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.get().getTaskName())
        );
    }

    @PutMapping("/tasks/cancel")
    public ResponseEntity<Task> updateTaskCancel(@Valid @RequestBody UpdateTaskVM updateTaskVM)
        throws URISyntaxException {
        log.warn("REST request to update task : {}", updateTaskVM.toString());
        if (updateTaskVM.getIdTask() == null) {
            throw new TaskWasNotSpecifyIdTask();
        }
        if (updateTaskVM.getIdGroup() == null) {
            throw new GroupWasNotSpecifyIdGroup();
        }
        if (updateTaskVM.getLogin() == null) {
            throw new UserWasNotSpecifyLogin();
        }

        Optional<Task> result = manageTaskAuxService.updateTaskCancel(updateTaskVM);

        clearCache();

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.get().getTaskName())
        );
    }

    /**
     * {@code PATCH  /tasks/:id} : Partial updates given fields of an existing task, field will ignore if it is null
     *
     * @param id the id of the task to save.
     * @param task the task to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated task,
     * or with status {@code 400 (Bad Request)} if the task is not valid,
     * or with status {@code 404 (Not Found)} if the task is not found,
     * or with status {@code 500 (Internal Server Error)} if the task couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/tasks/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Task> partialUpdateTask(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Task task
    ) throws URISyntaxException {
        log.warn("REST request to update task : {} with data : {}", id, task.toString());

        if (task.getId() == null) {
            throw new TaskWasNotSpecifyIdTask();
        }
        if (!Objects.equals(id, task.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Task> result = taskService.partialUpdate(task);

        clearCache();

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, task.getId().toString())
        );
    }

    /**
     * {@code GET  /tasks} : get all the tasks.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tasks in body.
     */
    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> getAllTasks(TaskCriteria criteria, @org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.warn("REST request to get all tasks : {}", criteria.toString());
        Page<Task> page = taskQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        clearCache();

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /tasks/count} : count all the tasks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/tasks/count")
    public ResponseEntity<Long> countTasks(TaskCriteria criteria) {
        log.warn("REST request to count tasks : {}", criteria.toString());

        clearCache();

        return ResponseEntity.ok().body(taskQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /tasks/:id} : get the "id" task.
     *
     * @param id the id of the task to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the task, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tasks/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        log.warn("REST request to get task : {}", id);
        Optional<Task> task = taskService.findOne(id);

        clearCache();

        return ResponseUtil.wrapOrNotFound(task);
    }


    /**
     * {@code DELETE  /tasks/:id} : delete the "id" task.
     *
     * @param id the id of the task to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id)
        throws Exception {
        if (id == null){
            throw new TaskWasNotSpecifyIdTask();
        }
        log.warn("REST request to delete task : {}", id);

        manageTaskAuxService.deleteTask(id);

        clearCache();

        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

}
