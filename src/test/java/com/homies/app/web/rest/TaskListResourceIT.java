package com.homies.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.homies.app.IntegrationTest;
import com.homies.app.domain.TaskList;
import com.homies.app.repository.TaskListRepository;
import com.homies.app.service.criteria.TaskListCriteria;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TaskListResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TaskListResourceIT {

    private static final String DEFAULT_NAME_LIST = "AAAAAAAAAA";
    private static final String UPDATED_NAME_LIST = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/task-lists";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TaskListRepository taskListRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTaskListMockMvc;

    private TaskList taskList;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaskList createEntity(EntityManager em) {
        TaskList taskList = new TaskList().nameList(DEFAULT_NAME_LIST);
        return taskList;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaskList createUpdatedEntity(EntityManager em) {
        TaskList taskList = new TaskList().nameList(UPDATED_NAME_LIST);
        return taskList;
    }

    @BeforeEach
    public void initTest() {
        taskList = createEntity(em);
    }

    @Test
    @Transactional
    void createTaskList() throws Exception {
        int databaseSizeBeforeCreate = taskListRepository.findAll().size();
        // Create the TaskList
        restTaskListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taskList)))
            .andExpect(status().isCreated());

        // Validate the TaskList in the database
        List<TaskList> taskListList = taskListRepository.findAll();
        assertThat(taskListList).hasSize(databaseSizeBeforeCreate + 1);
        TaskList testTaskList = taskListList.get(taskListList.size() - 1);
        assertThat(testTaskList.getNameList()).isEqualTo(DEFAULT_NAME_LIST);
    }

    @Test
    @Transactional
    void createTaskListWithExistingId() throws Exception {
        // Create the TaskList with an existing ID
        taskList.setId(1L);

        int databaseSizeBeforeCreate = taskListRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTaskListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taskList)))
            .andExpect(status().isBadRequest());

        // Validate the TaskList in the database
        List<TaskList> taskListList = taskListRepository.findAll();
        assertThat(taskListList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTaskLists() throws Exception {
        // Initialize the database
        taskListRepository.saveAndFlush(taskList);

        // Get all the taskListList
        restTaskListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(taskList.getId().intValue())))
            .andExpect(jsonPath("$.[*].nameList").value(hasItem(DEFAULT_NAME_LIST)));
    }

    @Test
    @Transactional
    void getTaskList() throws Exception {
        // Initialize the database
        taskListRepository.saveAndFlush(taskList);

        // Get the taskList
        restTaskListMockMvc
            .perform(get(ENTITY_API_URL_ID, taskList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(taskList.getId().intValue()))
            .andExpect(jsonPath("$.nameList").value(DEFAULT_NAME_LIST));
    }

    @Test
    @Transactional
    void getTaskListsByIdFiltering() throws Exception {
        // Initialize the database
        taskListRepository.saveAndFlush(taskList);

        Long id = taskList.getId();

        defaultTaskListShouldBeFound("id.equals=" + id);
        defaultTaskListShouldNotBeFound("id.notEquals=" + id);

        defaultTaskListShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTaskListShouldNotBeFound("id.greaterThan=" + id);

        defaultTaskListShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTaskListShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTaskListsByNameListIsEqualToSomething() throws Exception {
        // Initialize the database
        taskListRepository.saveAndFlush(taskList);

        // Get all the taskListList where nameList equals to DEFAULT_NAME_LIST
        defaultTaskListShouldBeFound("nameList.equals=" + DEFAULT_NAME_LIST);

        // Get all the taskListList where nameList equals to UPDATED_NAME_LIST
        defaultTaskListShouldNotBeFound("nameList.equals=" + UPDATED_NAME_LIST);
    }

    @Test
    @Transactional
    void getAllTaskListsByNameListIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskListRepository.saveAndFlush(taskList);

        // Get all the taskListList where nameList not equals to DEFAULT_NAME_LIST
        defaultTaskListShouldNotBeFound("nameList.notEquals=" + DEFAULT_NAME_LIST);

        // Get all the taskListList where nameList not equals to UPDATED_NAME_LIST
        defaultTaskListShouldBeFound("nameList.notEquals=" + UPDATED_NAME_LIST);
    }

    @Test
    @Transactional
    void getAllTaskListsByNameListIsInShouldWork() throws Exception {
        // Initialize the database
        taskListRepository.saveAndFlush(taskList);

        // Get all the taskListList where nameList in DEFAULT_NAME_LIST or UPDATED_NAME_LIST
        defaultTaskListShouldBeFound("nameList.in=" + DEFAULT_NAME_LIST + "," + UPDATED_NAME_LIST);

        // Get all the taskListList where nameList equals to UPDATED_NAME_LIST
        defaultTaskListShouldNotBeFound("nameList.in=" + UPDATED_NAME_LIST);
    }

    @Test
    @Transactional
    void getAllTaskListsByNameListIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskListRepository.saveAndFlush(taskList);

        // Get all the taskListList where nameList is not null
        defaultTaskListShouldBeFound("nameList.specified=true");

        // Get all the taskListList where nameList is null
        defaultTaskListShouldNotBeFound("nameList.specified=false");
    }

    @Test
    @Transactional
    void getAllTaskListsByNameListContainsSomething() throws Exception {
        // Initialize the database
        taskListRepository.saveAndFlush(taskList);

        // Get all the taskListList where nameList contains DEFAULT_NAME_LIST
        defaultTaskListShouldBeFound("nameList.contains=" + DEFAULT_NAME_LIST);

        // Get all the taskListList where nameList contains UPDATED_NAME_LIST
        defaultTaskListShouldNotBeFound("nameList.contains=" + UPDATED_NAME_LIST);
    }

    @Test
    @Transactional
    void getAllTaskListsByNameListNotContainsSomething() throws Exception {
        // Initialize the database
        taskListRepository.saveAndFlush(taskList);

        // Get all the taskListList where nameList does not contain DEFAULT_NAME_LIST
        defaultTaskListShouldNotBeFound("nameList.doesNotContain=" + DEFAULT_NAME_LIST);

        // Get all the taskListList where nameList does not contain UPDATED_NAME_LIST
        defaultTaskListShouldBeFound("nameList.doesNotContain=" + UPDATED_NAME_LIST);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTaskListShouldBeFound(String filter) throws Exception {
        restTaskListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(taskList.getId().intValue())))
            .andExpect(jsonPath("$.[*].nameList").value(hasItem(DEFAULT_NAME_LIST)));

        // Check, that the count call also returns 1
        restTaskListMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTaskListShouldNotBeFound(String filter) throws Exception {
        restTaskListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTaskListMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTaskList() throws Exception {
        // Get the taskList
        restTaskListMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTaskList() throws Exception {
        // Initialize the database
        taskListRepository.saveAndFlush(taskList);

        int databaseSizeBeforeUpdate = taskListRepository.findAll().size();

        // Update the taskList
        TaskList updatedTaskList = taskListRepository.findById(taskList.getId()).get();
        // Disconnect from session so that the updates on updatedTaskList are not directly saved in db
        em.detach(updatedTaskList);
        updatedTaskList.nameList(UPDATED_NAME_LIST);

        restTaskListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTaskList.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTaskList))
            )
            .andExpect(status().isOk());

        // Validate the TaskList in the database
        List<TaskList> taskListList = taskListRepository.findAll();
        assertThat(taskListList).hasSize(databaseSizeBeforeUpdate);
        TaskList testTaskList = taskListList.get(taskListList.size() - 1);
        assertThat(testTaskList.getNameList()).isEqualTo(UPDATED_NAME_LIST);
    }

    @Test
    @Transactional
    void putNonExistingTaskList() throws Exception {
        int databaseSizeBeforeUpdate = taskListRepository.findAll().size();
        taskList.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taskList.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taskList))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskList in the database
        List<TaskList> taskListList = taskListRepository.findAll();
        assertThat(taskListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTaskList() throws Exception {
        int databaseSizeBeforeUpdate = taskListRepository.findAll().size();
        taskList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taskList))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskList in the database
        List<TaskList> taskListList = taskListRepository.findAll();
        assertThat(taskListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTaskList() throws Exception {
        int databaseSizeBeforeUpdate = taskListRepository.findAll().size();
        taskList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskListMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taskList)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TaskList in the database
        List<TaskList> taskListList = taskListRepository.findAll();
        assertThat(taskListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTaskListWithPatch() throws Exception {
        // Initialize the database
        taskListRepository.saveAndFlush(taskList);

        int databaseSizeBeforeUpdate = taskListRepository.findAll().size();

        // Update the taskList using partial update
        TaskList partialUpdatedTaskList = new TaskList();
        partialUpdatedTaskList.setId(taskList.getId());

        partialUpdatedTaskList.nameList(UPDATED_NAME_LIST);

        restTaskListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTaskList.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTaskList))
            )
            .andExpect(status().isOk());

        // Validate the TaskList in the database
        List<TaskList> taskListList = taskListRepository.findAll();
        assertThat(taskListList).hasSize(databaseSizeBeforeUpdate);
        TaskList testTaskList = taskListList.get(taskListList.size() - 1);
        assertThat(testTaskList.getNameList()).isEqualTo(UPDATED_NAME_LIST);
    }

    @Test
    @Transactional
    void fullUpdateTaskListWithPatch() throws Exception {
        // Initialize the database
        taskListRepository.saveAndFlush(taskList);

        int databaseSizeBeforeUpdate = taskListRepository.findAll().size();

        // Update the taskList using partial update
        TaskList partialUpdatedTaskList = new TaskList();
        partialUpdatedTaskList.setId(taskList.getId());

        partialUpdatedTaskList.nameList(UPDATED_NAME_LIST);

        restTaskListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTaskList.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTaskList))
            )
            .andExpect(status().isOk());

        // Validate the TaskList in the database
        List<TaskList> taskListList = taskListRepository.findAll();
        assertThat(taskListList).hasSize(databaseSizeBeforeUpdate);
        TaskList testTaskList = taskListList.get(taskListList.size() - 1);
        assertThat(testTaskList.getNameList()).isEqualTo(UPDATED_NAME_LIST);
    }

    @Test
    @Transactional
    void patchNonExistingTaskList() throws Exception {
        int databaseSizeBeforeUpdate = taskListRepository.findAll().size();
        taskList.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, taskList.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(taskList))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskList in the database
        List<TaskList> taskListList = taskListRepository.findAll();
        assertThat(taskListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTaskList() throws Exception {
        int databaseSizeBeforeUpdate = taskListRepository.findAll().size();
        taskList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(taskList))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskList in the database
        List<TaskList> taskListList = taskListRepository.findAll();
        assertThat(taskListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTaskList() throws Exception {
        int databaseSizeBeforeUpdate = taskListRepository.findAll().size();
        taskList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskListMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(taskList)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TaskList in the database
        List<TaskList> taskListList = taskListRepository.findAll();
        assertThat(taskListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTaskList() throws Exception {
        // Initialize the database
        taskListRepository.saveAndFlush(taskList);

        int databaseSizeBeforeDelete = taskListRepository.findAll().size();

        // Delete the taskList
        restTaskListMockMvc
            .perform(delete(ENTITY_API_URL_ID, taskList.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TaskList> taskListList = taskListRepository.findAll();
        assertThat(taskListList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
