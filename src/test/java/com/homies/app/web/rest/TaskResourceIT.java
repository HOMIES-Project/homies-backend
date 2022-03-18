package com.homies.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.homies.app.IntegrationTest;
import com.homies.app.domain.Task;
import com.homies.app.domain.TaskList;
import com.homies.app.domain.UserData;
import com.homies.app.repository.TaskRepository;
import com.homies.app.service.criteria.TaskCriteria;
import java.time.LocalDate;
import java.time.ZoneId;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link TaskResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TaskResourceIT {

    private static final String DEFAULT_TASK_NAME = "AAAAAAAAAA";
    private static final String UPDATED_TASK_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATA_CREATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_CREATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATA_CREATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_DATA_END = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_END = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATA_END = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_CANCEL = false;
    private static final Boolean UPDATED_CANCEL = true;

    private static final byte[] DEFAULT_PHOTO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PHOTO = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PHOTO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PHOTO_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_PUNTUACION = "AAAAAAAAAA";
    private static final String UPDATED_PUNTUACION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/tasks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTaskMockMvc;

    private Task task;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Task createEntity(EntityManager em) {
        Task task = new Task()
            .taskName(DEFAULT_TASK_NAME)
            .dataCreate(DEFAULT_DATA_CREATE)
            .dataEnd(DEFAULT_DATA_END)
            .description(DEFAULT_DESCRIPTION)
            .cancel(DEFAULT_CANCEL)
            .photo(DEFAULT_PHOTO)
            .photoContentType(DEFAULT_PHOTO_CONTENT_TYPE)
            .puntuacion(DEFAULT_PUNTUACION);
        return task;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Task createUpdatedEntity(EntityManager em) {
        Task task = new Task()
            .taskName(UPDATED_TASK_NAME)
            .dataCreate(UPDATED_DATA_CREATE)
            .dataEnd(UPDATED_DATA_END)
            .description(UPDATED_DESCRIPTION)
            .cancel(UPDATED_CANCEL)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .puntuacion(UPDATED_PUNTUACION);
        return task;
    }

    @BeforeEach
    public void initTest() {
        task = createEntity(em);
    }

    @Test
    @Transactional
    void createTask() throws Exception {
        int databaseSizeBeforeCreate = taskRepository.findAll().size();
        // Create the Task
        restTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(task)))
            .andExpect(status().isCreated());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeCreate + 1);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getTaskName()).isEqualTo(DEFAULT_TASK_NAME);
        assertThat(testTask.getDataCreate()).isEqualTo(DEFAULT_DATA_CREATE);
        assertThat(testTask.getDataEnd()).isEqualTo(DEFAULT_DATA_END);
        assertThat(testTask.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTask.getCancel()).isEqualTo(DEFAULT_CANCEL);
        assertThat(testTask.getPhoto()).isEqualTo(DEFAULT_PHOTO);
        assertThat(testTask.getPhotoContentType()).isEqualTo(DEFAULT_PHOTO_CONTENT_TYPE);
        assertThat(testTask.getPuntuacion()).isEqualTo(DEFAULT_PUNTUACION);
    }

    @Test
    @Transactional
    void createTaskWithExistingId() throws Exception {
        // Create the Task with an existing ID
        task.setId(1L);

        int databaseSizeBeforeCreate = taskRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(task)))
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTaskNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = taskRepository.findAll().size();
        // set the field null
        task.setTaskName(null);

        // Create the Task, which fails.

        restTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(task)))
            .andExpect(status().isBadRequest());

        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = taskRepository.findAll().size();
        // set the field null
        task.setDescription(null);

        // Create the Task, which fails.

        restTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(task)))
            .andExpect(status().isBadRequest());

        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTasks() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(task.getId().intValue())))
            .andExpect(jsonPath("$.[*].taskName").value(hasItem(DEFAULT_TASK_NAME)))
            .andExpect(jsonPath("$.[*].dataCreate").value(hasItem(DEFAULT_DATA_CREATE.toString())))
            .andExpect(jsonPath("$.[*].dataEnd").value(hasItem(DEFAULT_DATA_END.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].cancel").value(hasItem(DEFAULT_CANCEL.booleanValue())))
            .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64Utils.encodeToString(DEFAULT_PHOTO))))
            .andExpect(jsonPath("$.[*].puntuacion").value(hasItem(DEFAULT_PUNTUACION)));
    }

    @Test
    @Transactional
    void getTask() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get the task
        restTaskMockMvc
            .perform(get(ENTITY_API_URL_ID, task.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(task.getId().intValue()))
            .andExpect(jsonPath("$.taskName").value(DEFAULT_TASK_NAME))
            .andExpect(jsonPath("$.dataCreate").value(DEFAULT_DATA_CREATE.toString()))
            .andExpect(jsonPath("$.dataEnd").value(DEFAULT_DATA_END.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.cancel").value(DEFAULT_CANCEL.booleanValue()))
            .andExpect(jsonPath("$.photoContentType").value(DEFAULT_PHOTO_CONTENT_TYPE))
            .andExpect(jsonPath("$.photo").value(Base64Utils.encodeToString(DEFAULT_PHOTO)))
            .andExpect(jsonPath("$.puntuacion").value(DEFAULT_PUNTUACION));
    }

    @Test
    @Transactional
    void getTasksByIdFiltering() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        Long id = task.getId();

        defaultTaskShouldBeFound("id.equals=" + id);
        defaultTaskShouldNotBeFound("id.notEquals=" + id);

        defaultTaskShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTaskShouldNotBeFound("id.greaterThan=" + id);

        defaultTaskShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTaskShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTasksByTaskNameIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where taskName equals to DEFAULT_TASK_NAME
        defaultTaskShouldBeFound("taskName.equals=" + DEFAULT_TASK_NAME);

        // Get all the taskList where taskName equals to UPDATED_TASK_NAME
        defaultTaskShouldNotBeFound("taskName.equals=" + UPDATED_TASK_NAME);
    }

    @Test
    @Transactional
    void getAllTasksByTaskNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where taskName not equals to DEFAULT_TASK_NAME
        defaultTaskShouldNotBeFound("taskName.notEquals=" + DEFAULT_TASK_NAME);

        // Get all the taskList where taskName not equals to UPDATED_TASK_NAME
        defaultTaskShouldBeFound("taskName.notEquals=" + UPDATED_TASK_NAME);
    }

    @Test
    @Transactional
    void getAllTasksByTaskNameIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where taskName in DEFAULT_TASK_NAME or UPDATED_TASK_NAME
        defaultTaskShouldBeFound("taskName.in=" + DEFAULT_TASK_NAME + "," + UPDATED_TASK_NAME);

        // Get all the taskList where taskName equals to UPDATED_TASK_NAME
        defaultTaskShouldNotBeFound("taskName.in=" + UPDATED_TASK_NAME);
    }

    @Test
    @Transactional
    void getAllTasksByTaskNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where taskName is not null
        defaultTaskShouldBeFound("taskName.specified=true");

        // Get all the taskList where taskName is null
        defaultTaskShouldNotBeFound("taskName.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByTaskNameContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where taskName contains DEFAULT_TASK_NAME
        defaultTaskShouldBeFound("taskName.contains=" + DEFAULT_TASK_NAME);

        // Get all the taskList where taskName contains UPDATED_TASK_NAME
        defaultTaskShouldNotBeFound("taskName.contains=" + UPDATED_TASK_NAME);
    }

    @Test
    @Transactional
    void getAllTasksByTaskNameNotContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where taskName does not contain DEFAULT_TASK_NAME
        defaultTaskShouldNotBeFound("taskName.doesNotContain=" + DEFAULT_TASK_NAME);

        // Get all the taskList where taskName does not contain UPDATED_TASK_NAME
        defaultTaskShouldBeFound("taskName.doesNotContain=" + UPDATED_TASK_NAME);
    }

    @Test
    @Transactional
    void getAllTasksByDataCreateIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where dataCreate equals to DEFAULT_DATA_CREATE
        defaultTaskShouldBeFound("dataCreate.equals=" + DEFAULT_DATA_CREATE);

        // Get all the taskList where dataCreate equals to UPDATED_DATA_CREATE
        defaultTaskShouldNotBeFound("dataCreate.equals=" + UPDATED_DATA_CREATE);
    }

    @Test
    @Transactional
    void getAllTasksByDataCreateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where dataCreate not equals to DEFAULT_DATA_CREATE
        defaultTaskShouldNotBeFound("dataCreate.notEquals=" + DEFAULT_DATA_CREATE);

        // Get all the taskList where dataCreate not equals to UPDATED_DATA_CREATE
        defaultTaskShouldBeFound("dataCreate.notEquals=" + UPDATED_DATA_CREATE);
    }

    @Test
    @Transactional
    void getAllTasksByDataCreateIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where dataCreate in DEFAULT_DATA_CREATE or UPDATED_DATA_CREATE
        defaultTaskShouldBeFound("dataCreate.in=" + DEFAULT_DATA_CREATE + "," + UPDATED_DATA_CREATE);

        // Get all the taskList where dataCreate equals to UPDATED_DATA_CREATE
        defaultTaskShouldNotBeFound("dataCreate.in=" + UPDATED_DATA_CREATE);
    }

    @Test
    @Transactional
    void getAllTasksByDataCreateIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where dataCreate is not null
        defaultTaskShouldBeFound("dataCreate.specified=true");

        // Get all the taskList where dataCreate is null
        defaultTaskShouldNotBeFound("dataCreate.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByDataCreateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where dataCreate is greater than or equal to DEFAULT_DATA_CREATE
        defaultTaskShouldBeFound("dataCreate.greaterThanOrEqual=" + DEFAULT_DATA_CREATE);

        // Get all the taskList where dataCreate is greater than or equal to UPDATED_DATA_CREATE
        defaultTaskShouldNotBeFound("dataCreate.greaterThanOrEqual=" + UPDATED_DATA_CREATE);
    }

    @Test
    @Transactional
    void getAllTasksByDataCreateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where dataCreate is less than or equal to DEFAULT_DATA_CREATE
        defaultTaskShouldBeFound("dataCreate.lessThanOrEqual=" + DEFAULT_DATA_CREATE);

        // Get all the taskList where dataCreate is less than or equal to SMALLER_DATA_CREATE
        defaultTaskShouldNotBeFound("dataCreate.lessThanOrEqual=" + SMALLER_DATA_CREATE);
    }

    @Test
    @Transactional
    void getAllTasksByDataCreateIsLessThanSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where dataCreate is less than DEFAULT_DATA_CREATE
        defaultTaskShouldNotBeFound("dataCreate.lessThan=" + DEFAULT_DATA_CREATE);

        // Get all the taskList where dataCreate is less than UPDATED_DATA_CREATE
        defaultTaskShouldBeFound("dataCreate.lessThan=" + UPDATED_DATA_CREATE);
    }

    @Test
    @Transactional
    void getAllTasksByDataCreateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where dataCreate is greater than DEFAULT_DATA_CREATE
        defaultTaskShouldNotBeFound("dataCreate.greaterThan=" + DEFAULT_DATA_CREATE);

        // Get all the taskList where dataCreate is greater than SMALLER_DATA_CREATE
        defaultTaskShouldBeFound("dataCreate.greaterThan=" + SMALLER_DATA_CREATE);
    }

    @Test
    @Transactional
    void getAllTasksByDataEndIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where dataEnd equals to DEFAULT_DATA_END
        defaultTaskShouldBeFound("dataEnd.equals=" + DEFAULT_DATA_END);

        // Get all the taskList where dataEnd equals to UPDATED_DATA_END
        defaultTaskShouldNotBeFound("dataEnd.equals=" + UPDATED_DATA_END);
    }

    @Test
    @Transactional
    void getAllTasksByDataEndIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where dataEnd not equals to DEFAULT_DATA_END
        defaultTaskShouldNotBeFound("dataEnd.notEquals=" + DEFAULT_DATA_END);

        // Get all the taskList where dataEnd not equals to UPDATED_DATA_END
        defaultTaskShouldBeFound("dataEnd.notEquals=" + UPDATED_DATA_END);
    }

    @Test
    @Transactional
    void getAllTasksByDataEndIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where dataEnd in DEFAULT_DATA_END or UPDATED_DATA_END
        defaultTaskShouldBeFound("dataEnd.in=" + DEFAULT_DATA_END + "," + UPDATED_DATA_END);

        // Get all the taskList where dataEnd equals to UPDATED_DATA_END
        defaultTaskShouldNotBeFound("dataEnd.in=" + UPDATED_DATA_END);
    }

    @Test
    @Transactional
    void getAllTasksByDataEndIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where dataEnd is not null
        defaultTaskShouldBeFound("dataEnd.specified=true");

        // Get all the taskList where dataEnd is null
        defaultTaskShouldNotBeFound("dataEnd.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByDataEndIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where dataEnd is greater than or equal to DEFAULT_DATA_END
        defaultTaskShouldBeFound("dataEnd.greaterThanOrEqual=" + DEFAULT_DATA_END);

        // Get all the taskList where dataEnd is greater than or equal to UPDATED_DATA_END
        defaultTaskShouldNotBeFound("dataEnd.greaterThanOrEqual=" + UPDATED_DATA_END);
    }

    @Test
    @Transactional
    void getAllTasksByDataEndIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where dataEnd is less than or equal to DEFAULT_DATA_END
        defaultTaskShouldBeFound("dataEnd.lessThanOrEqual=" + DEFAULT_DATA_END);

        // Get all the taskList where dataEnd is less than or equal to SMALLER_DATA_END
        defaultTaskShouldNotBeFound("dataEnd.lessThanOrEqual=" + SMALLER_DATA_END);
    }

    @Test
    @Transactional
    void getAllTasksByDataEndIsLessThanSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where dataEnd is less than DEFAULT_DATA_END
        defaultTaskShouldNotBeFound("dataEnd.lessThan=" + DEFAULT_DATA_END);

        // Get all the taskList where dataEnd is less than UPDATED_DATA_END
        defaultTaskShouldBeFound("dataEnd.lessThan=" + UPDATED_DATA_END);
    }

    @Test
    @Transactional
    void getAllTasksByDataEndIsGreaterThanSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where dataEnd is greater than DEFAULT_DATA_END
        defaultTaskShouldNotBeFound("dataEnd.greaterThan=" + DEFAULT_DATA_END);

        // Get all the taskList where dataEnd is greater than SMALLER_DATA_END
        defaultTaskShouldBeFound("dataEnd.greaterThan=" + SMALLER_DATA_END);
    }

    @Test
    @Transactional
    void getAllTasksByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where description equals to DEFAULT_DESCRIPTION
        defaultTaskShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the taskList where description equals to UPDATED_DESCRIPTION
        defaultTaskShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTasksByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where description not equals to DEFAULT_DESCRIPTION
        defaultTaskShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the taskList where description not equals to UPDATED_DESCRIPTION
        defaultTaskShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTasksByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultTaskShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the taskList where description equals to UPDATED_DESCRIPTION
        defaultTaskShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTasksByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where description is not null
        defaultTaskShouldBeFound("description.specified=true");

        // Get all the taskList where description is null
        defaultTaskShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where description contains DEFAULT_DESCRIPTION
        defaultTaskShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the taskList where description contains UPDATED_DESCRIPTION
        defaultTaskShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTasksByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where description does not contain DEFAULT_DESCRIPTION
        defaultTaskShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the taskList where description does not contain UPDATED_DESCRIPTION
        defaultTaskShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTasksByCancelIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where cancel equals to DEFAULT_CANCEL
        defaultTaskShouldBeFound("cancel.equals=" + DEFAULT_CANCEL);

        // Get all the taskList where cancel equals to UPDATED_CANCEL
        defaultTaskShouldNotBeFound("cancel.equals=" + UPDATED_CANCEL);
    }

    @Test
    @Transactional
    void getAllTasksByCancelIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where cancel not equals to DEFAULT_CANCEL
        defaultTaskShouldNotBeFound("cancel.notEquals=" + DEFAULT_CANCEL);

        // Get all the taskList where cancel not equals to UPDATED_CANCEL
        defaultTaskShouldBeFound("cancel.notEquals=" + UPDATED_CANCEL);
    }

    @Test
    @Transactional
    void getAllTasksByCancelIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where cancel in DEFAULT_CANCEL or UPDATED_CANCEL
        defaultTaskShouldBeFound("cancel.in=" + DEFAULT_CANCEL + "," + UPDATED_CANCEL);

        // Get all the taskList where cancel equals to UPDATED_CANCEL
        defaultTaskShouldNotBeFound("cancel.in=" + UPDATED_CANCEL);
    }

    @Test
    @Transactional
    void getAllTasksByCancelIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where cancel is not null
        defaultTaskShouldBeFound("cancel.specified=true");

        // Get all the taskList where cancel is null
        defaultTaskShouldNotBeFound("cancel.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByPuntuacionIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where puntuacion equals to DEFAULT_PUNTUACION
        defaultTaskShouldBeFound("puntuacion.equals=" + DEFAULT_PUNTUACION);

        // Get all the taskList where puntuacion equals to UPDATED_PUNTUACION
        defaultTaskShouldNotBeFound("puntuacion.equals=" + UPDATED_PUNTUACION);
    }

    @Test
    @Transactional
    void getAllTasksByPuntuacionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where puntuacion not equals to DEFAULT_PUNTUACION
        defaultTaskShouldNotBeFound("puntuacion.notEquals=" + DEFAULT_PUNTUACION);

        // Get all the taskList where puntuacion not equals to UPDATED_PUNTUACION
        defaultTaskShouldBeFound("puntuacion.notEquals=" + UPDATED_PUNTUACION);
    }

    @Test
    @Transactional
    void getAllTasksByPuntuacionIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where puntuacion in DEFAULT_PUNTUACION or UPDATED_PUNTUACION
        defaultTaskShouldBeFound("puntuacion.in=" + DEFAULT_PUNTUACION + "," + UPDATED_PUNTUACION);

        // Get all the taskList where puntuacion equals to UPDATED_PUNTUACION
        defaultTaskShouldNotBeFound("puntuacion.in=" + UPDATED_PUNTUACION);
    }

    @Test
    @Transactional
    void getAllTasksByPuntuacionIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where puntuacion is not null
        defaultTaskShouldBeFound("puntuacion.specified=true");

        // Get all the taskList where puntuacion is null
        defaultTaskShouldNotBeFound("puntuacion.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByPuntuacionContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where puntuacion contains DEFAULT_PUNTUACION
        defaultTaskShouldBeFound("puntuacion.contains=" + DEFAULT_PUNTUACION);

        // Get all the taskList where puntuacion contains UPDATED_PUNTUACION
        defaultTaskShouldNotBeFound("puntuacion.contains=" + UPDATED_PUNTUACION);
    }

    @Test
    @Transactional
    void getAllTasksByPuntuacionNotContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where puntuacion does not contain DEFAULT_PUNTUACION
        defaultTaskShouldNotBeFound("puntuacion.doesNotContain=" + DEFAULT_PUNTUACION);

        // Get all the taskList where puntuacion does not contain UPDATED_PUNTUACION
        defaultTaskShouldBeFound("puntuacion.doesNotContain=" + UPDATED_PUNTUACION);
    }

    @Test
    @Transactional
    void getAllTasksByTaskListIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);
        TaskList taskList;
        if (TestUtil.findAll(em, TaskList.class).isEmpty()) {
            taskList = TaskListResourceIT.createEntity(em);
            em.persist(taskList);
            em.flush();
        } else {
            taskList = TestUtil.findAll(em, TaskList.class).get(0);
        }
        em.persist(taskList);
        em.flush();
        task.setTaskList(taskList);
        taskRepository.saveAndFlush(task);
        Long taskListId = taskList.getId();

        // Get all the taskList where taskList equals to taskListId
        defaultTaskShouldBeFound("taskListId.equals=" + taskListId);

        // Get all the taskList where taskList equals to (taskListId + 1)
        defaultTaskShouldNotBeFound("taskListId.equals=" + (taskListId + 1));
    }

    @Test
    @Transactional
    void getAllTasksByUserDataIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);
        UserData userData;
        if (TestUtil.findAll(em, UserData.class).isEmpty()) {
            userData = UserDataResourceIT.createEntity(em);
            em.persist(userData);
            em.flush();
        } else {
            userData = TestUtil.findAll(em, UserData.class).get(0);
        }
        em.persist(userData);
        em.flush();
        task.setUserData(userData);
        taskRepository.saveAndFlush(task);
        Long userDataId = userData.getId();

        // Get all the taskList where userData equals to userDataId
        defaultTaskShouldBeFound("userDataId.equals=" + userDataId);

        // Get all the taskList where userData equals to (userDataId + 1)
        defaultTaskShouldNotBeFound("userDataId.equals=" + (userDataId + 1));
    }

    @Test
    @Transactional
    void getAllTasksByUserCreatorIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);
        UserData userCreator;
        if (TestUtil.findAll(em, UserData.class).isEmpty()) {
            userCreator = UserDataResourceIT.createEntity(em);
            em.persist(userCreator);
            em.flush();
        } else {
            userCreator = TestUtil.findAll(em, UserData.class).get(0);
        }
        em.persist(userCreator);
        em.flush();
        task.setUserCreator(userCreator);
        taskRepository.saveAndFlush(task);
        Long userCreatorId = userCreator.getId();

        // Get all the taskList where userCreator equals to userCreatorId
        defaultTaskShouldBeFound("userCreatorId.equals=" + userCreatorId);

        // Get all the taskList where userCreator equals to (userCreatorId + 1)
        defaultTaskShouldNotBeFound("userCreatorId.equals=" + (userCreatorId + 1));
    }

    @Test
    @Transactional
    void getAllTasksByUserAssignedIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);
        UserData userAssigned;
        if (TestUtil.findAll(em, UserData.class).isEmpty()) {
            userAssigned = UserDataResourceIT.createEntity(em);
            em.persist(userAssigned);
            em.flush();
        } else {
            userAssigned = TestUtil.findAll(em, UserData.class).get(0);
        }
        em.persist(userAssigned);
        em.flush();
        task.addUserAssigned(userAssigned);
        taskRepository.saveAndFlush(task);
        Long userAssignedId = userAssigned.getId();

        // Get all the taskList where userAssigned equals to userAssignedId
        defaultTaskShouldBeFound("userAssignedId.equals=" + userAssignedId);

        // Get all the taskList where userAssigned equals to (userAssignedId + 1)
        defaultTaskShouldNotBeFound("userAssignedId.equals=" + (userAssignedId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTaskShouldBeFound(String filter) throws Exception {
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(task.getId().intValue())))
            .andExpect(jsonPath("$.[*].taskName").value(hasItem(DEFAULT_TASK_NAME)))
            .andExpect(jsonPath("$.[*].dataCreate").value(hasItem(DEFAULT_DATA_CREATE.toString())))
            .andExpect(jsonPath("$.[*].dataEnd").value(hasItem(DEFAULT_DATA_END.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].cancel").value(hasItem(DEFAULT_CANCEL.booleanValue())))
            .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64Utils.encodeToString(DEFAULT_PHOTO))))
            .andExpect(jsonPath("$.[*].puntuacion").value(hasItem(DEFAULT_PUNTUACION)));

        // Check, that the count call also returns 1
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTaskShouldNotBeFound(String filter) throws Exception {
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTask() throws Exception {
        // Get the task
        restTaskMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTask() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        int databaseSizeBeforeUpdate = taskRepository.findAll().size();

        // Update the task
        Task updatedTask = taskRepository.findById(task.getId()).get();
        // Disconnect from session so that the updates on updatedTask are not directly saved in db
        em.detach(updatedTask);
        updatedTask
            .taskName(UPDATED_TASK_NAME)
            .dataCreate(UPDATED_DATA_CREATE)
            .dataEnd(UPDATED_DATA_END)
            .description(UPDATED_DESCRIPTION)
            .cancel(UPDATED_CANCEL)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .puntuacion(UPDATED_PUNTUACION);

        restTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTask.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTask))
            )
            .andExpect(status().isOk());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getTaskName()).isEqualTo(UPDATED_TASK_NAME);
        assertThat(testTask.getDataCreate()).isEqualTo(UPDATED_DATA_CREATE);
        assertThat(testTask.getDataEnd()).isEqualTo(UPDATED_DATA_END);
        assertThat(testTask.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTask.getCancel()).isEqualTo(UPDATED_CANCEL);
        assertThat(testTask.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testTask.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testTask.getPuntuacion()).isEqualTo(UPDATED_PUNTUACION);
    }

    @Test
    @Transactional
    void putNonExistingTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, task.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(task))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(task))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(task)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTaskWithPatch() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        int databaseSizeBeforeUpdate = taskRepository.findAll().size();

        // Update the task using partial update
        Task partialUpdatedTask = new Task();
        partialUpdatedTask.setId(task.getId());

        partialUpdatedTask.dataCreate(UPDATED_DATA_CREATE).description(UPDATED_DESCRIPTION);

        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTask.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTask))
            )
            .andExpect(status().isOk());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getTaskName()).isEqualTo(DEFAULT_TASK_NAME);
        assertThat(testTask.getDataCreate()).isEqualTo(UPDATED_DATA_CREATE);
        assertThat(testTask.getDataEnd()).isEqualTo(DEFAULT_DATA_END);
        assertThat(testTask.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTask.getCancel()).isEqualTo(DEFAULT_CANCEL);
        assertThat(testTask.getPhoto()).isEqualTo(DEFAULT_PHOTO);
        assertThat(testTask.getPhotoContentType()).isEqualTo(DEFAULT_PHOTO_CONTENT_TYPE);
        assertThat(testTask.getPuntuacion()).isEqualTo(DEFAULT_PUNTUACION);
    }

    @Test
    @Transactional
    void fullUpdateTaskWithPatch() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        int databaseSizeBeforeUpdate = taskRepository.findAll().size();

        // Update the task using partial update
        Task partialUpdatedTask = new Task();
        partialUpdatedTask.setId(task.getId());

        partialUpdatedTask
            .taskName(UPDATED_TASK_NAME)
            .dataCreate(UPDATED_DATA_CREATE)
            .dataEnd(UPDATED_DATA_END)
            .description(UPDATED_DESCRIPTION)
            .cancel(UPDATED_CANCEL)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .puntuacion(UPDATED_PUNTUACION);

        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTask.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTask))
            )
            .andExpect(status().isOk());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getTaskName()).isEqualTo(UPDATED_TASK_NAME);
        assertThat(testTask.getDataCreate()).isEqualTo(UPDATED_DATA_CREATE);
        assertThat(testTask.getDataEnd()).isEqualTo(UPDATED_DATA_END);
        assertThat(testTask.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTask.getCancel()).isEqualTo(UPDATED_CANCEL);
        assertThat(testTask.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testTask.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testTask.getPuntuacion()).isEqualTo(UPDATED_PUNTUACION);
    }

    @Test
    @Transactional
    void patchNonExistingTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, task.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(task))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(task))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(task)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTask() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        int databaseSizeBeforeDelete = taskRepository.findAll().size();

        // Delete the task
        restTaskMockMvc
            .perform(delete(ENTITY_API_URL_ID, task.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
