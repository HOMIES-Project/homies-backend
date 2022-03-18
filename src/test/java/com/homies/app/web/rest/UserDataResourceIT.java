package com.homies.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.homies.app.IntegrationTest;
import com.homies.app.domain.Group;
import com.homies.app.domain.User;
import com.homies.app.domain.UserData;
import com.homies.app.repository.UserDataRepository;
import com.homies.app.service.criteria.UserDataCriteria;
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
 * Integration tests for the {@link UserDataResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserDataResourceIT {

    private static final byte[] DEFAULT_PHOTO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PHOTO = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PHOTO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PHOTO_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_PREMIUM = false;
    private static final Boolean UPDATED_PREMIUM = true;

    private static final LocalDate DEFAULT_BIRTH_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BIRTH_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_BIRTH_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_ADD_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ADD_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_ADD_DATE = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/user-data";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserDataRepository userDataRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserDataMockMvc;

    private UserData userData;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserData createEntity(EntityManager em) {
        UserData userData = new UserData()
            .photo(DEFAULT_PHOTO)
            .photoContentType(DEFAULT_PHOTO_CONTENT_TYPE)
            .phone(DEFAULT_PHONE)
            .premium(DEFAULT_PREMIUM)
            .birthDate(DEFAULT_BIRTH_DATE)
            .addDate(DEFAULT_ADD_DATE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        userData.setUser(user);
        return userData;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserData createUpdatedEntity(EntityManager em) {
        UserData userData = new UserData()
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .phone(UPDATED_PHONE)
            .premium(UPDATED_PREMIUM)
            .birthDate(UPDATED_BIRTH_DATE)
            .addDate(UPDATED_ADD_DATE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        userData.setUser(user);
        return userData;
    }

    @BeforeEach
    public void initTest() {
        userData = createEntity(em);
    }

    @Test
    @Transactional
    void createUserData() throws Exception {
        int databaseSizeBeforeCreate = userDataRepository.findAll().size();
        // Create the UserData
        restUserDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userData)))
            .andExpect(status().isCreated());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeCreate + 1);
        UserData testUserData = userDataList.get(userDataList.size() - 1);
        assertThat(testUserData.getPhoto()).isEqualTo(DEFAULT_PHOTO);
        assertThat(testUserData.getPhotoContentType()).isEqualTo(DEFAULT_PHOTO_CONTENT_TYPE);
        assertThat(testUserData.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testUserData.getPremium()).isEqualTo(DEFAULT_PREMIUM);
        assertThat(testUserData.getBirthDate()).isEqualTo(DEFAULT_BIRTH_DATE);
        assertThat(testUserData.getAddDate()).isEqualTo(DEFAULT_ADD_DATE);

        // Validate the id for MapsId, the ids must be same
        assertThat(testUserData.getId()).isEqualTo(testUserData.getUser().getId());
    }

    @Test
    @Transactional
    void createUserDataWithExistingId() throws Exception {
        // Create the UserData with an existing ID
        userData.setId(1L);

        int databaseSizeBeforeCreate = userDataRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userData)))
            .andExpect(status().isBadRequest());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void updateUserDataMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);
        int databaseSizeBeforeCreate = userDataRepository.findAll().size();

        // Load the userData
        UserData updatedUserData = userDataRepository.findById(userData.getId()).get();
        assertThat(updatedUserData).isNotNull();
        // Disconnect from session so that the updates on updatedUserData are not directly saved in db
        em.detach(updatedUserData);

        // Update the User with new association value
        updatedUserData.setUser();

        // Update the entity
        restUserDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUserData.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedUserData))
            )
            .andExpect(status().isOk());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeCreate);
        UserData testUserData = userDataList.get(userDataList.size() - 1);
        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testUserData.getId()).isEqualTo(testUserData.getUser().getId());
    }

    @Test
    @Transactional
    void checkPremiumIsRequired() throws Exception {
        int databaseSizeBeforeTest = userDataRepository.findAll().size();
        // set the field null
        userData.setPremium(null);

        // Create the UserData, which fails.

        restUserDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userData)))
            .andExpect(status().isBadRequest());

        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUserData() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get all the userDataList
        restUserDataMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userData.getId().intValue())))
            .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64Utils.encodeToString(DEFAULT_PHOTO))))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].premium").value(hasItem(DEFAULT_PREMIUM.booleanValue())))
            .andExpect(jsonPath("$.[*].birthDate").value(hasItem(DEFAULT_BIRTH_DATE.toString())))
            .andExpect(jsonPath("$.[*].addDate").value(hasItem(DEFAULT_ADD_DATE.toString())));
    }

    @Test
    @Transactional
    void getUserData() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get the userData
        restUserDataMockMvc
            .perform(get(ENTITY_API_URL_ID, userData.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userData.getId().intValue()))
            .andExpect(jsonPath("$.photoContentType").value(DEFAULT_PHOTO_CONTENT_TYPE))
            .andExpect(jsonPath("$.photo").value(Base64Utils.encodeToString(DEFAULT_PHOTO)))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.premium").value(DEFAULT_PREMIUM.booleanValue()))
            .andExpect(jsonPath("$.birthDate").value(DEFAULT_BIRTH_DATE.toString()))
            .andExpect(jsonPath("$.addDate").value(DEFAULT_ADD_DATE.toString()));
    }

    @Test
    @Transactional
    void getUserDataByIdFiltering() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        Long id = userData.getId();

        defaultUserDataShouldBeFound("id.equals=" + id);
        defaultUserDataShouldNotBeFound("id.notEquals=" + id);

        defaultUserDataShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultUserDataShouldNotBeFound("id.greaterThan=" + id);

        defaultUserDataShouldBeFound("id.lessThanOrEqual=" + id);
        defaultUserDataShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllUserDataByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get all the userDataList where phone equals to DEFAULT_PHONE
        defaultUserDataShouldBeFound("phone.equals=" + DEFAULT_PHONE);

        // Get all the userDataList where phone equals to UPDATED_PHONE
        defaultUserDataShouldNotBeFound("phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllUserDataByPhoneIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get all the userDataList where phone not equals to DEFAULT_PHONE
        defaultUserDataShouldNotBeFound("phone.notEquals=" + DEFAULT_PHONE);

        // Get all the userDataList where phone not equals to UPDATED_PHONE
        defaultUserDataShouldBeFound("phone.notEquals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllUserDataByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get all the userDataList where phone in DEFAULT_PHONE or UPDATED_PHONE
        defaultUserDataShouldBeFound("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE);

        // Get all the userDataList where phone equals to UPDATED_PHONE
        defaultUserDataShouldNotBeFound("phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllUserDataByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get all the userDataList where phone is not null
        defaultUserDataShouldBeFound("phone.specified=true");

        // Get all the userDataList where phone is null
        defaultUserDataShouldNotBeFound("phone.specified=false");
    }

    @Test
    @Transactional
    void getAllUserDataByPhoneContainsSomething() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get all the userDataList where phone contains DEFAULT_PHONE
        defaultUserDataShouldBeFound("phone.contains=" + DEFAULT_PHONE);

        // Get all the userDataList where phone contains UPDATED_PHONE
        defaultUserDataShouldNotBeFound("phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllUserDataByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get all the userDataList where phone does not contain DEFAULT_PHONE
        defaultUserDataShouldNotBeFound("phone.doesNotContain=" + DEFAULT_PHONE);

        // Get all the userDataList where phone does not contain UPDATED_PHONE
        defaultUserDataShouldBeFound("phone.doesNotContain=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllUserDataByPremiumIsEqualToSomething() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get all the userDataList where premium equals to DEFAULT_PREMIUM
        defaultUserDataShouldBeFound("premium.equals=" + DEFAULT_PREMIUM);

        // Get all the userDataList where premium equals to UPDATED_PREMIUM
        defaultUserDataShouldNotBeFound("premium.equals=" + UPDATED_PREMIUM);
    }

    @Test
    @Transactional
    void getAllUserDataByPremiumIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get all the userDataList where premium not equals to DEFAULT_PREMIUM
        defaultUserDataShouldNotBeFound("premium.notEquals=" + DEFAULT_PREMIUM);

        // Get all the userDataList where premium not equals to UPDATED_PREMIUM
        defaultUserDataShouldBeFound("premium.notEquals=" + UPDATED_PREMIUM);
    }

    @Test
    @Transactional
    void getAllUserDataByPremiumIsInShouldWork() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get all the userDataList where premium in DEFAULT_PREMIUM or UPDATED_PREMIUM
        defaultUserDataShouldBeFound("premium.in=" + DEFAULT_PREMIUM + "," + UPDATED_PREMIUM);

        // Get all the userDataList where premium equals to UPDATED_PREMIUM
        defaultUserDataShouldNotBeFound("premium.in=" + UPDATED_PREMIUM);
    }

    @Test
    @Transactional
    void getAllUserDataByPremiumIsNullOrNotNull() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get all the userDataList where premium is not null
        defaultUserDataShouldBeFound("premium.specified=true");

        // Get all the userDataList where premium is null
        defaultUserDataShouldNotBeFound("premium.specified=false");
    }

    @Test
    @Transactional
    void getAllUserDataByBirthDateIsEqualToSomething() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get all the userDataList where birthDate equals to DEFAULT_BIRTH_DATE
        defaultUserDataShouldBeFound("birthDate.equals=" + DEFAULT_BIRTH_DATE);

        // Get all the userDataList where birthDate equals to UPDATED_BIRTH_DATE
        defaultUserDataShouldNotBeFound("birthDate.equals=" + UPDATED_BIRTH_DATE);
    }

    @Test
    @Transactional
    void getAllUserDataByBirthDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get all the userDataList where birthDate not equals to DEFAULT_BIRTH_DATE
        defaultUserDataShouldNotBeFound("birthDate.notEquals=" + DEFAULT_BIRTH_DATE);

        // Get all the userDataList where birthDate not equals to UPDATED_BIRTH_DATE
        defaultUserDataShouldBeFound("birthDate.notEquals=" + UPDATED_BIRTH_DATE);
    }

    @Test
    @Transactional
    void getAllUserDataByBirthDateIsInShouldWork() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get all the userDataList where birthDate in DEFAULT_BIRTH_DATE or UPDATED_BIRTH_DATE
        defaultUserDataShouldBeFound("birthDate.in=" + DEFAULT_BIRTH_DATE + "," + UPDATED_BIRTH_DATE);

        // Get all the userDataList where birthDate equals to UPDATED_BIRTH_DATE
        defaultUserDataShouldNotBeFound("birthDate.in=" + UPDATED_BIRTH_DATE);
    }

    @Test
    @Transactional
    void getAllUserDataByBirthDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get all the userDataList where birthDate is not null
        defaultUserDataShouldBeFound("birthDate.specified=true");

        // Get all the userDataList where birthDate is null
        defaultUserDataShouldNotBeFound("birthDate.specified=false");
    }

    @Test
    @Transactional
    void getAllUserDataByBirthDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get all the userDataList where birthDate is greater than or equal to DEFAULT_BIRTH_DATE
        defaultUserDataShouldBeFound("birthDate.greaterThanOrEqual=" + DEFAULT_BIRTH_DATE);

        // Get all the userDataList where birthDate is greater than or equal to UPDATED_BIRTH_DATE
        defaultUserDataShouldNotBeFound("birthDate.greaterThanOrEqual=" + UPDATED_BIRTH_DATE);
    }

    @Test
    @Transactional
    void getAllUserDataByBirthDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get all the userDataList where birthDate is less than or equal to DEFAULT_BIRTH_DATE
        defaultUserDataShouldBeFound("birthDate.lessThanOrEqual=" + DEFAULT_BIRTH_DATE);

        // Get all the userDataList where birthDate is less than or equal to SMALLER_BIRTH_DATE
        defaultUserDataShouldNotBeFound("birthDate.lessThanOrEqual=" + SMALLER_BIRTH_DATE);
    }

    @Test
    @Transactional
    void getAllUserDataByBirthDateIsLessThanSomething() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get all the userDataList where birthDate is less than DEFAULT_BIRTH_DATE
        defaultUserDataShouldNotBeFound("birthDate.lessThan=" + DEFAULT_BIRTH_DATE);

        // Get all the userDataList where birthDate is less than UPDATED_BIRTH_DATE
        defaultUserDataShouldBeFound("birthDate.lessThan=" + UPDATED_BIRTH_DATE);
    }

    @Test
    @Transactional
    void getAllUserDataByBirthDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get all the userDataList where birthDate is greater than DEFAULT_BIRTH_DATE
        defaultUserDataShouldNotBeFound("birthDate.greaterThan=" + DEFAULT_BIRTH_DATE);

        // Get all the userDataList where birthDate is greater than SMALLER_BIRTH_DATE
        defaultUserDataShouldBeFound("birthDate.greaterThan=" + SMALLER_BIRTH_DATE);
    }

    @Test
    @Transactional
    void getAllUserDataByAddDateIsEqualToSomething() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get all the userDataList where addDate equals to DEFAULT_ADD_DATE
        defaultUserDataShouldBeFound("addDate.equals=" + DEFAULT_ADD_DATE);

        // Get all the userDataList where addDate equals to UPDATED_ADD_DATE
        defaultUserDataShouldNotBeFound("addDate.equals=" + UPDATED_ADD_DATE);
    }

    @Test
    @Transactional
    void getAllUserDataByAddDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get all the userDataList where addDate not equals to DEFAULT_ADD_DATE
        defaultUserDataShouldNotBeFound("addDate.notEquals=" + DEFAULT_ADD_DATE);

        // Get all the userDataList where addDate not equals to UPDATED_ADD_DATE
        defaultUserDataShouldBeFound("addDate.notEquals=" + UPDATED_ADD_DATE);
    }

    @Test
    @Transactional
    void getAllUserDataByAddDateIsInShouldWork() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get all the userDataList where addDate in DEFAULT_ADD_DATE or UPDATED_ADD_DATE
        defaultUserDataShouldBeFound("addDate.in=" + DEFAULT_ADD_DATE + "," + UPDATED_ADD_DATE);

        // Get all the userDataList where addDate equals to UPDATED_ADD_DATE
        defaultUserDataShouldNotBeFound("addDate.in=" + UPDATED_ADD_DATE);
    }

    @Test
    @Transactional
    void getAllUserDataByAddDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get all the userDataList where addDate is not null
        defaultUserDataShouldBeFound("addDate.specified=true");

        // Get all the userDataList where addDate is null
        defaultUserDataShouldNotBeFound("addDate.specified=false");
    }

    @Test
    @Transactional
    void getAllUserDataByAddDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get all the userDataList where addDate is greater than or equal to DEFAULT_ADD_DATE
        defaultUserDataShouldBeFound("addDate.greaterThanOrEqual=" + DEFAULT_ADD_DATE);

        // Get all the userDataList where addDate is greater than or equal to UPDATED_ADD_DATE
        defaultUserDataShouldNotBeFound("addDate.greaterThanOrEqual=" + UPDATED_ADD_DATE);
    }

    @Test
    @Transactional
    void getAllUserDataByAddDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get all the userDataList where addDate is less than or equal to DEFAULT_ADD_DATE
        defaultUserDataShouldBeFound("addDate.lessThanOrEqual=" + DEFAULT_ADD_DATE);

        // Get all the userDataList where addDate is less than or equal to SMALLER_ADD_DATE
        defaultUserDataShouldNotBeFound("addDate.lessThanOrEqual=" + SMALLER_ADD_DATE);
    }

    @Test
    @Transactional
    void getAllUserDataByAddDateIsLessThanSomething() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get all the userDataList where addDate is less than DEFAULT_ADD_DATE
        defaultUserDataShouldNotBeFound("addDate.lessThan=" + DEFAULT_ADD_DATE);

        // Get all the userDataList where addDate is less than UPDATED_ADD_DATE
        defaultUserDataShouldBeFound("addDate.lessThan=" + UPDATED_ADD_DATE);
    }

    @Test
    @Transactional
    void getAllUserDataByAddDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get all the userDataList where addDate is greater than DEFAULT_ADD_DATE
        defaultUserDataShouldNotBeFound("addDate.greaterThan=" + DEFAULT_ADD_DATE);

        // Get all the userDataList where addDate is greater than SMALLER_ADD_DATE
        defaultUserDataShouldBeFound("addDate.greaterThan=" + SMALLER_ADD_DATE);
    }

    @Test
    @Transactional
    void getAllUserDataByGroupIsEqualToSomething() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);
        Group group;
        if (TestUtil.findAll(em, Group.class).isEmpty()) {
            group = GroupResourceIT.createEntity(em);
            em.persist(group);
            em.flush();
        } else {
            group = TestUtil.findAll(em, Group.class).get(0);
        }
        em.persist(group);
        em.flush();
        userData.addGroup(group);
        userDataRepository.saveAndFlush(userData);
        Long groupId = group.getId();

        // Get all the userDataList where group equals to groupId
        defaultUserDataShouldBeFound("groupId.equals=" + groupId);

        // Get all the userDataList where group equals to (groupId + 1)
        defaultUserDataShouldNotBeFound("groupId.equals=" + (groupId + 1));
    }

    @Test
    @Transactional
    void getAllUserDataByUserIsEqualToSomething() throws Exception {
        // Get already existing entity
        User user = userData.getUser();
        userDataRepository.saveAndFlush(userData);
        Long userId = user.getId();

        // Get all the userDataList where user equals to userId
        defaultUserDataShouldBeFound("userId.equals=" + userId);

        // Get all the userDataList where user equals to (userId + 1)
        defaultUserDataShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllUserDataByAdminGroupsIsEqualToSomething() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);
        Group adminGroups;
        if (TestUtil.findAll(em, Group.class).isEmpty()) {
            adminGroups = GroupResourceIT.createEntity(em);
            em.persist(adminGroups);
            em.flush();
        } else {
            adminGroups = TestUtil.findAll(em, Group.class).get(0);
        }
        em.persist(adminGroups);
        em.flush();
        userData.addAdminGroups(adminGroups);
        userDataRepository.saveAndFlush(userData);
        Long adminGroupsId = adminGroups.getId();

        // Get all the userDataList where adminGroups equals to adminGroupsId
        defaultUserDataShouldBeFound("adminGroupsId.equals=" + adminGroupsId);

        // Get all the userDataList where adminGroups equals to (adminGroupsId + 1)
        defaultUserDataShouldNotBeFound("adminGroupsId.equals=" + (adminGroupsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultUserDataShouldBeFound(String filter) throws Exception {
        restUserDataMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userData.getId().intValue())))
            .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64Utils.encodeToString(DEFAULT_PHOTO))))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].premium").value(hasItem(DEFAULT_PREMIUM.booleanValue())))
            .andExpect(jsonPath("$.[*].birthDate").value(hasItem(DEFAULT_BIRTH_DATE.toString())))
            .andExpect(jsonPath("$.[*].addDate").value(hasItem(DEFAULT_ADD_DATE.toString())));

        // Check, that the count call also returns 1
        restUserDataMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultUserDataShouldNotBeFound(String filter) throws Exception {
        restUserDataMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUserDataMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingUserData() throws Exception {
        // Get the userData
        restUserDataMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewUserData() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        int databaseSizeBeforeUpdate = userDataRepository.findAll().size();

        // Update the userData
        UserData updatedUserData = userDataRepository.findById(userData.getId()).get();
        // Disconnect from session so that the updates on updatedUserData are not directly saved in db
        em.detach(updatedUserData);
        updatedUserData
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .phone(UPDATED_PHONE)
            .premium(UPDATED_PREMIUM)
            .birthDate(UPDATED_BIRTH_DATE)
            .addDate(UPDATED_ADD_DATE);

        restUserDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUserData.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedUserData))
            )
            .andExpect(status().isOk());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeUpdate);
        UserData testUserData = userDataList.get(userDataList.size() - 1);
        assertThat(testUserData.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testUserData.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testUserData.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testUserData.getPremium()).isEqualTo(UPDATED_PREMIUM);
        assertThat(testUserData.getBirthDate()).isEqualTo(UPDATED_BIRTH_DATE);
        assertThat(testUserData.getAddDate()).isEqualTo(UPDATED_ADD_DATE);
    }

    @Test
    @Transactional
    void putNonExistingUserData() throws Exception {
        int databaseSizeBeforeUpdate = userDataRepository.findAll().size();
        userData.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userData.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userData))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserData() throws Exception {
        int databaseSizeBeforeUpdate = userDataRepository.findAll().size();
        userData.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userData))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserData() throws Exception {
        int databaseSizeBeforeUpdate = userDataRepository.findAll().size();
        userData.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserDataMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userData)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserDataWithPatch() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        int databaseSizeBeforeUpdate = userDataRepository.findAll().size();

        // Update the userData using partial update
        UserData partialUpdatedUserData = new UserData();
        partialUpdatedUserData.setId(userData.getId());

        partialUpdatedUserData.birthDate(UPDATED_BIRTH_DATE);

        restUserDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserData.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserData))
            )
            .andExpect(status().isOk());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeUpdate);
        UserData testUserData = userDataList.get(userDataList.size() - 1);
        assertThat(testUserData.getPhoto()).isEqualTo(DEFAULT_PHOTO);
        assertThat(testUserData.getPhotoContentType()).isEqualTo(DEFAULT_PHOTO_CONTENT_TYPE);
        assertThat(testUserData.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testUserData.getPremium()).isEqualTo(DEFAULT_PREMIUM);
        assertThat(testUserData.getBirthDate()).isEqualTo(UPDATED_BIRTH_DATE);
        assertThat(testUserData.getAddDate()).isEqualTo(DEFAULT_ADD_DATE);
    }

    @Test
    @Transactional
    void fullUpdateUserDataWithPatch() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        int databaseSizeBeforeUpdate = userDataRepository.findAll().size();

        // Update the userData using partial update
        UserData partialUpdatedUserData = new UserData();
        partialUpdatedUserData.setId(userData.getId());

        partialUpdatedUserData
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .phone(UPDATED_PHONE)
            .premium(UPDATED_PREMIUM)
            .birthDate(UPDATED_BIRTH_DATE)
            .addDate(UPDATED_ADD_DATE);

        restUserDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserData.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserData))
            )
            .andExpect(status().isOk());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeUpdate);
        UserData testUserData = userDataList.get(userDataList.size() - 1);
        assertThat(testUserData.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testUserData.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testUserData.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testUserData.getPremium()).isEqualTo(UPDATED_PREMIUM);
        assertThat(testUserData.getBirthDate()).isEqualTo(UPDATED_BIRTH_DATE);
        assertThat(testUserData.getAddDate()).isEqualTo(UPDATED_ADD_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingUserData() throws Exception {
        int databaseSizeBeforeUpdate = userDataRepository.findAll().size();
        userData.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userData.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userData))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserData() throws Exception {
        int databaseSizeBeforeUpdate = userDataRepository.findAll().size();
        userData.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userData))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserData() throws Exception {
        int databaseSizeBeforeUpdate = userDataRepository.findAll().size();
        userData.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserDataMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(userData)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserData() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        int databaseSizeBeforeDelete = userDataRepository.findAll().size();

        // Delete the userData
        restUserDataMockMvc
            .perform(delete(ENTITY_API_URL_ID, userData.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
