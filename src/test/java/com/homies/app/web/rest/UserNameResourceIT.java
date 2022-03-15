package com.homies.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.homies.app.IntegrationTest;
import com.homies.app.domain.Group;
import com.homies.app.domain.UserData;
import com.homies.app.domain.UserName;
import com.homies.app.repository.UserNameRepository;
import com.homies.app.service.criteria.UserNameCriteria;
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
 * Integration tests for the {@link UserNameResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserNameResourceIT {

    private static final String DEFAULT_USER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_USER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final String DEFAULT_TOKEN = "AAAAAAAAAA";
    private static final String UPDATED_TOKEN = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/user-names";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserNameRepository userNameRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserNameMockMvc;

    private UserName userName;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserName createEntity(EntityManager em) {
        UserName userName = new UserName().user_name(DEFAULT_USER_NAME).password(DEFAULT_PASSWORD).token(DEFAULT_TOKEN);
        // Add required entity
        UserData userData;
        if (TestUtil.findAll(em, UserData.class).isEmpty()) {
            userData = UserDataResourceIT.createEntity(em);
            em.persist(userData);
            em.flush();
        } else {
            userData = TestUtil.findAll(em, UserData.class).get(0);
        }
        userName.setUserData(userData);
        return userName;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserName createUpdatedEntity(EntityManager em) {
        UserName userName = new UserName().user_name(UPDATED_USER_NAME).password(UPDATED_PASSWORD).token(UPDATED_TOKEN);
        // Add required entity
        UserData userData;
        if (TestUtil.findAll(em, UserData.class).isEmpty()) {
            userData = UserDataResourceIT.createUpdatedEntity(em);
            em.persist(userData);
            em.flush();
        } else {
            userData = TestUtil.findAll(em, UserData.class).get(0);
        }
        userName.setUserData(userData);
        return userName;
    }

    @BeforeEach
    public void initTest() {
        userName = createEntity(em);
    }

    @Test
    @Transactional
    void createUserName() throws Exception {
        int databaseSizeBeforeCreate = userNameRepository.findAll().size();
        // Create the UserName
        restUserNameMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userName)))
            .andExpect(status().isCreated());

        // Validate the UserName in the database
        List<UserName> userNameList = userNameRepository.findAll();
        assertThat(userNameList).hasSize(databaseSizeBeforeCreate + 1);
        UserName testUserName = userNameList.get(userNameList.size() - 1);
        assertThat(testUserName.getUser_name()).isEqualTo(DEFAULT_USER_NAME);
        assertThat(testUserName.getPassword()).isEqualTo(DEFAULT_PASSWORD);
        assertThat(testUserName.getToken()).isEqualTo(DEFAULT_TOKEN);

        // Validate the id for MapsId, the ids must be same
        assertThat(testUserName.getId()).isEqualTo(testUserName.getUserData().getId());
    }

    @Test
    @Transactional
    void createUserNameWithExistingId() throws Exception {
        // Create the UserName with an existing ID
        userName.setId(1L);

        int databaseSizeBeforeCreate = userNameRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserNameMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userName)))
            .andExpect(status().isBadRequest());

        // Validate the UserName in the database
        List<UserName> userNameList = userNameRepository.findAll();
        assertThat(userNameList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void updateUserNameMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);
        int databaseSizeBeforeCreate = userNameRepository.findAll().size();

        // Add a new parent entity
        UserData userData = UserDataResourceIT.createUpdatedEntity(em);
        em.persist(userData);
        em.flush();

        // Load the userName
        UserName updatedUserName = userNameRepository.findById(userName.getId()).get();
        assertThat(updatedUserName).isNotNull();
        // Disconnect from session so that the updates on updatedUserName are not directly saved in db
        em.detach(updatedUserName);

        // Update the UserData with new association value
        updatedUserName.setUserData(userData);

        // Update the entity
        restUserNameMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUserName.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedUserName))
            )
            .andExpect(status().isOk());

        // Validate the UserName in the database
        List<UserName> userNameList = userNameRepository.findAll();
        assertThat(userNameList).hasSize(databaseSizeBeforeCreate);
        UserName testUserName = userNameList.get(userNameList.size() - 1);
        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testUserName.getId()).isEqualTo(testUserName.getUserData().getId());
    }

    @Test
    @Transactional
    void checkUser_nameIsRequired() throws Exception {
        int databaseSizeBeforeTest = userNameRepository.findAll().size();
        // set the field null
        userName.setUser_name(null);

        // Create the UserName, which fails.

        restUserNameMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userName)))
            .andExpect(status().isBadRequest());

        List<UserName> userNameList = userNameRepository.findAll();
        assertThat(userNameList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPasswordIsRequired() throws Exception {
        int databaseSizeBeforeTest = userNameRepository.findAll().size();
        // set the field null
        userName.setPassword(null);

        // Create the UserName, which fails.

        restUserNameMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userName)))
            .andExpect(status().isBadRequest());

        List<UserName> userNameList = userNameRepository.findAll();
        assertThat(userNameList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUserNames() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList
        restUserNameMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userName.getId().intValue())))
            .andExpect(jsonPath("$.[*].user_name").value(hasItem(DEFAULT_USER_NAME)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)))
            .andExpect(jsonPath("$.[*].token").value(hasItem(DEFAULT_TOKEN)));
    }

    @Test
    @Transactional
    void getUserName() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get the userName
        restUserNameMockMvc
            .perform(get(ENTITY_API_URL_ID, userName.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userName.getId().intValue()))
            .andExpect(jsonPath("$.user_name").value(DEFAULT_USER_NAME))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD))
            .andExpect(jsonPath("$.token").value(DEFAULT_TOKEN));
    }

    @Test
    @Transactional
    void getUserNamesByIdFiltering() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        Long id = userName.getId();

        defaultUserNameShouldBeFound("id.equals=" + id);
        defaultUserNameShouldNotBeFound("id.notEquals=" + id);

        defaultUserNameShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultUserNameShouldNotBeFound("id.greaterThan=" + id);

        defaultUserNameShouldBeFound("id.lessThanOrEqual=" + id);
        defaultUserNameShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllUserNamesByUser_nameIsEqualToSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where user_name equals to DEFAULT_USER_NAME
        defaultUserNameShouldBeFound("user_name.equals=" + DEFAULT_USER_NAME);

        // Get all the userNameList where user_name equals to UPDATED_USER_NAME
        defaultUserNameShouldNotBeFound("user_name.equals=" + UPDATED_USER_NAME);
    }

    @Test
    @Transactional
    void getAllUserNamesByUser_nameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where user_name not equals to DEFAULT_USER_NAME
        defaultUserNameShouldNotBeFound("user_name.notEquals=" + DEFAULT_USER_NAME);

        // Get all the userNameList where user_name not equals to UPDATED_USER_NAME
        defaultUserNameShouldBeFound("user_name.notEquals=" + UPDATED_USER_NAME);
    }

    @Test
    @Transactional
    void getAllUserNamesByUser_nameIsInShouldWork() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where user_name in DEFAULT_USER_NAME or UPDATED_USER_NAME
        defaultUserNameShouldBeFound("user_name.in=" + DEFAULT_USER_NAME + "," + UPDATED_USER_NAME);

        // Get all the userNameList where user_name equals to UPDATED_USER_NAME
        defaultUserNameShouldNotBeFound("user_name.in=" + UPDATED_USER_NAME);
    }

    @Test
    @Transactional
    void getAllUserNamesByUser_nameIsNullOrNotNull() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where user_name is not null
        defaultUserNameShouldBeFound("user_name.specified=true");

        // Get all the userNameList where user_name is null
        defaultUserNameShouldNotBeFound("user_name.specified=false");
    }

    @Test
    @Transactional
    void getAllUserNamesByUser_nameContainsSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where user_name contains DEFAULT_USER_NAME
        defaultUserNameShouldBeFound("user_name.contains=" + DEFAULT_USER_NAME);

        // Get all the userNameList where user_name contains UPDATED_USER_NAME
        defaultUserNameShouldNotBeFound("user_name.contains=" + UPDATED_USER_NAME);
    }

    @Test
    @Transactional
    void getAllUserNamesByUser_nameNotContainsSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where user_name does not contain DEFAULT_USER_NAME
        defaultUserNameShouldNotBeFound("user_name.doesNotContain=" + DEFAULT_USER_NAME);

        // Get all the userNameList where user_name does not contain UPDATED_USER_NAME
        defaultUserNameShouldBeFound("user_name.doesNotContain=" + UPDATED_USER_NAME);
    }

    @Test
    @Transactional
    void getAllUserNamesByPasswordIsEqualToSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where password equals to DEFAULT_PASSWORD
        defaultUserNameShouldBeFound("password.equals=" + DEFAULT_PASSWORD);

        // Get all the userNameList where password equals to UPDATED_PASSWORD
        defaultUserNameShouldNotBeFound("password.equals=" + UPDATED_PASSWORD);
    }

    @Test
    @Transactional
    void getAllUserNamesByPasswordIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where password not equals to DEFAULT_PASSWORD
        defaultUserNameShouldNotBeFound("password.notEquals=" + DEFAULT_PASSWORD);

        // Get all the userNameList where password not equals to UPDATED_PASSWORD
        defaultUserNameShouldBeFound("password.notEquals=" + UPDATED_PASSWORD);
    }

    @Test
    @Transactional
    void getAllUserNamesByPasswordIsInShouldWork() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where password in DEFAULT_PASSWORD or UPDATED_PASSWORD
        defaultUserNameShouldBeFound("password.in=" + DEFAULT_PASSWORD + "," + UPDATED_PASSWORD);

        // Get all the userNameList where password equals to UPDATED_PASSWORD
        defaultUserNameShouldNotBeFound("password.in=" + UPDATED_PASSWORD);
    }

    @Test
    @Transactional
    void getAllUserNamesByPasswordIsNullOrNotNull() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where password is not null
        defaultUserNameShouldBeFound("password.specified=true");

        // Get all the userNameList where password is null
        defaultUserNameShouldNotBeFound("password.specified=false");
    }

    @Test
    @Transactional
    void getAllUserNamesByPasswordContainsSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where password contains DEFAULT_PASSWORD
        defaultUserNameShouldBeFound("password.contains=" + DEFAULT_PASSWORD);

        // Get all the userNameList where password contains UPDATED_PASSWORD
        defaultUserNameShouldNotBeFound("password.contains=" + UPDATED_PASSWORD);
    }

    @Test
    @Transactional
    void getAllUserNamesByPasswordNotContainsSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where password does not contain DEFAULT_PASSWORD
        defaultUserNameShouldNotBeFound("password.doesNotContain=" + DEFAULT_PASSWORD);

        // Get all the userNameList where password does not contain UPDATED_PASSWORD
        defaultUserNameShouldBeFound("password.doesNotContain=" + UPDATED_PASSWORD);
    }

    @Test
    @Transactional
    void getAllUserNamesByTokenIsEqualToSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where token equals to DEFAULT_TOKEN
        defaultUserNameShouldBeFound("token.equals=" + DEFAULT_TOKEN);

        // Get all the userNameList where token equals to UPDATED_TOKEN
        defaultUserNameShouldNotBeFound("token.equals=" + UPDATED_TOKEN);
    }

    @Test
    @Transactional
    void getAllUserNamesByTokenIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where token not equals to DEFAULT_TOKEN
        defaultUserNameShouldNotBeFound("token.notEquals=" + DEFAULT_TOKEN);

        // Get all the userNameList where token not equals to UPDATED_TOKEN
        defaultUserNameShouldBeFound("token.notEquals=" + UPDATED_TOKEN);
    }

    @Test
    @Transactional
    void getAllUserNamesByTokenIsInShouldWork() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where token in DEFAULT_TOKEN or UPDATED_TOKEN
        defaultUserNameShouldBeFound("token.in=" + DEFAULT_TOKEN + "," + UPDATED_TOKEN);

        // Get all the userNameList where token equals to UPDATED_TOKEN
        defaultUserNameShouldNotBeFound("token.in=" + UPDATED_TOKEN);
    }

    @Test
    @Transactional
    void getAllUserNamesByTokenIsNullOrNotNull() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where token is not null
        defaultUserNameShouldBeFound("token.specified=true");

        // Get all the userNameList where token is null
        defaultUserNameShouldNotBeFound("token.specified=false");
    }

    @Test
    @Transactional
    void getAllUserNamesByTokenContainsSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where token contains DEFAULT_TOKEN
        defaultUserNameShouldBeFound("token.contains=" + DEFAULT_TOKEN);

        // Get all the userNameList where token contains UPDATED_TOKEN
        defaultUserNameShouldNotBeFound("token.contains=" + UPDATED_TOKEN);
    }

    @Test
    @Transactional
    void getAllUserNamesByTokenNotContainsSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where token does not contain DEFAULT_TOKEN
        defaultUserNameShouldNotBeFound("token.doesNotContain=" + DEFAULT_TOKEN);

        // Get all the userNameList where token does not contain UPDATED_TOKEN
        defaultUserNameShouldBeFound("token.doesNotContain=" + UPDATED_TOKEN);
    }

    @Test
    @Transactional
    void getAllUserNamesByUserDataIsEqualToSomething() throws Exception {
        // Get already existing entity
        UserData userData = userName.getUserData();
        userNameRepository.saveAndFlush(userName);
        Long userDataId = userData.getId();

        // Get all the userNameList where userData equals to userDataId
        defaultUserNameShouldBeFound("userDataId.equals=" + userDataId);

        // Get all the userNameList where userData equals to (userDataId + 1)
        defaultUserNameShouldNotBeFound("userDataId.equals=" + (userDataId + 1));
    }

    @Test
    @Transactional
    void getAllUserNamesByGroupIsEqualToSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);
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
        userName.addGroup(group);
        userNameRepository.saveAndFlush(userName);
        Long groupId = group.getId();

        // Get all the userNameList where group equals to groupId
        defaultUserNameShouldBeFound("groupId.equals=" + groupId);

        // Get all the userNameList where group equals to (groupId + 1)
        defaultUserNameShouldNotBeFound("groupId.equals=" + (groupId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultUserNameShouldBeFound(String filter) throws Exception {
        restUserNameMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userName.getId().intValue())))
            .andExpect(jsonPath("$.[*].user_name").value(hasItem(DEFAULT_USER_NAME)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)))
            .andExpect(jsonPath("$.[*].token").value(hasItem(DEFAULT_TOKEN)));

        // Check, that the count call also returns 1
        restUserNameMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultUserNameShouldNotBeFound(String filter) throws Exception {
        restUserNameMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUserNameMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingUserName() throws Exception {
        // Get the userName
        restUserNameMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewUserName() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        int databaseSizeBeforeUpdate = userNameRepository.findAll().size();

        // Update the userName
        UserName updatedUserName = userNameRepository.findById(userName.getId()).get();
        // Disconnect from session so that the updates on updatedUserName are not directly saved in db
        em.detach(updatedUserName);
        updatedUserName.user_name(UPDATED_USER_NAME).password(UPDATED_PASSWORD).token(UPDATED_TOKEN);

        restUserNameMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUserName.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedUserName))
            )
            .andExpect(status().isOk());

        // Validate the UserName in the database
        List<UserName> userNameList = userNameRepository.findAll();
        assertThat(userNameList).hasSize(databaseSizeBeforeUpdate);
        UserName testUserName = userNameList.get(userNameList.size() - 1);
        assertThat(testUserName.getUser_name()).isEqualTo(UPDATED_USER_NAME);
        assertThat(testUserName.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testUserName.getToken()).isEqualTo(UPDATED_TOKEN);
    }

    @Test
    @Transactional
    void putNonExistingUserName() throws Exception {
        int databaseSizeBeforeUpdate = userNameRepository.findAll().size();
        userName.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserNameMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userName.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userName))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserName in the database
        List<UserName> userNameList = userNameRepository.findAll();
        assertThat(userNameList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserName() throws Exception {
        int databaseSizeBeforeUpdate = userNameRepository.findAll().size();
        userName.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserNameMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userName))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserName in the database
        List<UserName> userNameList = userNameRepository.findAll();
        assertThat(userNameList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserName() throws Exception {
        int databaseSizeBeforeUpdate = userNameRepository.findAll().size();
        userName.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserNameMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userName)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserName in the database
        List<UserName> userNameList = userNameRepository.findAll();
        assertThat(userNameList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserNameWithPatch() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        int databaseSizeBeforeUpdate = userNameRepository.findAll().size();

        // Update the userName using partial update
        UserName partialUpdatedUserName = new UserName();
        partialUpdatedUserName.setId(userName.getId());

        partialUpdatedUserName.password(UPDATED_PASSWORD).token(UPDATED_TOKEN);

        restUserNameMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserName.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserName))
            )
            .andExpect(status().isOk());

        // Validate the UserName in the database
        List<UserName> userNameList = userNameRepository.findAll();
        assertThat(userNameList).hasSize(databaseSizeBeforeUpdate);
        UserName testUserName = userNameList.get(userNameList.size() - 1);
        assertThat(testUserName.getUser_name()).isEqualTo(DEFAULT_USER_NAME);
        assertThat(testUserName.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testUserName.getToken()).isEqualTo(UPDATED_TOKEN);
    }

    @Test
    @Transactional
    void fullUpdateUserNameWithPatch() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        int databaseSizeBeforeUpdate = userNameRepository.findAll().size();

        // Update the userName using partial update
        UserName partialUpdatedUserName = new UserName();
        partialUpdatedUserName.setId(userName.getId());

        partialUpdatedUserName.user_name(UPDATED_USER_NAME).password(UPDATED_PASSWORD).token(UPDATED_TOKEN);

        restUserNameMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserName.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserName))
            )
            .andExpect(status().isOk());

        // Validate the UserName in the database
        List<UserName> userNameList = userNameRepository.findAll();
        assertThat(userNameList).hasSize(databaseSizeBeforeUpdate);
        UserName testUserName = userNameList.get(userNameList.size() - 1);
        assertThat(testUserName.getUser_name()).isEqualTo(UPDATED_USER_NAME);
        assertThat(testUserName.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testUserName.getToken()).isEqualTo(UPDATED_TOKEN);
    }

    @Test
    @Transactional
    void patchNonExistingUserName() throws Exception {
        int databaseSizeBeforeUpdate = userNameRepository.findAll().size();
        userName.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserNameMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userName.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userName))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserName in the database
        List<UserName> userNameList = userNameRepository.findAll();
        assertThat(userNameList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserName() throws Exception {
        int databaseSizeBeforeUpdate = userNameRepository.findAll().size();
        userName.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserNameMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userName))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserName in the database
        List<UserName> userNameList = userNameRepository.findAll();
        assertThat(userNameList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserName() throws Exception {
        int databaseSizeBeforeUpdate = userNameRepository.findAll().size();
        userName.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserNameMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(userName)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserName in the database
        List<UserName> userNameList = userNameRepository.findAll();
        assertThat(userNameList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserName() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        int databaseSizeBeforeDelete = userNameRepository.findAll().size();

        // Delete the userName
        restUserNameMockMvc
            .perform(delete(ENTITY_API_URL_ID, userName.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserName> userNameList = userNameRepository.findAll();
        assertThat(userNameList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
