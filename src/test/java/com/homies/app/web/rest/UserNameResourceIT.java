package com.homies.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.homies.app.IntegrationTest;
import com.homies.app.domain.Group;
import com.homies.app.domain.UserName;
import com.homies.app.repository.UserNameRepository;
import com.homies.app.service.criteria.UserNameCriteria;
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
 * Integration tests for the {@link UserNameResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserNameResourceIT {

    private static final String DEFAULT_EMAIL = "vX@]?I'y.B4y!\"d";
    private static final String UPDATED_EMAIL = "w[@2][gR.8SHX";

    private static final String DEFAULT_NICK = "AAAAAAAAAA";
    private static final String UPDATED_NICK = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SURNAME = "AAAAAAAAAA";
    private static final String UPDATED_SURNAME = "BBBBBBBBBB";

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
        UserName userName = new UserName()
            .email(DEFAULT_EMAIL)
            .nick(DEFAULT_NICK)
            .password(DEFAULT_PASSWORD)
            .name(DEFAULT_NAME)
            .surname(DEFAULT_SURNAME)
            .photo(DEFAULT_PHOTO)
            .photoContentType(DEFAULT_PHOTO_CONTENT_TYPE)
            .phone(DEFAULT_PHONE)
            .premium(DEFAULT_PREMIUM)
            .birthDate(DEFAULT_BIRTH_DATE)
            .addDate(DEFAULT_ADD_DATE)
            .token(DEFAULT_TOKEN);
        return userName;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserName createUpdatedEntity(EntityManager em) {
        UserName userName = new UserName()
            .email(UPDATED_EMAIL)
            .nick(UPDATED_NICK)
            .password(UPDATED_PASSWORD)
            .name(UPDATED_NAME)
            .surname(UPDATED_SURNAME)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .phone(UPDATED_PHONE)
            .premium(UPDATED_PREMIUM)
            .birthDate(UPDATED_BIRTH_DATE)
            .addDate(UPDATED_ADD_DATE)
            .token(UPDATED_TOKEN);
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
        assertThat(testUserName.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testUserName.getNick()).isEqualTo(DEFAULT_NICK);
        assertThat(testUserName.getPassword()).isEqualTo(DEFAULT_PASSWORD);
        assertThat(testUserName.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testUserName.getSurname()).isEqualTo(DEFAULT_SURNAME);
        assertThat(testUserName.getPhoto()).isEqualTo(DEFAULT_PHOTO);
        assertThat(testUserName.getPhotoContentType()).isEqualTo(DEFAULT_PHOTO_CONTENT_TYPE);
        assertThat(testUserName.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testUserName.getPremium()).isEqualTo(DEFAULT_PREMIUM);
        assertThat(testUserName.getBirthDate()).isEqualTo(DEFAULT_BIRTH_DATE);
        assertThat(testUserName.getAddDate()).isEqualTo(DEFAULT_ADD_DATE);
        assertThat(testUserName.getToken()).isEqualTo(DEFAULT_TOKEN);
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
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = userNameRepository.findAll().size();
        // set the field null
        userName.setEmail(null);

        // Create the UserName, which fails.

        restUserNameMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userName)))
            .andExpect(status().isBadRequest());

        List<UserName> userNameList = userNameRepository.findAll();
        assertThat(userNameList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNickIsRequired() throws Exception {
        int databaseSizeBeforeTest = userNameRepository.findAll().size();
        // set the field null
        userName.setNick(null);

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
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = userNameRepository.findAll().size();
        // set the field null
        userName.setName(null);

        // Create the UserName, which fails.

        restUserNameMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userName)))
            .andExpect(status().isBadRequest());

        List<UserName> userNameList = userNameRepository.findAll();
        assertThat(userNameList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSurnameIsRequired() throws Exception {
        int databaseSizeBeforeTest = userNameRepository.findAll().size();
        // set the field null
        userName.setSurname(null);

        // Create the UserName, which fails.

        restUserNameMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userName)))
            .andExpect(status().isBadRequest());

        List<UserName> userNameList = userNameRepository.findAll();
        assertThat(userNameList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPremiumIsRequired() throws Exception {
        int databaseSizeBeforeTest = userNameRepository.findAll().size();
        // set the field null
        userName.setPremium(null);

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
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].nick").value(hasItem(DEFAULT_NICK)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].surname").value(hasItem(DEFAULT_SURNAME)))
            .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64Utils.encodeToString(DEFAULT_PHOTO))))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].premium").value(hasItem(DEFAULT_PREMIUM.booleanValue())))
            .andExpect(jsonPath("$.[*].birthDate").value(hasItem(DEFAULT_BIRTH_DATE.toString())))
            .andExpect(jsonPath("$.[*].addDate").value(hasItem(DEFAULT_ADD_DATE.toString())))
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
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.nick").value(DEFAULT_NICK))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.surname").value(DEFAULT_SURNAME))
            .andExpect(jsonPath("$.photoContentType").value(DEFAULT_PHOTO_CONTENT_TYPE))
            .andExpect(jsonPath("$.photo").value(Base64Utils.encodeToString(DEFAULT_PHOTO)))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.premium").value(DEFAULT_PREMIUM.booleanValue()))
            .andExpect(jsonPath("$.birthDate").value(DEFAULT_BIRTH_DATE.toString()))
            .andExpect(jsonPath("$.addDate").value(DEFAULT_ADD_DATE.toString()))
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
    void getAllUserNamesByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where email equals to DEFAULT_EMAIL
        defaultUserNameShouldBeFound("email.equals=" + DEFAULT_EMAIL);

        // Get all the userNameList where email equals to UPDATED_EMAIL
        defaultUserNameShouldNotBeFound("email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllUserNamesByEmailIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where email not equals to DEFAULT_EMAIL
        defaultUserNameShouldNotBeFound("email.notEquals=" + DEFAULT_EMAIL);

        // Get all the userNameList where email not equals to UPDATED_EMAIL
        defaultUserNameShouldBeFound("email.notEquals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllUserNamesByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultUserNameShouldBeFound("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL);

        // Get all the userNameList where email equals to UPDATED_EMAIL
        defaultUserNameShouldNotBeFound("email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllUserNamesByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where email is not null
        defaultUserNameShouldBeFound("email.specified=true");

        // Get all the userNameList where email is null
        defaultUserNameShouldNotBeFound("email.specified=false");
    }

    @Test
    @Transactional
    void getAllUserNamesByEmailContainsSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where email contains DEFAULT_EMAIL
        defaultUserNameShouldBeFound("email.contains=" + DEFAULT_EMAIL);

        // Get all the userNameList where email contains UPDATED_EMAIL
        defaultUserNameShouldNotBeFound("email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllUserNamesByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where email does not contain DEFAULT_EMAIL
        defaultUserNameShouldNotBeFound("email.doesNotContain=" + DEFAULT_EMAIL);

        // Get all the userNameList where email does not contain UPDATED_EMAIL
        defaultUserNameShouldBeFound("email.doesNotContain=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllUserNamesByNickIsEqualToSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where nick equals to DEFAULT_NICK
        defaultUserNameShouldBeFound("nick.equals=" + DEFAULT_NICK);

        // Get all the userNameList where nick equals to UPDATED_NICK
        defaultUserNameShouldNotBeFound("nick.equals=" + UPDATED_NICK);
    }

    @Test
    @Transactional
    void getAllUserNamesByNickIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where nick not equals to DEFAULT_NICK
        defaultUserNameShouldNotBeFound("nick.notEquals=" + DEFAULT_NICK);

        // Get all the userNameList where nick not equals to UPDATED_NICK
        defaultUserNameShouldBeFound("nick.notEquals=" + UPDATED_NICK);
    }

    @Test
    @Transactional
    void getAllUserNamesByNickIsInShouldWork() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where nick in DEFAULT_NICK or UPDATED_NICK
        defaultUserNameShouldBeFound("nick.in=" + DEFAULT_NICK + "," + UPDATED_NICK);

        // Get all the userNameList where nick equals to UPDATED_NICK
        defaultUserNameShouldNotBeFound("nick.in=" + UPDATED_NICK);
    }

    @Test
    @Transactional
    void getAllUserNamesByNickIsNullOrNotNull() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where nick is not null
        defaultUserNameShouldBeFound("nick.specified=true");

        // Get all the userNameList where nick is null
        defaultUserNameShouldNotBeFound("nick.specified=false");
    }

    @Test
    @Transactional
    void getAllUserNamesByNickContainsSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where nick contains DEFAULT_NICK
        defaultUserNameShouldBeFound("nick.contains=" + DEFAULT_NICK);

        // Get all the userNameList where nick contains UPDATED_NICK
        defaultUserNameShouldNotBeFound("nick.contains=" + UPDATED_NICK);
    }

    @Test
    @Transactional
    void getAllUserNamesByNickNotContainsSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where nick does not contain DEFAULT_NICK
        defaultUserNameShouldNotBeFound("nick.doesNotContain=" + DEFAULT_NICK);

        // Get all the userNameList where nick does not contain UPDATED_NICK
        defaultUserNameShouldBeFound("nick.doesNotContain=" + UPDATED_NICK);
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
    void getAllUserNamesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where name equals to DEFAULT_NAME
        defaultUserNameShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the userNameList where name equals to UPDATED_NAME
        defaultUserNameShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllUserNamesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where name not equals to DEFAULT_NAME
        defaultUserNameShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the userNameList where name not equals to UPDATED_NAME
        defaultUserNameShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllUserNamesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where name in DEFAULT_NAME or UPDATED_NAME
        defaultUserNameShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the userNameList where name equals to UPDATED_NAME
        defaultUserNameShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllUserNamesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where name is not null
        defaultUserNameShouldBeFound("name.specified=true");

        // Get all the userNameList where name is null
        defaultUserNameShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllUserNamesByNameContainsSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where name contains DEFAULT_NAME
        defaultUserNameShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the userNameList where name contains UPDATED_NAME
        defaultUserNameShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllUserNamesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where name does not contain DEFAULT_NAME
        defaultUserNameShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the userNameList where name does not contain UPDATED_NAME
        defaultUserNameShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllUserNamesBySurnameIsEqualToSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where surname equals to DEFAULT_SURNAME
        defaultUserNameShouldBeFound("surname.equals=" + DEFAULT_SURNAME);

        // Get all the userNameList where surname equals to UPDATED_SURNAME
        defaultUserNameShouldNotBeFound("surname.equals=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    void getAllUserNamesBySurnameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where surname not equals to DEFAULT_SURNAME
        defaultUserNameShouldNotBeFound("surname.notEquals=" + DEFAULT_SURNAME);

        // Get all the userNameList where surname not equals to UPDATED_SURNAME
        defaultUserNameShouldBeFound("surname.notEquals=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    void getAllUserNamesBySurnameIsInShouldWork() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where surname in DEFAULT_SURNAME or UPDATED_SURNAME
        defaultUserNameShouldBeFound("surname.in=" + DEFAULT_SURNAME + "," + UPDATED_SURNAME);

        // Get all the userNameList where surname equals to UPDATED_SURNAME
        defaultUserNameShouldNotBeFound("surname.in=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    void getAllUserNamesBySurnameIsNullOrNotNull() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where surname is not null
        defaultUserNameShouldBeFound("surname.specified=true");

        // Get all the userNameList where surname is null
        defaultUserNameShouldNotBeFound("surname.specified=false");
    }

    @Test
    @Transactional
    void getAllUserNamesBySurnameContainsSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where surname contains DEFAULT_SURNAME
        defaultUserNameShouldBeFound("surname.contains=" + DEFAULT_SURNAME);

        // Get all the userNameList where surname contains UPDATED_SURNAME
        defaultUserNameShouldNotBeFound("surname.contains=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    void getAllUserNamesBySurnameNotContainsSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where surname does not contain DEFAULT_SURNAME
        defaultUserNameShouldNotBeFound("surname.doesNotContain=" + DEFAULT_SURNAME);

        // Get all the userNameList where surname does not contain UPDATED_SURNAME
        defaultUserNameShouldBeFound("surname.doesNotContain=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    void getAllUserNamesByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where phone equals to DEFAULT_PHONE
        defaultUserNameShouldBeFound("phone.equals=" + DEFAULT_PHONE);

        // Get all the userNameList where phone equals to UPDATED_PHONE
        defaultUserNameShouldNotBeFound("phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllUserNamesByPhoneIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where phone not equals to DEFAULT_PHONE
        defaultUserNameShouldNotBeFound("phone.notEquals=" + DEFAULT_PHONE);

        // Get all the userNameList where phone not equals to UPDATED_PHONE
        defaultUserNameShouldBeFound("phone.notEquals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllUserNamesByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where phone in DEFAULT_PHONE or UPDATED_PHONE
        defaultUserNameShouldBeFound("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE);

        // Get all the userNameList where phone equals to UPDATED_PHONE
        defaultUserNameShouldNotBeFound("phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllUserNamesByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where phone is not null
        defaultUserNameShouldBeFound("phone.specified=true");

        // Get all the userNameList where phone is null
        defaultUserNameShouldNotBeFound("phone.specified=false");
    }

    @Test
    @Transactional
    void getAllUserNamesByPhoneContainsSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where phone contains DEFAULT_PHONE
        defaultUserNameShouldBeFound("phone.contains=" + DEFAULT_PHONE);

        // Get all the userNameList where phone contains UPDATED_PHONE
        defaultUserNameShouldNotBeFound("phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllUserNamesByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where phone does not contain DEFAULT_PHONE
        defaultUserNameShouldNotBeFound("phone.doesNotContain=" + DEFAULT_PHONE);

        // Get all the userNameList where phone does not contain UPDATED_PHONE
        defaultUserNameShouldBeFound("phone.doesNotContain=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllUserNamesByPremiumIsEqualToSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where premium equals to DEFAULT_PREMIUM
        defaultUserNameShouldBeFound("premium.equals=" + DEFAULT_PREMIUM);

        // Get all the userNameList where premium equals to UPDATED_PREMIUM
        defaultUserNameShouldNotBeFound("premium.equals=" + UPDATED_PREMIUM);
    }

    @Test
    @Transactional
    void getAllUserNamesByPremiumIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where premium not equals to DEFAULT_PREMIUM
        defaultUserNameShouldNotBeFound("premium.notEquals=" + DEFAULT_PREMIUM);

        // Get all the userNameList where premium not equals to UPDATED_PREMIUM
        defaultUserNameShouldBeFound("premium.notEquals=" + UPDATED_PREMIUM);
    }

    @Test
    @Transactional
    void getAllUserNamesByPremiumIsInShouldWork() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where premium in DEFAULT_PREMIUM or UPDATED_PREMIUM
        defaultUserNameShouldBeFound("premium.in=" + DEFAULT_PREMIUM + "," + UPDATED_PREMIUM);

        // Get all the userNameList where premium equals to UPDATED_PREMIUM
        defaultUserNameShouldNotBeFound("premium.in=" + UPDATED_PREMIUM);
    }

    @Test
    @Transactional
    void getAllUserNamesByPremiumIsNullOrNotNull() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where premium is not null
        defaultUserNameShouldBeFound("premium.specified=true");

        // Get all the userNameList where premium is null
        defaultUserNameShouldNotBeFound("premium.specified=false");
    }

    @Test
    @Transactional
    void getAllUserNamesByBirthDateIsEqualToSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where birthDate equals to DEFAULT_BIRTH_DATE
        defaultUserNameShouldBeFound("birthDate.equals=" + DEFAULT_BIRTH_DATE);

        // Get all the userNameList where birthDate equals to UPDATED_BIRTH_DATE
        defaultUserNameShouldNotBeFound("birthDate.equals=" + UPDATED_BIRTH_DATE);
    }

    @Test
    @Transactional
    void getAllUserNamesByBirthDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where birthDate not equals to DEFAULT_BIRTH_DATE
        defaultUserNameShouldNotBeFound("birthDate.notEquals=" + DEFAULT_BIRTH_DATE);

        // Get all the userNameList where birthDate not equals to UPDATED_BIRTH_DATE
        defaultUserNameShouldBeFound("birthDate.notEquals=" + UPDATED_BIRTH_DATE);
    }

    @Test
    @Transactional
    void getAllUserNamesByBirthDateIsInShouldWork() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where birthDate in DEFAULT_BIRTH_DATE or UPDATED_BIRTH_DATE
        defaultUserNameShouldBeFound("birthDate.in=" + DEFAULT_BIRTH_DATE + "," + UPDATED_BIRTH_DATE);

        // Get all the userNameList where birthDate equals to UPDATED_BIRTH_DATE
        defaultUserNameShouldNotBeFound("birthDate.in=" + UPDATED_BIRTH_DATE);
    }

    @Test
    @Transactional
    void getAllUserNamesByBirthDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where birthDate is not null
        defaultUserNameShouldBeFound("birthDate.specified=true");

        // Get all the userNameList where birthDate is null
        defaultUserNameShouldNotBeFound("birthDate.specified=false");
    }

    @Test
    @Transactional
    void getAllUserNamesByBirthDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where birthDate is greater than or equal to DEFAULT_BIRTH_DATE
        defaultUserNameShouldBeFound("birthDate.greaterThanOrEqual=" + DEFAULT_BIRTH_DATE);

        // Get all the userNameList where birthDate is greater than or equal to UPDATED_BIRTH_DATE
        defaultUserNameShouldNotBeFound("birthDate.greaterThanOrEqual=" + UPDATED_BIRTH_DATE);
    }

    @Test
    @Transactional
    void getAllUserNamesByBirthDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where birthDate is less than or equal to DEFAULT_BIRTH_DATE
        defaultUserNameShouldBeFound("birthDate.lessThanOrEqual=" + DEFAULT_BIRTH_DATE);

        // Get all the userNameList where birthDate is less than or equal to SMALLER_BIRTH_DATE
        defaultUserNameShouldNotBeFound("birthDate.lessThanOrEqual=" + SMALLER_BIRTH_DATE);
    }

    @Test
    @Transactional
    void getAllUserNamesByBirthDateIsLessThanSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where birthDate is less than DEFAULT_BIRTH_DATE
        defaultUserNameShouldNotBeFound("birthDate.lessThan=" + DEFAULT_BIRTH_DATE);

        // Get all the userNameList where birthDate is less than UPDATED_BIRTH_DATE
        defaultUserNameShouldBeFound("birthDate.lessThan=" + UPDATED_BIRTH_DATE);
    }

    @Test
    @Transactional
    void getAllUserNamesByBirthDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where birthDate is greater than DEFAULT_BIRTH_DATE
        defaultUserNameShouldNotBeFound("birthDate.greaterThan=" + DEFAULT_BIRTH_DATE);

        // Get all the userNameList where birthDate is greater than SMALLER_BIRTH_DATE
        defaultUserNameShouldBeFound("birthDate.greaterThan=" + SMALLER_BIRTH_DATE);
    }

    @Test
    @Transactional
    void getAllUserNamesByAddDateIsEqualToSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where addDate equals to DEFAULT_ADD_DATE
        defaultUserNameShouldBeFound("addDate.equals=" + DEFAULT_ADD_DATE);

        // Get all the userNameList where addDate equals to UPDATED_ADD_DATE
        defaultUserNameShouldNotBeFound("addDate.equals=" + UPDATED_ADD_DATE);
    }

    @Test
    @Transactional
    void getAllUserNamesByAddDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where addDate not equals to DEFAULT_ADD_DATE
        defaultUserNameShouldNotBeFound("addDate.notEquals=" + DEFAULT_ADD_DATE);

        // Get all the userNameList where addDate not equals to UPDATED_ADD_DATE
        defaultUserNameShouldBeFound("addDate.notEquals=" + UPDATED_ADD_DATE);
    }

    @Test
    @Transactional
    void getAllUserNamesByAddDateIsInShouldWork() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where addDate in DEFAULT_ADD_DATE or UPDATED_ADD_DATE
        defaultUserNameShouldBeFound("addDate.in=" + DEFAULT_ADD_DATE + "," + UPDATED_ADD_DATE);

        // Get all the userNameList where addDate equals to UPDATED_ADD_DATE
        defaultUserNameShouldNotBeFound("addDate.in=" + UPDATED_ADD_DATE);
    }

    @Test
    @Transactional
    void getAllUserNamesByAddDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where addDate is not null
        defaultUserNameShouldBeFound("addDate.specified=true");

        // Get all the userNameList where addDate is null
        defaultUserNameShouldNotBeFound("addDate.specified=false");
    }

    @Test
    @Transactional
    void getAllUserNamesByAddDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where addDate is greater than or equal to DEFAULT_ADD_DATE
        defaultUserNameShouldBeFound("addDate.greaterThanOrEqual=" + DEFAULT_ADD_DATE);

        // Get all the userNameList where addDate is greater than or equal to UPDATED_ADD_DATE
        defaultUserNameShouldNotBeFound("addDate.greaterThanOrEqual=" + UPDATED_ADD_DATE);
    }

    @Test
    @Transactional
    void getAllUserNamesByAddDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where addDate is less than or equal to DEFAULT_ADD_DATE
        defaultUserNameShouldBeFound("addDate.lessThanOrEqual=" + DEFAULT_ADD_DATE);

        // Get all the userNameList where addDate is less than or equal to SMALLER_ADD_DATE
        defaultUserNameShouldNotBeFound("addDate.lessThanOrEqual=" + SMALLER_ADD_DATE);
    }

    @Test
    @Transactional
    void getAllUserNamesByAddDateIsLessThanSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where addDate is less than DEFAULT_ADD_DATE
        defaultUserNameShouldNotBeFound("addDate.lessThan=" + DEFAULT_ADD_DATE);

        // Get all the userNameList where addDate is less than UPDATED_ADD_DATE
        defaultUserNameShouldBeFound("addDate.lessThan=" + UPDATED_ADD_DATE);
    }

    @Test
    @Transactional
    void getAllUserNamesByAddDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        userNameRepository.saveAndFlush(userName);

        // Get all the userNameList where addDate is greater than DEFAULT_ADD_DATE
        defaultUserNameShouldNotBeFound("addDate.greaterThan=" + DEFAULT_ADD_DATE);

        // Get all the userNameList where addDate is greater than SMALLER_ADD_DATE
        defaultUserNameShouldBeFound("addDate.greaterThan=" + SMALLER_ADD_DATE);
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
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].nick").value(hasItem(DEFAULT_NICK)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].surname").value(hasItem(DEFAULT_SURNAME)))
            .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64Utils.encodeToString(DEFAULT_PHOTO))))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].premium").value(hasItem(DEFAULT_PREMIUM.booleanValue())))
            .andExpect(jsonPath("$.[*].birthDate").value(hasItem(DEFAULT_BIRTH_DATE.toString())))
            .andExpect(jsonPath("$.[*].addDate").value(hasItem(DEFAULT_ADD_DATE.toString())))
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
        updatedUserName
            .email(UPDATED_EMAIL)
            .nick(UPDATED_NICK)
            .password(UPDATED_PASSWORD)
            .name(UPDATED_NAME)
            .surname(UPDATED_SURNAME)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .phone(UPDATED_PHONE)
            .premium(UPDATED_PREMIUM)
            .birthDate(UPDATED_BIRTH_DATE)
            .addDate(UPDATED_ADD_DATE)
            .token(UPDATED_TOKEN);

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
        assertThat(testUserName.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testUserName.getNick()).isEqualTo(UPDATED_NICK);
        assertThat(testUserName.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testUserName.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUserName.getSurname()).isEqualTo(UPDATED_SURNAME);
        assertThat(testUserName.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testUserName.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testUserName.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testUserName.getPremium()).isEqualTo(UPDATED_PREMIUM);
        assertThat(testUserName.getBirthDate()).isEqualTo(UPDATED_BIRTH_DATE);
        assertThat(testUserName.getAddDate()).isEqualTo(UPDATED_ADD_DATE);
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

        partialUpdatedUserName.name(UPDATED_NAME).addDate(UPDATED_ADD_DATE).token(UPDATED_TOKEN);

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
        assertThat(testUserName.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testUserName.getNick()).isEqualTo(DEFAULT_NICK);
        assertThat(testUserName.getPassword()).isEqualTo(DEFAULT_PASSWORD);
        assertThat(testUserName.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUserName.getSurname()).isEqualTo(DEFAULT_SURNAME);
        assertThat(testUserName.getPhoto()).isEqualTo(DEFAULT_PHOTO);
        assertThat(testUserName.getPhotoContentType()).isEqualTo(DEFAULT_PHOTO_CONTENT_TYPE);
        assertThat(testUserName.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testUserName.getPremium()).isEqualTo(DEFAULT_PREMIUM);
        assertThat(testUserName.getBirthDate()).isEqualTo(DEFAULT_BIRTH_DATE);
        assertThat(testUserName.getAddDate()).isEqualTo(UPDATED_ADD_DATE);
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

        partialUpdatedUserName
            .email(UPDATED_EMAIL)
            .nick(UPDATED_NICK)
            .password(UPDATED_PASSWORD)
            .name(UPDATED_NAME)
            .surname(UPDATED_SURNAME)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .phone(UPDATED_PHONE)
            .premium(UPDATED_PREMIUM)
            .birthDate(UPDATED_BIRTH_DATE)
            .addDate(UPDATED_ADD_DATE)
            .token(UPDATED_TOKEN);

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
        assertThat(testUserName.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testUserName.getNick()).isEqualTo(UPDATED_NICK);
        assertThat(testUserName.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testUserName.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUserName.getSurname()).isEqualTo(UPDATED_SURNAME);
        assertThat(testUserName.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testUserName.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testUserName.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testUserName.getPremium()).isEqualTo(UPDATED_PREMIUM);
        assertThat(testUserName.getBirthDate()).isEqualTo(UPDATED_BIRTH_DATE);
        assertThat(testUserName.getAddDate()).isEqualTo(UPDATED_ADD_DATE);
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
