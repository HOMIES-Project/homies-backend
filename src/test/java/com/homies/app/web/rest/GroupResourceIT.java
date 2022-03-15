package com.homies.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.homies.app.IntegrationTest;
import com.homies.app.domain.Group;
import com.homies.app.domain.UserName;
import com.homies.app.repository.GroupRepository;
import com.homies.app.service.GroupService;
import com.homies.app.service.criteria.GroupCriteria;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link GroupResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class GroupResourceIT {

    private static final String DEFAULT_GROUP_KEY = "AAAAAAAAAA";
    private static final String UPDATED_GROUP_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_GROUP_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_GROUP_PASSWORD = "BBBBBBBBBB";

    private static final String DEFAULT_GROUP_NAME = "AAAAAAAAAA";
    private static final String UPDATED_GROUP_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_GROUP_RELATION_NAME = "AAAAAAAAAA";
    private static final String UPDATED_GROUP_RELATION_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_ADD_GROUP_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ADD_GROUP_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_ADD_GROUP_DATE = LocalDate.ofEpochDay(-1L);

    private static final Long DEFAULT_ID_USER_NAME = 1L;
    private static final Long UPDATED_ID_USER_NAME = 2L;
    private static final Long SMALLER_ID_USER_NAME = 1L - 1L;

    private static final String ENTITY_API_URL = "/api/groups";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private GroupRepository groupRepository;

    @Mock
    private GroupRepository groupRepositoryMock;

    @Mock
    private GroupService groupServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGroupMockMvc;

    private Group group;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Group createEntity(EntityManager em) {
        Group group = new Group()
            .groupKey(DEFAULT_GROUP_KEY)
            .groupPassword(DEFAULT_GROUP_PASSWORD)
            .groupName(DEFAULT_GROUP_NAME)
            .groupRelationName(DEFAULT_GROUP_RELATION_NAME)
            .addGroupDate(DEFAULT_ADD_GROUP_DATE)
            .idUserName(DEFAULT_ID_USER_NAME);
        return group;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Group createUpdatedEntity(EntityManager em) {
        Group group = new Group()
            .groupKey(UPDATED_GROUP_KEY)
            .groupPassword(UPDATED_GROUP_PASSWORD)
            .groupName(UPDATED_GROUP_NAME)
            .groupRelationName(UPDATED_GROUP_RELATION_NAME)
            .addGroupDate(UPDATED_ADD_GROUP_DATE)
            .idUserName(UPDATED_ID_USER_NAME);
        return group;
    }

    @BeforeEach
    public void initTest() {
        group = createEntity(em);
    }

    @Test
    @Transactional
    void createGroup() throws Exception {
        int databaseSizeBeforeCreate = groupRepository.findAll().size();
        // Create the Group
        restGroupMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(group)))
            .andExpect(status().isCreated());

        // Validate the Group in the database
        List<Group> groupList = groupRepository.findAll();
        assertThat(groupList).hasSize(databaseSizeBeforeCreate + 1);
        Group testGroup = groupList.get(groupList.size() - 1);
        assertThat(testGroup.getGroupKey()).isEqualTo(DEFAULT_GROUP_KEY);
        assertThat(testGroup.getGroupPassword()).isEqualTo(DEFAULT_GROUP_PASSWORD);
        assertThat(testGroup.getGroupName()).isEqualTo(DEFAULT_GROUP_NAME);
        assertThat(testGroup.getGroupRelationName()).isEqualTo(DEFAULT_GROUP_RELATION_NAME);
        assertThat(testGroup.getAddGroupDate()).isEqualTo(DEFAULT_ADD_GROUP_DATE);
        assertThat(testGroup.getIdUserName()).isEqualTo(DEFAULT_ID_USER_NAME);
    }

    @Test
    @Transactional
    void createGroupWithExistingId() throws Exception {
        // Create the Group with an existing ID
        group.setId(1L);

        int databaseSizeBeforeCreate = groupRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGroupMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(group)))
            .andExpect(status().isBadRequest());

        // Validate the Group in the database
        List<Group> groupList = groupRepository.findAll();
        assertThat(groupList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkGroupKeyIsRequired() throws Exception {
        int databaseSizeBeforeTest = groupRepository.findAll().size();
        // set the field null
        group.setGroupKey(null);

        // Create the Group, which fails.

        restGroupMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(group)))
            .andExpect(status().isBadRequest());

        List<Group> groupList = groupRepository.findAll();
        assertThat(groupList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkGroupPasswordIsRequired() throws Exception {
        int databaseSizeBeforeTest = groupRepository.findAll().size();
        // set the field null
        group.setGroupPassword(null);

        // Create the Group, which fails.

        restGroupMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(group)))
            .andExpect(status().isBadRequest());

        List<Group> groupList = groupRepository.findAll();
        assertThat(groupList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkGroupNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = groupRepository.findAll().size();
        // set the field null
        group.setGroupName(null);

        // Create the Group, which fails.

        restGroupMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(group)))
            .andExpect(status().isBadRequest());

        List<Group> groupList = groupRepository.findAll();
        assertThat(groupList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkGroupRelationNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = groupRepository.findAll().size();
        // set the field null
        group.setGroupRelationName(null);

        // Create the Group, which fails.

        restGroupMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(group)))
            .andExpect(status().isBadRequest());

        List<Group> groupList = groupRepository.findAll();
        assertThat(groupList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIdUserNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = groupRepository.findAll().size();
        // set the field null
        group.setIdUserName(null);

        // Create the Group, which fails.

        restGroupMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(group)))
            .andExpect(status().isBadRequest());

        List<Group> groupList = groupRepository.findAll();
        assertThat(groupList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllGroups() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList
        restGroupMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(group.getId().intValue())))
            .andExpect(jsonPath("$.[*].groupKey").value(hasItem(DEFAULT_GROUP_KEY)))
            .andExpect(jsonPath("$.[*].groupPassword").value(hasItem(DEFAULT_GROUP_PASSWORD)))
            .andExpect(jsonPath("$.[*].groupName").value(hasItem(DEFAULT_GROUP_NAME)))
            .andExpect(jsonPath("$.[*].groupRelationName").value(hasItem(DEFAULT_GROUP_RELATION_NAME)))
            .andExpect(jsonPath("$.[*].addGroupDate").value(hasItem(DEFAULT_ADD_GROUP_DATE.toString())))
            .andExpect(jsonPath("$.[*].idUserName").value(hasItem(DEFAULT_ID_USER_NAME.intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllGroupsWithEagerRelationshipsIsEnabled() throws Exception {
        when(groupServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restGroupMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(groupServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllGroupsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(groupServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restGroupMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(groupServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getGroup() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get the group
        restGroupMockMvc
            .perform(get(ENTITY_API_URL_ID, group.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(group.getId().intValue()))
            .andExpect(jsonPath("$.groupKey").value(DEFAULT_GROUP_KEY))
            .andExpect(jsonPath("$.groupPassword").value(DEFAULT_GROUP_PASSWORD))
            .andExpect(jsonPath("$.groupName").value(DEFAULT_GROUP_NAME))
            .andExpect(jsonPath("$.groupRelationName").value(DEFAULT_GROUP_RELATION_NAME))
            .andExpect(jsonPath("$.addGroupDate").value(DEFAULT_ADD_GROUP_DATE.toString()))
            .andExpect(jsonPath("$.idUserName").value(DEFAULT_ID_USER_NAME.intValue()));
    }

    @Test
    @Transactional
    void getGroupsByIdFiltering() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        Long id = group.getId();

        defaultGroupShouldBeFound("id.equals=" + id);
        defaultGroupShouldNotBeFound("id.notEquals=" + id);

        defaultGroupShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultGroupShouldNotBeFound("id.greaterThan=" + id);

        defaultGroupShouldBeFound("id.lessThanOrEqual=" + id);
        defaultGroupShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllGroupsByGroupKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where groupKey equals to DEFAULT_GROUP_KEY
        defaultGroupShouldBeFound("groupKey.equals=" + DEFAULT_GROUP_KEY);

        // Get all the groupList where groupKey equals to UPDATED_GROUP_KEY
        defaultGroupShouldNotBeFound("groupKey.equals=" + UPDATED_GROUP_KEY);
    }

    @Test
    @Transactional
    void getAllGroupsByGroupKeyIsNotEqualToSomething() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where groupKey not equals to DEFAULT_GROUP_KEY
        defaultGroupShouldNotBeFound("groupKey.notEquals=" + DEFAULT_GROUP_KEY);

        // Get all the groupList where groupKey not equals to UPDATED_GROUP_KEY
        defaultGroupShouldBeFound("groupKey.notEquals=" + UPDATED_GROUP_KEY);
    }

    @Test
    @Transactional
    void getAllGroupsByGroupKeyIsInShouldWork() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where groupKey in DEFAULT_GROUP_KEY or UPDATED_GROUP_KEY
        defaultGroupShouldBeFound("groupKey.in=" + DEFAULT_GROUP_KEY + "," + UPDATED_GROUP_KEY);

        // Get all the groupList where groupKey equals to UPDATED_GROUP_KEY
        defaultGroupShouldNotBeFound("groupKey.in=" + UPDATED_GROUP_KEY);
    }

    @Test
    @Transactional
    void getAllGroupsByGroupKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where groupKey is not null
        defaultGroupShouldBeFound("groupKey.specified=true");

        // Get all the groupList where groupKey is null
        defaultGroupShouldNotBeFound("groupKey.specified=false");
    }

    @Test
    @Transactional
    void getAllGroupsByGroupKeyContainsSomething() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where groupKey contains DEFAULT_GROUP_KEY
        defaultGroupShouldBeFound("groupKey.contains=" + DEFAULT_GROUP_KEY);

        // Get all the groupList where groupKey contains UPDATED_GROUP_KEY
        defaultGroupShouldNotBeFound("groupKey.contains=" + UPDATED_GROUP_KEY);
    }

    @Test
    @Transactional
    void getAllGroupsByGroupKeyNotContainsSomething() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where groupKey does not contain DEFAULT_GROUP_KEY
        defaultGroupShouldNotBeFound("groupKey.doesNotContain=" + DEFAULT_GROUP_KEY);

        // Get all the groupList where groupKey does not contain UPDATED_GROUP_KEY
        defaultGroupShouldBeFound("groupKey.doesNotContain=" + UPDATED_GROUP_KEY);
    }

    @Test
    @Transactional
    void getAllGroupsByGroupPasswordIsEqualToSomething() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where groupPassword equals to DEFAULT_GROUP_PASSWORD
        defaultGroupShouldBeFound("groupPassword.equals=" + DEFAULT_GROUP_PASSWORD);

        // Get all the groupList where groupPassword equals to UPDATED_GROUP_PASSWORD
        defaultGroupShouldNotBeFound("groupPassword.equals=" + UPDATED_GROUP_PASSWORD);
    }

    @Test
    @Transactional
    void getAllGroupsByGroupPasswordIsNotEqualToSomething() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where groupPassword not equals to DEFAULT_GROUP_PASSWORD
        defaultGroupShouldNotBeFound("groupPassword.notEquals=" + DEFAULT_GROUP_PASSWORD);

        // Get all the groupList where groupPassword not equals to UPDATED_GROUP_PASSWORD
        defaultGroupShouldBeFound("groupPassword.notEquals=" + UPDATED_GROUP_PASSWORD);
    }

    @Test
    @Transactional
    void getAllGroupsByGroupPasswordIsInShouldWork() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where groupPassword in DEFAULT_GROUP_PASSWORD or UPDATED_GROUP_PASSWORD
        defaultGroupShouldBeFound("groupPassword.in=" + DEFAULT_GROUP_PASSWORD + "," + UPDATED_GROUP_PASSWORD);

        // Get all the groupList where groupPassword equals to UPDATED_GROUP_PASSWORD
        defaultGroupShouldNotBeFound("groupPassword.in=" + UPDATED_GROUP_PASSWORD);
    }

    @Test
    @Transactional
    void getAllGroupsByGroupPasswordIsNullOrNotNull() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where groupPassword is not null
        defaultGroupShouldBeFound("groupPassword.specified=true");

        // Get all the groupList where groupPassword is null
        defaultGroupShouldNotBeFound("groupPassword.specified=false");
    }

    @Test
    @Transactional
    void getAllGroupsByGroupPasswordContainsSomething() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where groupPassword contains DEFAULT_GROUP_PASSWORD
        defaultGroupShouldBeFound("groupPassword.contains=" + DEFAULT_GROUP_PASSWORD);

        // Get all the groupList where groupPassword contains UPDATED_GROUP_PASSWORD
        defaultGroupShouldNotBeFound("groupPassword.contains=" + UPDATED_GROUP_PASSWORD);
    }

    @Test
    @Transactional
    void getAllGroupsByGroupPasswordNotContainsSomething() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where groupPassword does not contain DEFAULT_GROUP_PASSWORD
        defaultGroupShouldNotBeFound("groupPassword.doesNotContain=" + DEFAULT_GROUP_PASSWORD);

        // Get all the groupList where groupPassword does not contain UPDATED_GROUP_PASSWORD
        defaultGroupShouldBeFound("groupPassword.doesNotContain=" + UPDATED_GROUP_PASSWORD);
    }

    @Test
    @Transactional
    void getAllGroupsByGroupNameIsEqualToSomething() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where groupName equals to DEFAULT_GROUP_NAME
        defaultGroupShouldBeFound("groupName.equals=" + DEFAULT_GROUP_NAME);

        // Get all the groupList where groupName equals to UPDATED_GROUP_NAME
        defaultGroupShouldNotBeFound("groupName.equals=" + UPDATED_GROUP_NAME);
    }

    @Test
    @Transactional
    void getAllGroupsByGroupNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where groupName not equals to DEFAULT_GROUP_NAME
        defaultGroupShouldNotBeFound("groupName.notEquals=" + DEFAULT_GROUP_NAME);

        // Get all the groupList where groupName not equals to UPDATED_GROUP_NAME
        defaultGroupShouldBeFound("groupName.notEquals=" + UPDATED_GROUP_NAME);
    }

    @Test
    @Transactional
    void getAllGroupsByGroupNameIsInShouldWork() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where groupName in DEFAULT_GROUP_NAME or UPDATED_GROUP_NAME
        defaultGroupShouldBeFound("groupName.in=" + DEFAULT_GROUP_NAME + "," + UPDATED_GROUP_NAME);

        // Get all the groupList where groupName equals to UPDATED_GROUP_NAME
        defaultGroupShouldNotBeFound("groupName.in=" + UPDATED_GROUP_NAME);
    }

    @Test
    @Transactional
    void getAllGroupsByGroupNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where groupName is not null
        defaultGroupShouldBeFound("groupName.specified=true");

        // Get all the groupList where groupName is null
        defaultGroupShouldNotBeFound("groupName.specified=false");
    }

    @Test
    @Transactional
    void getAllGroupsByGroupNameContainsSomething() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where groupName contains DEFAULT_GROUP_NAME
        defaultGroupShouldBeFound("groupName.contains=" + DEFAULT_GROUP_NAME);

        // Get all the groupList where groupName contains UPDATED_GROUP_NAME
        defaultGroupShouldNotBeFound("groupName.contains=" + UPDATED_GROUP_NAME);
    }

    @Test
    @Transactional
    void getAllGroupsByGroupNameNotContainsSomething() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where groupName does not contain DEFAULT_GROUP_NAME
        defaultGroupShouldNotBeFound("groupName.doesNotContain=" + DEFAULT_GROUP_NAME);

        // Get all the groupList where groupName does not contain UPDATED_GROUP_NAME
        defaultGroupShouldBeFound("groupName.doesNotContain=" + UPDATED_GROUP_NAME);
    }

    @Test
    @Transactional
    void getAllGroupsByGroupRelationNameIsEqualToSomething() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where groupRelationName equals to DEFAULT_GROUP_RELATION_NAME
        defaultGroupShouldBeFound("groupRelationName.equals=" + DEFAULT_GROUP_RELATION_NAME);

        // Get all the groupList where groupRelationName equals to UPDATED_GROUP_RELATION_NAME
        defaultGroupShouldNotBeFound("groupRelationName.equals=" + UPDATED_GROUP_RELATION_NAME);
    }

    @Test
    @Transactional
    void getAllGroupsByGroupRelationNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where groupRelationName not equals to DEFAULT_GROUP_RELATION_NAME
        defaultGroupShouldNotBeFound("groupRelationName.notEquals=" + DEFAULT_GROUP_RELATION_NAME);

        // Get all the groupList where groupRelationName not equals to UPDATED_GROUP_RELATION_NAME
        defaultGroupShouldBeFound("groupRelationName.notEquals=" + UPDATED_GROUP_RELATION_NAME);
    }

    @Test
    @Transactional
    void getAllGroupsByGroupRelationNameIsInShouldWork() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where groupRelationName in DEFAULT_GROUP_RELATION_NAME or UPDATED_GROUP_RELATION_NAME
        defaultGroupShouldBeFound("groupRelationName.in=" + DEFAULT_GROUP_RELATION_NAME + "," + UPDATED_GROUP_RELATION_NAME);

        // Get all the groupList where groupRelationName equals to UPDATED_GROUP_RELATION_NAME
        defaultGroupShouldNotBeFound("groupRelationName.in=" + UPDATED_GROUP_RELATION_NAME);
    }

    @Test
    @Transactional
    void getAllGroupsByGroupRelationNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where groupRelationName is not null
        defaultGroupShouldBeFound("groupRelationName.specified=true");

        // Get all the groupList where groupRelationName is null
        defaultGroupShouldNotBeFound("groupRelationName.specified=false");
    }

    @Test
    @Transactional
    void getAllGroupsByGroupRelationNameContainsSomething() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where groupRelationName contains DEFAULT_GROUP_RELATION_NAME
        defaultGroupShouldBeFound("groupRelationName.contains=" + DEFAULT_GROUP_RELATION_NAME);

        // Get all the groupList where groupRelationName contains UPDATED_GROUP_RELATION_NAME
        defaultGroupShouldNotBeFound("groupRelationName.contains=" + UPDATED_GROUP_RELATION_NAME);
    }

    @Test
    @Transactional
    void getAllGroupsByGroupRelationNameNotContainsSomething() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where groupRelationName does not contain DEFAULT_GROUP_RELATION_NAME
        defaultGroupShouldNotBeFound("groupRelationName.doesNotContain=" + DEFAULT_GROUP_RELATION_NAME);

        // Get all the groupList where groupRelationName does not contain UPDATED_GROUP_RELATION_NAME
        defaultGroupShouldBeFound("groupRelationName.doesNotContain=" + UPDATED_GROUP_RELATION_NAME);
    }

    @Test
    @Transactional
    void getAllGroupsByAddGroupDateIsEqualToSomething() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where addGroupDate equals to DEFAULT_ADD_GROUP_DATE
        defaultGroupShouldBeFound("addGroupDate.equals=" + DEFAULT_ADD_GROUP_DATE);

        // Get all the groupList where addGroupDate equals to UPDATED_ADD_GROUP_DATE
        defaultGroupShouldNotBeFound("addGroupDate.equals=" + UPDATED_ADD_GROUP_DATE);
    }

    @Test
    @Transactional
    void getAllGroupsByAddGroupDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where addGroupDate not equals to DEFAULT_ADD_GROUP_DATE
        defaultGroupShouldNotBeFound("addGroupDate.notEquals=" + DEFAULT_ADD_GROUP_DATE);

        // Get all the groupList where addGroupDate not equals to UPDATED_ADD_GROUP_DATE
        defaultGroupShouldBeFound("addGroupDate.notEquals=" + UPDATED_ADD_GROUP_DATE);
    }

    @Test
    @Transactional
    void getAllGroupsByAddGroupDateIsInShouldWork() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where addGroupDate in DEFAULT_ADD_GROUP_DATE or UPDATED_ADD_GROUP_DATE
        defaultGroupShouldBeFound("addGroupDate.in=" + DEFAULT_ADD_GROUP_DATE + "," + UPDATED_ADD_GROUP_DATE);

        // Get all the groupList where addGroupDate equals to UPDATED_ADD_GROUP_DATE
        defaultGroupShouldNotBeFound("addGroupDate.in=" + UPDATED_ADD_GROUP_DATE);
    }

    @Test
    @Transactional
    void getAllGroupsByAddGroupDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where addGroupDate is not null
        defaultGroupShouldBeFound("addGroupDate.specified=true");

        // Get all the groupList where addGroupDate is null
        defaultGroupShouldNotBeFound("addGroupDate.specified=false");
    }

    @Test
    @Transactional
    void getAllGroupsByAddGroupDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where addGroupDate is greater than or equal to DEFAULT_ADD_GROUP_DATE
        defaultGroupShouldBeFound("addGroupDate.greaterThanOrEqual=" + DEFAULT_ADD_GROUP_DATE);

        // Get all the groupList where addGroupDate is greater than or equal to UPDATED_ADD_GROUP_DATE
        defaultGroupShouldNotBeFound("addGroupDate.greaterThanOrEqual=" + UPDATED_ADD_GROUP_DATE);
    }

    @Test
    @Transactional
    void getAllGroupsByAddGroupDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where addGroupDate is less than or equal to DEFAULT_ADD_GROUP_DATE
        defaultGroupShouldBeFound("addGroupDate.lessThanOrEqual=" + DEFAULT_ADD_GROUP_DATE);

        // Get all the groupList where addGroupDate is less than or equal to SMALLER_ADD_GROUP_DATE
        defaultGroupShouldNotBeFound("addGroupDate.lessThanOrEqual=" + SMALLER_ADD_GROUP_DATE);
    }

    @Test
    @Transactional
    void getAllGroupsByAddGroupDateIsLessThanSomething() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where addGroupDate is less than DEFAULT_ADD_GROUP_DATE
        defaultGroupShouldNotBeFound("addGroupDate.lessThan=" + DEFAULT_ADD_GROUP_DATE);

        // Get all the groupList where addGroupDate is less than UPDATED_ADD_GROUP_DATE
        defaultGroupShouldBeFound("addGroupDate.lessThan=" + UPDATED_ADD_GROUP_DATE);
    }

    @Test
    @Transactional
    void getAllGroupsByAddGroupDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where addGroupDate is greater than DEFAULT_ADD_GROUP_DATE
        defaultGroupShouldNotBeFound("addGroupDate.greaterThan=" + DEFAULT_ADD_GROUP_DATE);

        // Get all the groupList where addGroupDate is greater than SMALLER_ADD_GROUP_DATE
        defaultGroupShouldBeFound("addGroupDate.greaterThan=" + SMALLER_ADD_GROUP_DATE);
    }

    @Test
    @Transactional
    void getAllGroupsByIdUserNameIsEqualToSomething() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where idUserName equals to DEFAULT_ID_USER_NAME
        defaultGroupShouldBeFound("idUserName.equals=" + DEFAULT_ID_USER_NAME);

        // Get all the groupList where idUserName equals to UPDATED_ID_USER_NAME
        defaultGroupShouldNotBeFound("idUserName.equals=" + UPDATED_ID_USER_NAME);
    }

    @Test
    @Transactional
    void getAllGroupsByIdUserNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where idUserName not equals to DEFAULT_ID_USER_NAME
        defaultGroupShouldNotBeFound("idUserName.notEquals=" + DEFAULT_ID_USER_NAME);

        // Get all the groupList where idUserName not equals to UPDATED_ID_USER_NAME
        defaultGroupShouldBeFound("idUserName.notEquals=" + UPDATED_ID_USER_NAME);
    }

    @Test
    @Transactional
    void getAllGroupsByIdUserNameIsInShouldWork() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where idUserName in DEFAULT_ID_USER_NAME or UPDATED_ID_USER_NAME
        defaultGroupShouldBeFound("idUserName.in=" + DEFAULT_ID_USER_NAME + "," + UPDATED_ID_USER_NAME);

        // Get all the groupList where idUserName equals to UPDATED_ID_USER_NAME
        defaultGroupShouldNotBeFound("idUserName.in=" + UPDATED_ID_USER_NAME);
    }

    @Test
    @Transactional
    void getAllGroupsByIdUserNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where idUserName is not null
        defaultGroupShouldBeFound("idUserName.specified=true");

        // Get all the groupList where idUserName is null
        defaultGroupShouldNotBeFound("idUserName.specified=false");
    }

    @Test
    @Transactional
    void getAllGroupsByIdUserNameIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where idUserName is greater than or equal to DEFAULT_ID_USER_NAME
        defaultGroupShouldBeFound("idUserName.greaterThanOrEqual=" + DEFAULT_ID_USER_NAME);

        // Get all the groupList where idUserName is greater than or equal to UPDATED_ID_USER_NAME
        defaultGroupShouldNotBeFound("idUserName.greaterThanOrEqual=" + UPDATED_ID_USER_NAME);
    }

    @Test
    @Transactional
    void getAllGroupsByIdUserNameIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where idUserName is less than or equal to DEFAULT_ID_USER_NAME
        defaultGroupShouldBeFound("idUserName.lessThanOrEqual=" + DEFAULT_ID_USER_NAME);

        // Get all the groupList where idUserName is less than or equal to SMALLER_ID_USER_NAME
        defaultGroupShouldNotBeFound("idUserName.lessThanOrEqual=" + SMALLER_ID_USER_NAME);
    }

    @Test
    @Transactional
    void getAllGroupsByIdUserNameIsLessThanSomething() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where idUserName is less than DEFAULT_ID_USER_NAME
        defaultGroupShouldNotBeFound("idUserName.lessThan=" + DEFAULT_ID_USER_NAME);

        // Get all the groupList where idUserName is less than UPDATED_ID_USER_NAME
        defaultGroupShouldBeFound("idUserName.lessThan=" + UPDATED_ID_USER_NAME);
    }

    @Test
    @Transactional
    void getAllGroupsByIdUserNameIsGreaterThanSomething() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        // Get all the groupList where idUserName is greater than DEFAULT_ID_USER_NAME
        defaultGroupShouldNotBeFound("idUserName.greaterThan=" + DEFAULT_ID_USER_NAME);

        // Get all the groupList where idUserName is greater than SMALLER_ID_USER_NAME
        defaultGroupShouldBeFound("idUserName.greaterThan=" + SMALLER_ID_USER_NAME);
    }

    @Test
    @Transactional
    void getAllGroupsByUserNameIsEqualToSomething() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);
        UserName userName;
        if (TestUtil.findAll(em, UserName.class).isEmpty()) {
            userName = UserNameResourceIT.createEntity(em);
            em.persist(userName);
            em.flush();
        } else {
            userName = TestUtil.findAll(em, UserName.class).get(0);
        }
        em.persist(userName);
        em.flush();
        group.addUserName(userName);
        groupRepository.saveAndFlush(group);
        Long userNameId = userName.getId();

        // Get all the groupList where userName equals to userNameId
        defaultGroupShouldBeFound("userNameId.equals=" + userNameId);

        // Get all the groupList where userName equals to (userNameId + 1)
        defaultGroupShouldNotBeFound("userNameId.equals=" + (userNameId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultGroupShouldBeFound(String filter) throws Exception {
        restGroupMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(group.getId().intValue())))
            .andExpect(jsonPath("$.[*].groupKey").value(hasItem(DEFAULT_GROUP_KEY)))
            .andExpect(jsonPath("$.[*].groupPassword").value(hasItem(DEFAULT_GROUP_PASSWORD)))
            .andExpect(jsonPath("$.[*].groupName").value(hasItem(DEFAULT_GROUP_NAME)))
            .andExpect(jsonPath("$.[*].groupRelationName").value(hasItem(DEFAULT_GROUP_RELATION_NAME)))
            .andExpect(jsonPath("$.[*].addGroupDate").value(hasItem(DEFAULT_ADD_GROUP_DATE.toString())))
            .andExpect(jsonPath("$.[*].idUserName").value(hasItem(DEFAULT_ID_USER_NAME.intValue())));

        // Check, that the count call also returns 1
        restGroupMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultGroupShouldNotBeFound(String filter) throws Exception {
        restGroupMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restGroupMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingGroup() throws Exception {
        // Get the group
        restGroupMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewGroup() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        int databaseSizeBeforeUpdate = groupRepository.findAll().size();

        // Update the group
        Group updatedGroup = groupRepository.findById(group.getId()).get();
        // Disconnect from session so that the updates on updatedGroup are not directly saved in db
        em.detach(updatedGroup);
        updatedGroup
            .groupKey(UPDATED_GROUP_KEY)
            .groupPassword(UPDATED_GROUP_PASSWORD)
            .groupName(UPDATED_GROUP_NAME)
            .groupRelationName(UPDATED_GROUP_RELATION_NAME)
            .addGroupDate(UPDATED_ADD_GROUP_DATE)
            .idUserName(UPDATED_ID_USER_NAME);

        restGroupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedGroup.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedGroup))
            )
            .andExpect(status().isOk());

        // Validate the Group in the database
        List<Group> groupList = groupRepository.findAll();
        assertThat(groupList).hasSize(databaseSizeBeforeUpdate);
        Group testGroup = groupList.get(groupList.size() - 1);
        assertThat(testGroup.getGroupKey()).isEqualTo(UPDATED_GROUP_KEY);
        assertThat(testGroup.getGroupPassword()).isEqualTo(UPDATED_GROUP_PASSWORD);
        assertThat(testGroup.getGroupName()).isEqualTo(UPDATED_GROUP_NAME);
        assertThat(testGroup.getGroupRelationName()).isEqualTo(UPDATED_GROUP_RELATION_NAME);
        assertThat(testGroup.getAddGroupDate()).isEqualTo(UPDATED_ADD_GROUP_DATE);
        assertThat(testGroup.getIdUserName()).isEqualTo(UPDATED_ID_USER_NAME);
    }

    @Test
    @Transactional
    void putNonExistingGroup() throws Exception {
        int databaseSizeBeforeUpdate = groupRepository.findAll().size();
        group.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGroupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, group.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(group))
            )
            .andExpect(status().isBadRequest());

        // Validate the Group in the database
        List<Group> groupList = groupRepository.findAll();
        assertThat(groupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGroup() throws Exception {
        int databaseSizeBeforeUpdate = groupRepository.findAll().size();
        group.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGroupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(group))
            )
            .andExpect(status().isBadRequest());

        // Validate the Group in the database
        List<Group> groupList = groupRepository.findAll();
        assertThat(groupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGroup() throws Exception {
        int databaseSizeBeforeUpdate = groupRepository.findAll().size();
        group.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGroupMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(group)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Group in the database
        List<Group> groupList = groupRepository.findAll();
        assertThat(groupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGroupWithPatch() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        int databaseSizeBeforeUpdate = groupRepository.findAll().size();

        // Update the group using partial update
        Group partialUpdatedGroup = new Group();
        partialUpdatedGroup.setId(group.getId());

        partialUpdatedGroup.idUserName(UPDATED_ID_USER_NAME);

        restGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGroup.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGroup))
            )
            .andExpect(status().isOk());

        // Validate the Group in the database
        List<Group> groupList = groupRepository.findAll();
        assertThat(groupList).hasSize(databaseSizeBeforeUpdate);
        Group testGroup = groupList.get(groupList.size() - 1);
        assertThat(testGroup.getGroupKey()).isEqualTo(DEFAULT_GROUP_KEY);
        assertThat(testGroup.getGroupPassword()).isEqualTo(DEFAULT_GROUP_PASSWORD);
        assertThat(testGroup.getGroupName()).isEqualTo(DEFAULT_GROUP_NAME);
        assertThat(testGroup.getGroupRelationName()).isEqualTo(DEFAULT_GROUP_RELATION_NAME);
        assertThat(testGroup.getAddGroupDate()).isEqualTo(DEFAULT_ADD_GROUP_DATE);
        assertThat(testGroup.getIdUserName()).isEqualTo(UPDATED_ID_USER_NAME);
    }

    @Test
    @Transactional
    void fullUpdateGroupWithPatch() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        int databaseSizeBeforeUpdate = groupRepository.findAll().size();

        // Update the group using partial update
        Group partialUpdatedGroup = new Group();
        partialUpdatedGroup.setId(group.getId());

        partialUpdatedGroup
            .groupKey(UPDATED_GROUP_KEY)
            .groupPassword(UPDATED_GROUP_PASSWORD)
            .groupName(UPDATED_GROUP_NAME)
            .groupRelationName(UPDATED_GROUP_RELATION_NAME)
            .addGroupDate(UPDATED_ADD_GROUP_DATE)
            .idUserName(UPDATED_ID_USER_NAME);

        restGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGroup.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGroup))
            )
            .andExpect(status().isOk());

        // Validate the Group in the database
        List<Group> groupList = groupRepository.findAll();
        assertThat(groupList).hasSize(databaseSizeBeforeUpdate);
        Group testGroup = groupList.get(groupList.size() - 1);
        assertThat(testGroup.getGroupKey()).isEqualTo(UPDATED_GROUP_KEY);
        assertThat(testGroup.getGroupPassword()).isEqualTo(UPDATED_GROUP_PASSWORD);
        assertThat(testGroup.getGroupName()).isEqualTo(UPDATED_GROUP_NAME);
        assertThat(testGroup.getGroupRelationName()).isEqualTo(UPDATED_GROUP_RELATION_NAME);
        assertThat(testGroup.getAddGroupDate()).isEqualTo(UPDATED_ADD_GROUP_DATE);
        assertThat(testGroup.getIdUserName()).isEqualTo(UPDATED_ID_USER_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingGroup() throws Exception {
        int databaseSizeBeforeUpdate = groupRepository.findAll().size();
        group.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, group.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(group))
            )
            .andExpect(status().isBadRequest());

        // Validate the Group in the database
        List<Group> groupList = groupRepository.findAll();
        assertThat(groupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGroup() throws Exception {
        int databaseSizeBeforeUpdate = groupRepository.findAll().size();
        group.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(group))
            )
            .andExpect(status().isBadRequest());

        // Validate the Group in the database
        List<Group> groupList = groupRepository.findAll();
        assertThat(groupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGroup() throws Exception {
        int databaseSizeBeforeUpdate = groupRepository.findAll().size();
        group.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGroupMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(group)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Group in the database
        List<Group> groupList = groupRepository.findAll();
        assertThat(groupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGroup() throws Exception {
        // Initialize the database
        groupRepository.saveAndFlush(group);

        int databaseSizeBeforeDelete = groupRepository.findAll().size();

        // Delete the group
        restGroupMockMvc
            .perform(delete(ENTITY_API_URL_ID, group.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Group> groupList = groupRepository.findAll();
        assertThat(groupList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
