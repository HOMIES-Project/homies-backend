package com.homies.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.homies.app.IntegrationTest;
import com.homies.app.domain.Group;
import com.homies.app.domain.SettingsList;
import com.homies.app.domain.SpendingList;
import com.homies.app.domain.UserPending;
import com.homies.app.repository.SettingsListRepository;
import com.homies.app.service.criteria.SettingsListCriteria;
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
 * Integration tests for the {@link SettingsListResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SettingsListResourceIT {

    private static final Boolean DEFAULT_SETTING_ONE = false;
    private static final Boolean UPDATED_SETTING_ONE = true;

    private static final Boolean DEFAULT_SETTING_TWO = false;
    private static final Boolean UPDATED_SETTING_TWO = true;

    private static final Boolean DEFAULT_SETTING_THREE = false;
    private static final Boolean UPDATED_SETTING_THREE = true;

    private static final Boolean DEFAULT_SETTING_FOUR = false;
    private static final Boolean UPDATED_SETTING_FOUR = true;

    private static final Boolean DEFAULT_SETTING_FIVE = false;
    private static final Boolean UPDATED_SETTING_FIVE = true;

    private static final Boolean DEFAULT_SETTING_SIX = false;
    private static final Boolean UPDATED_SETTING_SIX = true;

    private static final Boolean DEFAULT_SETTING_SEVEN = false;
    private static final Boolean UPDATED_SETTING_SEVEN = true;

    private static final String ENTITY_API_URL = "/api/settings-lists";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SettingsListRepository settingsListRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSettingsListMockMvc;

    private SettingsList settingsList;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SettingsList createEntity(EntityManager em) {
        SettingsList settingsList = new SettingsList()
            .settingOne(DEFAULT_SETTING_ONE)
            .settingTwo(DEFAULT_SETTING_TWO)
            .settingThree(DEFAULT_SETTING_THREE)
            .settingFour(DEFAULT_SETTING_FOUR)
            .settingFive(DEFAULT_SETTING_FIVE)
            .settingSix(DEFAULT_SETTING_SIX)
            .settingSeven(DEFAULT_SETTING_SEVEN);
        // Add required entity
        Group group;
        if (TestUtil.findAll(em, Group.class).isEmpty()) {
            group = GroupResourceIT.createEntity(em);
            em.persist(group);
            em.flush();
        } else {
            group = TestUtil.findAll(em, Group.class).get(0);
        }
        settingsList.setGroup(group);
        return settingsList;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SettingsList createUpdatedEntity(EntityManager em) {
        SettingsList settingsList = new SettingsList()
            .settingOne(UPDATED_SETTING_ONE)
            .settingTwo(UPDATED_SETTING_TWO)
            .settingThree(UPDATED_SETTING_THREE)
            .settingFour(UPDATED_SETTING_FOUR)
            .settingFive(UPDATED_SETTING_FIVE)
            .settingSix(UPDATED_SETTING_SIX)
            .settingSeven(UPDATED_SETTING_SEVEN);
        // Add required entity
        Group group;
        if (TestUtil.findAll(em, Group.class).isEmpty()) {
            group = GroupResourceIT.createUpdatedEntity(em);
            em.persist(group);
            em.flush();
        } else {
            group = TestUtil.findAll(em, Group.class).get(0);
        }
        settingsList.setGroup(group);
        return settingsList;
    }

    @BeforeEach
    public void initTest() {
        settingsList = createEntity(em);
    }

    @Test
    @Transactional
    void createSettingsList() throws Exception {
        int databaseSizeBeforeCreate = settingsListRepository.findAll().size();
        // Create the SettingsList
        restSettingsListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(settingsList)))
            .andExpect(status().isCreated());

        // Validate the SettingsList in the database
        List<SettingsList> settingsListList = settingsListRepository.findAll();
        assertThat(settingsListList).hasSize(databaseSizeBeforeCreate + 1);
        SettingsList testSettingsList = settingsListList.get(settingsListList.size() - 1);
        assertThat(testSettingsList.getSettingOne()).isEqualTo(DEFAULT_SETTING_ONE);
        assertThat(testSettingsList.getSettingTwo()).isEqualTo(DEFAULT_SETTING_TWO);
        assertThat(testSettingsList.getSettingThree()).isEqualTo(DEFAULT_SETTING_THREE);
        assertThat(testSettingsList.getSettingFour()).isEqualTo(DEFAULT_SETTING_FOUR);
        assertThat(testSettingsList.getSettingFive()).isEqualTo(DEFAULT_SETTING_FIVE);
        assertThat(testSettingsList.getSettingSix()).isEqualTo(DEFAULT_SETTING_SIX);
        assertThat(testSettingsList.getSettingSeven()).isEqualTo(DEFAULT_SETTING_SEVEN);

        // Validate the id for MapsId, the ids must be same
        assertThat(testSettingsList.getId()).isEqualTo(testSettingsList.getGroup().getId());
    }

    @Test
    @Transactional
    void createSettingsListWithExistingId() throws Exception {
        // Create the SettingsList with an existing ID
        settingsList.setId(1L);

        int databaseSizeBeforeCreate = settingsListRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSettingsListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(settingsList)))
            .andExpect(status().isBadRequest());

        // Validate the SettingsList in the database
        List<SettingsList> settingsListList = settingsListRepository.findAll();
        assertThat(settingsListList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void updateSettingsListMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);
        int databaseSizeBeforeCreate = settingsListRepository.findAll().size();

        // Load the settingsList
        SettingsList updatedSettingsList = settingsListRepository.findById(settingsList.getId()).get();
        assertThat(updatedSettingsList).isNotNull();
        // Disconnect from session so that the updates on updatedSettingsList are not directly saved in db
        em.detach(updatedSettingsList);

        // Update the Group with new association value
        updatedSettingsList.setGroup(settingsList.getGroup());

        // Update the entity
        restSettingsListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSettingsList.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSettingsList))
            )
            .andExpect(status().isOk());

        // Validate the SettingsList in the database
        List<SettingsList> settingsListList = settingsListRepository.findAll();
        assertThat(settingsListList).hasSize(databaseSizeBeforeCreate);
        SettingsList testSettingsList = settingsListList.get(settingsListList.size() - 1);
        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testSettingsList.getId()).isEqualTo(testSettingsList.getGroup().getId());
    }

    @Test
    @Transactional
    void getAllSettingsLists() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get all the settingsListList
        restSettingsListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(settingsList.getId().intValue())))
            .andExpect(jsonPath("$.[*].settingOne").value(hasItem(DEFAULT_SETTING_ONE.booleanValue())))
            .andExpect(jsonPath("$.[*].settingTwo").value(hasItem(DEFAULT_SETTING_TWO.booleanValue())))
            .andExpect(jsonPath("$.[*].settingThree").value(hasItem(DEFAULT_SETTING_THREE.booleanValue())))
            .andExpect(jsonPath("$.[*].settingFour").value(hasItem(DEFAULT_SETTING_FOUR.booleanValue())))
            .andExpect(jsonPath("$.[*].settingFive").value(hasItem(DEFAULT_SETTING_FIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].settingSix").value(hasItem(DEFAULT_SETTING_SIX.booleanValue())))
            .andExpect(jsonPath("$.[*].settingSeven").value(hasItem(DEFAULT_SETTING_SEVEN.booleanValue())));
    }

    @Test
    @Transactional
    void getSettingsList() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get the settingsList
        restSettingsListMockMvc
            .perform(get(ENTITY_API_URL_ID, settingsList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(settingsList.getId().intValue()))
            .andExpect(jsonPath("$.settingOne").value(DEFAULT_SETTING_ONE.booleanValue()))
            .andExpect(jsonPath("$.settingTwo").value(DEFAULT_SETTING_TWO.booleanValue()))
            .andExpect(jsonPath("$.settingThree").value(DEFAULT_SETTING_THREE.booleanValue()))
            .andExpect(jsonPath("$.settingFour").value(DEFAULT_SETTING_FOUR.booleanValue()))
            .andExpect(jsonPath("$.settingFive").value(DEFAULT_SETTING_FIVE.booleanValue()))
            .andExpect(jsonPath("$.settingSix").value(DEFAULT_SETTING_SIX.booleanValue()))
            .andExpect(jsonPath("$.settingSeven").value(DEFAULT_SETTING_SEVEN.booleanValue()));
    }

    @Test
    @Transactional
    void getSettingsListsByIdFiltering() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        Long id = settingsList.getId();

        defaultSettingsListShouldBeFound("id.equals=" + id);
        defaultSettingsListShouldNotBeFound("id.notEquals=" + id);

        defaultSettingsListShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSettingsListShouldNotBeFound("id.greaterThan=" + id);

        defaultSettingsListShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSettingsListShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSettingsListsBySettingOneIsEqualToSomething() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get all the settingsListList where settingOne equals to DEFAULT_SETTING_ONE
        defaultSettingsListShouldBeFound("settingOne.equals=" + DEFAULT_SETTING_ONE);

        // Get all the settingsListList where settingOne equals to UPDATED_SETTING_ONE
        defaultSettingsListShouldNotBeFound("settingOne.equals=" + UPDATED_SETTING_ONE);
    }

    @Test
    @Transactional
    void getAllSettingsListsBySettingOneIsNotEqualToSomething() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get all the settingsListList where settingOne not equals to DEFAULT_SETTING_ONE
        defaultSettingsListShouldNotBeFound("settingOne.notEquals=" + DEFAULT_SETTING_ONE);

        // Get all the settingsListList where settingOne not equals to UPDATED_SETTING_ONE
        defaultSettingsListShouldBeFound("settingOne.notEquals=" + UPDATED_SETTING_ONE);
    }

    @Test
    @Transactional
    void getAllSettingsListsBySettingOneIsInShouldWork() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get all the settingsListList where settingOne in DEFAULT_SETTING_ONE or UPDATED_SETTING_ONE
        defaultSettingsListShouldBeFound("settingOne.in=" + DEFAULT_SETTING_ONE + "," + UPDATED_SETTING_ONE);

        // Get all the settingsListList where settingOne equals to UPDATED_SETTING_ONE
        defaultSettingsListShouldNotBeFound("settingOne.in=" + UPDATED_SETTING_ONE);
    }

    @Test
    @Transactional
    void getAllSettingsListsBySettingOneIsNullOrNotNull() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get all the settingsListList where settingOne is not null
        defaultSettingsListShouldBeFound("settingOne.specified=true");

        // Get all the settingsListList where settingOne is null
        defaultSettingsListShouldNotBeFound("settingOne.specified=false");
    }

    @Test
    @Transactional
    void getAllSettingsListsBySettingTwoIsEqualToSomething() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get all the settingsListList where settingTwo equals to DEFAULT_SETTING_TWO
        defaultSettingsListShouldBeFound("settingTwo.equals=" + DEFAULT_SETTING_TWO);

        // Get all the settingsListList where settingTwo equals to UPDATED_SETTING_TWO
        defaultSettingsListShouldNotBeFound("settingTwo.equals=" + UPDATED_SETTING_TWO);
    }

    @Test
    @Transactional
    void getAllSettingsListsBySettingTwoIsNotEqualToSomething() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get all the settingsListList where settingTwo not equals to DEFAULT_SETTING_TWO
        defaultSettingsListShouldNotBeFound("settingTwo.notEquals=" + DEFAULT_SETTING_TWO);

        // Get all the settingsListList where settingTwo not equals to UPDATED_SETTING_TWO
        defaultSettingsListShouldBeFound("settingTwo.notEquals=" + UPDATED_SETTING_TWO);
    }

    @Test
    @Transactional
    void getAllSettingsListsBySettingTwoIsInShouldWork() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get all the settingsListList where settingTwo in DEFAULT_SETTING_TWO or UPDATED_SETTING_TWO
        defaultSettingsListShouldBeFound("settingTwo.in=" + DEFAULT_SETTING_TWO + "," + UPDATED_SETTING_TWO);

        // Get all the settingsListList where settingTwo equals to UPDATED_SETTING_TWO
        defaultSettingsListShouldNotBeFound("settingTwo.in=" + UPDATED_SETTING_TWO);
    }

    @Test
    @Transactional
    void getAllSettingsListsBySettingTwoIsNullOrNotNull() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get all the settingsListList where settingTwo is not null
        defaultSettingsListShouldBeFound("settingTwo.specified=true");

        // Get all the settingsListList where settingTwo is null
        defaultSettingsListShouldNotBeFound("settingTwo.specified=false");
    }

    @Test
    @Transactional
    void getAllSettingsListsBySettingThreeIsEqualToSomething() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get all the settingsListList where settingThree equals to DEFAULT_SETTING_THREE
        defaultSettingsListShouldBeFound("settingThree.equals=" + DEFAULT_SETTING_THREE);

        // Get all the settingsListList where settingThree equals to UPDATED_SETTING_THREE
        defaultSettingsListShouldNotBeFound("settingThree.equals=" + UPDATED_SETTING_THREE);
    }

    @Test
    @Transactional
    void getAllSettingsListsBySettingThreeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get all the settingsListList where settingThree not equals to DEFAULT_SETTING_THREE
        defaultSettingsListShouldNotBeFound("settingThree.notEquals=" + DEFAULT_SETTING_THREE);

        // Get all the settingsListList where settingThree not equals to UPDATED_SETTING_THREE
        defaultSettingsListShouldBeFound("settingThree.notEquals=" + UPDATED_SETTING_THREE);
    }

    @Test
    @Transactional
    void getAllSettingsListsBySettingThreeIsInShouldWork() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get all the settingsListList where settingThree in DEFAULT_SETTING_THREE or UPDATED_SETTING_THREE
        defaultSettingsListShouldBeFound("settingThree.in=" + DEFAULT_SETTING_THREE + "," + UPDATED_SETTING_THREE);

        // Get all the settingsListList where settingThree equals to UPDATED_SETTING_THREE
        defaultSettingsListShouldNotBeFound("settingThree.in=" + UPDATED_SETTING_THREE);
    }

    @Test
    @Transactional
    void getAllSettingsListsBySettingThreeIsNullOrNotNull() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get all the settingsListList where settingThree is not null
        defaultSettingsListShouldBeFound("settingThree.specified=true");

        // Get all the settingsListList where settingThree is null
        defaultSettingsListShouldNotBeFound("settingThree.specified=false");
    }

    @Test
    @Transactional
    void getAllSettingsListsBySettingFourIsEqualToSomething() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get all the settingsListList where settingFour equals to DEFAULT_SETTING_FOUR
        defaultSettingsListShouldBeFound("settingFour.equals=" + DEFAULT_SETTING_FOUR);

        // Get all the settingsListList where settingFour equals to UPDATED_SETTING_FOUR
        defaultSettingsListShouldNotBeFound("settingFour.equals=" + UPDATED_SETTING_FOUR);
    }

    @Test
    @Transactional
    void getAllSettingsListsBySettingFourIsNotEqualToSomething() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get all the settingsListList where settingFour not equals to DEFAULT_SETTING_FOUR
        defaultSettingsListShouldNotBeFound("settingFour.notEquals=" + DEFAULT_SETTING_FOUR);

        // Get all the settingsListList where settingFour not equals to UPDATED_SETTING_FOUR
        defaultSettingsListShouldBeFound("settingFour.notEquals=" + UPDATED_SETTING_FOUR);
    }

    @Test
    @Transactional
    void getAllSettingsListsBySettingFourIsInShouldWork() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get all the settingsListList where settingFour in DEFAULT_SETTING_FOUR or UPDATED_SETTING_FOUR
        defaultSettingsListShouldBeFound("settingFour.in=" + DEFAULT_SETTING_FOUR + "," + UPDATED_SETTING_FOUR);

        // Get all the settingsListList where settingFour equals to UPDATED_SETTING_FOUR
        defaultSettingsListShouldNotBeFound("settingFour.in=" + UPDATED_SETTING_FOUR);
    }

    @Test
    @Transactional
    void getAllSettingsListsBySettingFourIsNullOrNotNull() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get all the settingsListList where settingFour is not null
        defaultSettingsListShouldBeFound("settingFour.specified=true");

        // Get all the settingsListList where settingFour is null
        defaultSettingsListShouldNotBeFound("settingFour.specified=false");
    }

    @Test
    @Transactional
    void getAllSettingsListsBySettingFiveIsEqualToSomething() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get all the settingsListList where settingFive equals to DEFAULT_SETTING_FIVE
        defaultSettingsListShouldBeFound("settingFive.equals=" + DEFAULT_SETTING_FIVE);

        // Get all the settingsListList where settingFive equals to UPDATED_SETTING_FIVE
        defaultSettingsListShouldNotBeFound("settingFive.equals=" + UPDATED_SETTING_FIVE);
    }

    @Test
    @Transactional
    void getAllSettingsListsBySettingFiveIsNotEqualToSomething() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get all the settingsListList where settingFive not equals to DEFAULT_SETTING_FIVE
        defaultSettingsListShouldNotBeFound("settingFive.notEquals=" + DEFAULT_SETTING_FIVE);

        // Get all the settingsListList where settingFive not equals to UPDATED_SETTING_FIVE
        defaultSettingsListShouldBeFound("settingFive.notEquals=" + UPDATED_SETTING_FIVE);
    }

    @Test
    @Transactional
    void getAllSettingsListsBySettingFiveIsInShouldWork() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get all the settingsListList where settingFive in DEFAULT_SETTING_FIVE or UPDATED_SETTING_FIVE
        defaultSettingsListShouldBeFound("settingFive.in=" + DEFAULT_SETTING_FIVE + "," + UPDATED_SETTING_FIVE);

        // Get all the settingsListList where settingFive equals to UPDATED_SETTING_FIVE
        defaultSettingsListShouldNotBeFound("settingFive.in=" + UPDATED_SETTING_FIVE);
    }

    @Test
    @Transactional
    void getAllSettingsListsBySettingFiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get all the settingsListList where settingFive is not null
        defaultSettingsListShouldBeFound("settingFive.specified=true");

        // Get all the settingsListList where settingFive is null
        defaultSettingsListShouldNotBeFound("settingFive.specified=false");
    }

    @Test
    @Transactional
    void getAllSettingsListsBySettingSixIsEqualToSomething() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get all the settingsListList where settingSix equals to DEFAULT_SETTING_SIX
        defaultSettingsListShouldBeFound("settingSix.equals=" + DEFAULT_SETTING_SIX);

        // Get all the settingsListList where settingSix equals to UPDATED_SETTING_SIX
        defaultSettingsListShouldNotBeFound("settingSix.equals=" + UPDATED_SETTING_SIX);
    }

    @Test
    @Transactional
    void getAllSettingsListsBySettingSixIsNotEqualToSomething() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get all the settingsListList where settingSix not equals to DEFAULT_SETTING_SIX
        defaultSettingsListShouldNotBeFound("settingSix.notEquals=" + DEFAULT_SETTING_SIX);

        // Get all the settingsListList where settingSix not equals to UPDATED_SETTING_SIX
        defaultSettingsListShouldBeFound("settingSix.notEquals=" + UPDATED_SETTING_SIX);
    }

    @Test
    @Transactional
    void getAllSettingsListsBySettingSixIsInShouldWork() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get all the settingsListList where settingSix in DEFAULT_SETTING_SIX or UPDATED_SETTING_SIX
        defaultSettingsListShouldBeFound("settingSix.in=" + DEFAULT_SETTING_SIX + "," + UPDATED_SETTING_SIX);

        // Get all the settingsListList where settingSix equals to UPDATED_SETTING_SIX
        defaultSettingsListShouldNotBeFound("settingSix.in=" + UPDATED_SETTING_SIX);
    }

    @Test
    @Transactional
    void getAllSettingsListsBySettingSixIsNullOrNotNull() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get all the settingsListList where settingSix is not null
        defaultSettingsListShouldBeFound("settingSix.specified=true");

        // Get all the settingsListList where settingSix is null
        defaultSettingsListShouldNotBeFound("settingSix.specified=false");
    }

    @Test
    @Transactional
    void getAllSettingsListsBySettingSevenIsEqualToSomething() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get all the settingsListList where settingSeven equals to DEFAULT_SETTING_SEVEN
        defaultSettingsListShouldBeFound("settingSeven.equals=" + DEFAULT_SETTING_SEVEN);

        // Get all the settingsListList where settingSeven equals to UPDATED_SETTING_SEVEN
        defaultSettingsListShouldNotBeFound("settingSeven.equals=" + UPDATED_SETTING_SEVEN);
    }

    @Test
    @Transactional
    void getAllSettingsListsBySettingSevenIsNotEqualToSomething() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get all the settingsListList where settingSeven not equals to DEFAULT_SETTING_SEVEN
        defaultSettingsListShouldNotBeFound("settingSeven.notEquals=" + DEFAULT_SETTING_SEVEN);

        // Get all the settingsListList where settingSeven not equals to UPDATED_SETTING_SEVEN
        defaultSettingsListShouldBeFound("settingSeven.notEquals=" + UPDATED_SETTING_SEVEN);
    }

    @Test
    @Transactional
    void getAllSettingsListsBySettingSevenIsInShouldWork() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get all the settingsListList where settingSeven in DEFAULT_SETTING_SEVEN or UPDATED_SETTING_SEVEN
        defaultSettingsListShouldBeFound("settingSeven.in=" + DEFAULT_SETTING_SEVEN + "," + UPDATED_SETTING_SEVEN);

        // Get all the settingsListList where settingSeven equals to UPDATED_SETTING_SEVEN
        defaultSettingsListShouldNotBeFound("settingSeven.in=" + UPDATED_SETTING_SEVEN);
    }

    @Test
    @Transactional
    void getAllSettingsListsBySettingSevenIsNullOrNotNull() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        // Get all the settingsListList where settingSeven is not null
        defaultSettingsListShouldBeFound("settingSeven.specified=true");

        // Get all the settingsListList where settingSeven is null
        defaultSettingsListShouldNotBeFound("settingSeven.specified=false");
    }

    @Test
    @Transactional
    void getAllSettingsListsBySpendingListIsEqualToSomething() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);
        SpendingList spendingList;
        if (TestUtil.findAll(em, SpendingList.class).isEmpty()) {
            spendingList = SpendingListResourceIT.createEntity(em);
            em.persist(spendingList);
            em.flush();
        } else {
            spendingList = TestUtil.findAll(em, SpendingList.class).get(0);
        }
        em.persist(spendingList);
        em.flush();
        settingsListRepository.saveAndFlush(settingsList);
        Long spendingListId = spendingList.getId();

        // Get all the settingsListList where spendingList equals to spendingListId
        defaultSettingsListShouldBeFound("spendingListId.equals=" + spendingListId);

        // Get all the settingsListList where spendingList equals to (spendingListId + 1)
        defaultSettingsListShouldNotBeFound("spendingListId.equals=" + (spendingListId + 1));
    }

    @Test
    @Transactional
    void getAllSettingsListsByUserPendingIsEqualToSomething() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);
        UserPending userPending;
        if (TestUtil.findAll(em, UserPending.class).isEmpty()) {
            userPending = UserPendingResourceIT.createEntity(em);
            em.persist(userPending);
            em.flush();
        } else {
            userPending = TestUtil.findAll(em, UserPending.class).get(0);
        }
        em.persist(userPending);
        em.flush();
        settingsListRepository.saveAndFlush(settingsList);
        Long userPendingId = userPending.getId();

        // Get all the settingsListList where userPending equals to userPendingId
        defaultSettingsListShouldBeFound("userPendingId.equals=" + userPendingId);

        // Get all the settingsListList where userPending equals to (userPendingId + 1)
        defaultSettingsListShouldNotBeFound("userPendingId.equals=" + (userPendingId + 1));
    }

    @Test
    @Transactional
    void getAllSettingsListsByGroupIsEqualToSomething() throws Exception {
        // Get already existing entity
        Group group = settingsList.getGroup();
        settingsListRepository.saveAndFlush(settingsList);
        Long groupId = group.getId();

        // Get all the settingsListList where group equals to groupId
        defaultSettingsListShouldBeFound("groupId.equals=" + groupId);

        // Get all the settingsListList where group equals to (groupId + 1)
        defaultSettingsListShouldNotBeFound("groupId.equals=" + (groupId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSettingsListShouldBeFound(String filter) throws Exception {
        restSettingsListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(settingsList.getId().intValue())))
            .andExpect(jsonPath("$.[*].settingOne").value(hasItem(DEFAULT_SETTING_ONE.booleanValue())))
            .andExpect(jsonPath("$.[*].settingTwo").value(hasItem(DEFAULT_SETTING_TWO.booleanValue())))
            .andExpect(jsonPath("$.[*].settingThree").value(hasItem(DEFAULT_SETTING_THREE.booleanValue())))
            .andExpect(jsonPath("$.[*].settingFour").value(hasItem(DEFAULT_SETTING_FOUR.booleanValue())))
            .andExpect(jsonPath("$.[*].settingFive").value(hasItem(DEFAULT_SETTING_FIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].settingSix").value(hasItem(DEFAULT_SETTING_SIX.booleanValue())))
            .andExpect(jsonPath("$.[*].settingSeven").value(hasItem(DEFAULT_SETTING_SEVEN.booleanValue())));

        // Check, that the count call also returns 1
        restSettingsListMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSettingsListShouldNotBeFound(String filter) throws Exception {
        restSettingsListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSettingsListMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSettingsList() throws Exception {
        // Get the settingsList
        restSettingsListMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSettingsList() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        int databaseSizeBeforeUpdate = settingsListRepository.findAll().size();

        // Update the settingsList
        SettingsList updatedSettingsList = settingsListRepository.findById(settingsList.getId()).get();
        // Disconnect from session so that the updates on updatedSettingsList are not directly saved in db
        em.detach(updatedSettingsList);
        updatedSettingsList
            .settingOne(UPDATED_SETTING_ONE)
            .settingTwo(UPDATED_SETTING_TWO)
            .settingThree(UPDATED_SETTING_THREE)
            .settingFour(UPDATED_SETTING_FOUR)
            .settingFive(UPDATED_SETTING_FIVE)
            .settingSix(UPDATED_SETTING_SIX)
            .settingSeven(UPDATED_SETTING_SEVEN);

        restSettingsListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSettingsList.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSettingsList))
            )
            .andExpect(status().isOk());

        // Validate the SettingsList in the database
        List<SettingsList> settingsListList = settingsListRepository.findAll();
        assertThat(settingsListList).hasSize(databaseSizeBeforeUpdate);
        SettingsList testSettingsList = settingsListList.get(settingsListList.size() - 1);
        assertThat(testSettingsList.getSettingOne()).isEqualTo(UPDATED_SETTING_ONE);
        assertThat(testSettingsList.getSettingTwo()).isEqualTo(UPDATED_SETTING_TWO);
        assertThat(testSettingsList.getSettingThree()).isEqualTo(UPDATED_SETTING_THREE);
        assertThat(testSettingsList.getSettingFour()).isEqualTo(UPDATED_SETTING_FOUR);
        assertThat(testSettingsList.getSettingFive()).isEqualTo(UPDATED_SETTING_FIVE);
        assertThat(testSettingsList.getSettingSix()).isEqualTo(UPDATED_SETTING_SIX);
        assertThat(testSettingsList.getSettingSeven()).isEqualTo(UPDATED_SETTING_SEVEN);
    }

    @Test
    @Transactional
    void putNonExistingSettingsList() throws Exception {
        int databaseSizeBeforeUpdate = settingsListRepository.findAll().size();
        settingsList.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSettingsListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, settingsList.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(settingsList))
            )
            .andExpect(status().isBadRequest());

        // Validate the SettingsList in the database
        List<SettingsList> settingsListList = settingsListRepository.findAll();
        assertThat(settingsListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSettingsList() throws Exception {
        int databaseSizeBeforeUpdate = settingsListRepository.findAll().size();
        settingsList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSettingsListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(settingsList))
            )
            .andExpect(status().isBadRequest());

        // Validate the SettingsList in the database
        List<SettingsList> settingsListList = settingsListRepository.findAll();
        assertThat(settingsListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSettingsList() throws Exception {
        int databaseSizeBeforeUpdate = settingsListRepository.findAll().size();
        settingsList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSettingsListMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(settingsList)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SettingsList in the database
        List<SettingsList> settingsListList = settingsListRepository.findAll();
        assertThat(settingsListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSettingsListWithPatch() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        int databaseSizeBeforeUpdate = settingsListRepository.findAll().size();

        // Update the settingsList using partial update
        SettingsList partialUpdatedSettingsList = new SettingsList();
        partialUpdatedSettingsList.setId(settingsList.getId());

        partialUpdatedSettingsList
            .settingOne(UPDATED_SETTING_ONE)
            .settingTwo(UPDATED_SETTING_TWO)
            .settingFive(UPDATED_SETTING_FIVE)
            .settingSeven(UPDATED_SETTING_SEVEN);

        restSettingsListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSettingsList.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSettingsList))
            )
            .andExpect(status().isOk());

        // Validate the SettingsList in the database
        List<SettingsList> settingsListList = settingsListRepository.findAll();
        assertThat(settingsListList).hasSize(databaseSizeBeforeUpdate);
        SettingsList testSettingsList = settingsListList.get(settingsListList.size() - 1);
        assertThat(testSettingsList.getSettingOne()).isEqualTo(UPDATED_SETTING_ONE);
        assertThat(testSettingsList.getSettingTwo()).isEqualTo(UPDATED_SETTING_TWO);
        assertThat(testSettingsList.getSettingThree()).isEqualTo(DEFAULT_SETTING_THREE);
        assertThat(testSettingsList.getSettingFour()).isEqualTo(DEFAULT_SETTING_FOUR);
        assertThat(testSettingsList.getSettingFive()).isEqualTo(UPDATED_SETTING_FIVE);
        assertThat(testSettingsList.getSettingSix()).isEqualTo(DEFAULT_SETTING_SIX);
        assertThat(testSettingsList.getSettingSeven()).isEqualTo(UPDATED_SETTING_SEVEN);
    }

    @Test
    @Transactional
    void fullUpdateSettingsListWithPatch() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        int databaseSizeBeforeUpdate = settingsListRepository.findAll().size();

        // Update the settingsList using partial update
        SettingsList partialUpdatedSettingsList = new SettingsList();
        partialUpdatedSettingsList.setId(settingsList.getId());

        partialUpdatedSettingsList
            .settingOne(UPDATED_SETTING_ONE)
            .settingTwo(UPDATED_SETTING_TWO)
            .settingThree(UPDATED_SETTING_THREE)
            .settingFour(UPDATED_SETTING_FOUR)
            .settingFive(UPDATED_SETTING_FIVE)
            .settingSix(UPDATED_SETTING_SIX)
            .settingSeven(UPDATED_SETTING_SEVEN);

        restSettingsListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSettingsList.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSettingsList))
            )
            .andExpect(status().isOk());

        // Validate the SettingsList in the database
        List<SettingsList> settingsListList = settingsListRepository.findAll();
        assertThat(settingsListList).hasSize(databaseSizeBeforeUpdate);
        SettingsList testSettingsList = settingsListList.get(settingsListList.size() - 1);
        assertThat(testSettingsList.getSettingOne()).isEqualTo(UPDATED_SETTING_ONE);
        assertThat(testSettingsList.getSettingTwo()).isEqualTo(UPDATED_SETTING_TWO);
        assertThat(testSettingsList.getSettingThree()).isEqualTo(UPDATED_SETTING_THREE);
        assertThat(testSettingsList.getSettingFour()).isEqualTo(UPDATED_SETTING_FOUR);
        assertThat(testSettingsList.getSettingFive()).isEqualTo(UPDATED_SETTING_FIVE);
        assertThat(testSettingsList.getSettingSix()).isEqualTo(UPDATED_SETTING_SIX);
        assertThat(testSettingsList.getSettingSeven()).isEqualTo(UPDATED_SETTING_SEVEN);
    }

    @Test
    @Transactional
    void patchNonExistingSettingsList() throws Exception {
        int databaseSizeBeforeUpdate = settingsListRepository.findAll().size();
        settingsList.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSettingsListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, settingsList.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(settingsList))
            )
            .andExpect(status().isBadRequest());

        // Validate the SettingsList in the database
        List<SettingsList> settingsListList = settingsListRepository.findAll();
        assertThat(settingsListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSettingsList() throws Exception {
        int databaseSizeBeforeUpdate = settingsListRepository.findAll().size();
        settingsList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSettingsListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(settingsList))
            )
            .andExpect(status().isBadRequest());

        // Validate the SettingsList in the database
        List<SettingsList> settingsListList = settingsListRepository.findAll();
        assertThat(settingsListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSettingsList() throws Exception {
        int databaseSizeBeforeUpdate = settingsListRepository.findAll().size();
        settingsList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSettingsListMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(settingsList))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SettingsList in the database
        List<SettingsList> settingsListList = settingsListRepository.findAll();
        assertThat(settingsListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSettingsList() throws Exception {
        // Initialize the database
        settingsListRepository.saveAndFlush(settingsList);

        int databaseSizeBeforeDelete = settingsListRepository.findAll().size();

        // Delete the settingsList
        restSettingsListMockMvc
            .perform(delete(ENTITY_API_URL_ID, settingsList.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SettingsList> settingsListList = settingsListRepository.findAll();
        assertThat(settingsListList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
