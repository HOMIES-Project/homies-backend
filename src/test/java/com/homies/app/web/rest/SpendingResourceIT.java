package com.homies.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.homies.app.IntegrationTest;
import com.homies.app.domain.Spending;
import com.homies.app.repository.SpendingRepository;
import com.homies.app.service.criteria.SpendingCriteria;
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
 * Integration tests for the {@link SpendingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SpendingResourceIT {

    private static final Integer DEFAULT_PAYER = 1;
    private static final Integer UPDATED_PAYER = 2;
    private static final Integer SMALLER_PAYER = 1 - 1;

    private static final String DEFAULT_NAME_COST = "AAAAAAAAAA";
    private static final String UPDATED_NAME_COST = "BBBBBBBBBB";

    private static final Float DEFAULT_COST = 0F;
    private static final Float UPDATED_COST = 1F;
    private static final Float SMALLER_COST = 0F - 1F;

    private static final byte[] DEFAULT_PHOTO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PHOTO = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PHOTO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PHOTO_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBB";

    private static final Float DEFAULT_PAID = 0F;
    private static final Float UPDATED_PAID = 1F;
    private static final Float SMALLER_PAID = 0F - 1F;

    private static final Float DEFAULT_PENDING = 0F;
    private static final Float UPDATED_PENDING = 1F;
    private static final Float SMALLER_PENDING = 0F - 1F;

    private static final Boolean DEFAULT_FINISHED = false;
    private static final Boolean UPDATED_FINISHED = true;

    private static final String ENTITY_API_URL = "/api/spendings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SpendingRepository spendingRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSpendingMockMvc;

    private Spending spending;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Spending createEntity(EntityManager em) {
        Spending spending = new Spending()
            .payer(DEFAULT_PAYER)
            .nameCost(DEFAULT_NAME_COST)
            .cost(DEFAULT_COST)
            .photo(DEFAULT_PHOTO)
            .photoContentType(DEFAULT_PHOTO_CONTENT_TYPE)
            .descripcion(DEFAULT_DESCRIPCION)
            .paid(DEFAULT_PAID)
            .pending(DEFAULT_PENDING)
            .finished(DEFAULT_FINISHED);
        return spending;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Spending createUpdatedEntity(EntityManager em) {
        Spending spending = new Spending()
            .payer(UPDATED_PAYER)
            .nameCost(UPDATED_NAME_COST)
            .cost(UPDATED_COST)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .descripcion(UPDATED_DESCRIPCION)
            .paid(UPDATED_PAID)
            .pending(UPDATED_PENDING)
            .finished(UPDATED_FINISHED);
        return spending;
    }

    @BeforeEach
    public void initTest() {
        spending = createEntity(em);
    }

    @Test
    @Transactional
    void createSpending() throws Exception {
        int databaseSizeBeforeCreate = spendingRepository.findAll().size();
        // Create the Spending
        restSpendingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(spending)))
            .andExpect(status().isCreated());

        // Validate the Spending in the database
        List<Spending> spendingList = spendingRepository.findAll();
        assertThat(spendingList).hasSize(databaseSizeBeforeCreate + 1);
        Spending testSpending = spendingList.get(spendingList.size() - 1);
        assertThat(testSpending.getPayer()).isEqualTo(DEFAULT_PAYER);
        assertThat(testSpending.getNameCost()).isEqualTo(DEFAULT_NAME_COST);
        assertThat(testSpending.getCost()).isEqualTo(DEFAULT_COST);
        assertThat(testSpending.getPhoto()).isEqualTo(DEFAULT_PHOTO);
        assertThat(testSpending.getPhotoContentType()).isEqualTo(DEFAULT_PHOTO_CONTENT_TYPE);
        assertThat(testSpending.getDescripcion()).isEqualTo(DEFAULT_DESCRIPCION);
        assertThat(testSpending.getPaid()).isEqualTo(DEFAULT_PAID);
        assertThat(testSpending.getPending()).isEqualTo(DEFAULT_PENDING);
        assertThat(testSpending.getFinished()).isEqualTo(DEFAULT_FINISHED);
    }

    @Test
    @Transactional
    void createSpendingWithExistingId() throws Exception {
        // Create the Spending with an existing ID
        spending.setId(1L);

        int databaseSizeBeforeCreate = spendingRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSpendingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(spending)))
            .andExpect(status().isBadRequest());

        // Validate the Spending in the database
        List<Spending> spendingList = spendingRepository.findAll();
        assertThat(spendingList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPayerIsRequired() throws Exception {
        int databaseSizeBeforeTest = spendingRepository.findAll().size();
        // set the field null
        spending.setPayer(null);

        // Create the Spending, which fails.

        restSpendingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(spending)))
            .andExpect(status().isBadRequest());

        List<Spending> spendingList = spendingRepository.findAll();
        assertThat(spendingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameCostIsRequired() throws Exception {
        int databaseSizeBeforeTest = spendingRepository.findAll().size();
        // set the field null
        spending.setNameCost(null);

        // Create the Spending, which fails.

        restSpendingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(spending)))
            .andExpect(status().isBadRequest());

        List<Spending> spendingList = spendingRepository.findAll();
        assertThat(spendingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCostIsRequired() throws Exception {
        int databaseSizeBeforeTest = spendingRepository.findAll().size();
        // set the field null
        spending.setCost(null);

        // Create the Spending, which fails.

        restSpendingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(spending)))
            .andExpect(status().isBadRequest());

        List<Spending> spendingList = spendingRepository.findAll();
        assertThat(spendingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSpendings() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList
        restSpendingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(spending.getId().intValue())))
            .andExpect(jsonPath("$.[*].payer").value(hasItem(DEFAULT_PAYER)))
            .andExpect(jsonPath("$.[*].nameCost").value(hasItem(DEFAULT_NAME_COST)))
            .andExpect(jsonPath("$.[*].cost").value(hasItem(DEFAULT_COST.doubleValue())))
            .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64Utils.encodeToString(DEFAULT_PHOTO))))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].paid").value(hasItem(DEFAULT_PAID.doubleValue())))
            .andExpect(jsonPath("$.[*].pending").value(hasItem(DEFAULT_PENDING.doubleValue())))
            .andExpect(jsonPath("$.[*].finished").value(hasItem(DEFAULT_FINISHED.booleanValue())));
    }

    @Test
    @Transactional
    void getSpending() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get the spending
        restSpendingMockMvc
            .perform(get(ENTITY_API_URL_ID, spending.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(spending.getId().intValue()))
            .andExpect(jsonPath("$.payer").value(DEFAULT_PAYER))
            .andExpect(jsonPath("$.nameCost").value(DEFAULT_NAME_COST))
            .andExpect(jsonPath("$.cost").value(DEFAULT_COST.doubleValue()))
            .andExpect(jsonPath("$.photoContentType").value(DEFAULT_PHOTO_CONTENT_TYPE))
            .andExpect(jsonPath("$.photo").value(Base64Utils.encodeToString(DEFAULT_PHOTO)))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION))
            .andExpect(jsonPath("$.paid").value(DEFAULT_PAID.doubleValue()))
            .andExpect(jsonPath("$.pending").value(DEFAULT_PENDING.doubleValue()))
            .andExpect(jsonPath("$.finished").value(DEFAULT_FINISHED.booleanValue()));
    }

    @Test
    @Transactional
    void getSpendingsByIdFiltering() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        Long id = spending.getId();

        defaultSpendingShouldBeFound("id.equals=" + id);
        defaultSpendingShouldNotBeFound("id.notEquals=" + id);

        defaultSpendingShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSpendingShouldNotBeFound("id.greaterThan=" + id);

        defaultSpendingShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSpendingShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSpendingsByPayerIsEqualToSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where payer equals to DEFAULT_PAYER
        defaultSpendingShouldBeFound("payer.equals=" + DEFAULT_PAYER);

        // Get all the spendingList where payer equals to UPDATED_PAYER
        defaultSpendingShouldNotBeFound("payer.equals=" + UPDATED_PAYER);
    }

    @Test
    @Transactional
    void getAllSpendingsByPayerIsNotEqualToSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where payer not equals to DEFAULT_PAYER
        defaultSpendingShouldNotBeFound("payer.notEquals=" + DEFAULT_PAYER);

        // Get all the spendingList where payer not equals to UPDATED_PAYER
        defaultSpendingShouldBeFound("payer.notEquals=" + UPDATED_PAYER);
    }

    @Test
    @Transactional
    void getAllSpendingsByPayerIsInShouldWork() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where payer in DEFAULT_PAYER or UPDATED_PAYER
        defaultSpendingShouldBeFound("payer.in=" + DEFAULT_PAYER + "," + UPDATED_PAYER);

        // Get all the spendingList where payer equals to UPDATED_PAYER
        defaultSpendingShouldNotBeFound("payer.in=" + UPDATED_PAYER);
    }

    @Test
    @Transactional
    void getAllSpendingsByPayerIsNullOrNotNull() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where payer is not null
        defaultSpendingShouldBeFound("payer.specified=true");

        // Get all the spendingList where payer is null
        defaultSpendingShouldNotBeFound("payer.specified=false");
    }

    @Test
    @Transactional
    void getAllSpendingsByPayerIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where payer is greater than or equal to DEFAULT_PAYER
        defaultSpendingShouldBeFound("payer.greaterThanOrEqual=" + DEFAULT_PAYER);

        // Get all the spendingList where payer is greater than or equal to UPDATED_PAYER
        defaultSpendingShouldNotBeFound("payer.greaterThanOrEqual=" + UPDATED_PAYER);
    }

    @Test
    @Transactional
    void getAllSpendingsByPayerIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where payer is less than or equal to DEFAULT_PAYER
        defaultSpendingShouldBeFound("payer.lessThanOrEqual=" + DEFAULT_PAYER);

        // Get all the spendingList where payer is less than or equal to SMALLER_PAYER
        defaultSpendingShouldNotBeFound("payer.lessThanOrEqual=" + SMALLER_PAYER);
    }

    @Test
    @Transactional
    void getAllSpendingsByPayerIsLessThanSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where payer is less than DEFAULT_PAYER
        defaultSpendingShouldNotBeFound("payer.lessThan=" + DEFAULT_PAYER);

        // Get all the spendingList where payer is less than UPDATED_PAYER
        defaultSpendingShouldBeFound("payer.lessThan=" + UPDATED_PAYER);
    }

    @Test
    @Transactional
    void getAllSpendingsByPayerIsGreaterThanSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where payer is greater than DEFAULT_PAYER
        defaultSpendingShouldNotBeFound("payer.greaterThan=" + DEFAULT_PAYER);

        // Get all the spendingList where payer is greater than SMALLER_PAYER
        defaultSpendingShouldBeFound("payer.greaterThan=" + SMALLER_PAYER);
    }

    @Test
    @Transactional
    void getAllSpendingsByNameCostIsEqualToSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where nameCost equals to DEFAULT_NAME_COST
        defaultSpendingShouldBeFound("nameCost.equals=" + DEFAULT_NAME_COST);

        // Get all the spendingList where nameCost equals to UPDATED_NAME_COST
        defaultSpendingShouldNotBeFound("nameCost.equals=" + UPDATED_NAME_COST);
    }

    @Test
    @Transactional
    void getAllSpendingsByNameCostIsNotEqualToSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where nameCost not equals to DEFAULT_NAME_COST
        defaultSpendingShouldNotBeFound("nameCost.notEquals=" + DEFAULT_NAME_COST);

        // Get all the spendingList where nameCost not equals to UPDATED_NAME_COST
        defaultSpendingShouldBeFound("nameCost.notEquals=" + UPDATED_NAME_COST);
    }

    @Test
    @Transactional
    void getAllSpendingsByNameCostIsInShouldWork() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where nameCost in DEFAULT_NAME_COST or UPDATED_NAME_COST
        defaultSpendingShouldBeFound("nameCost.in=" + DEFAULT_NAME_COST + "," + UPDATED_NAME_COST);

        // Get all the spendingList where nameCost equals to UPDATED_NAME_COST
        defaultSpendingShouldNotBeFound("nameCost.in=" + UPDATED_NAME_COST);
    }

    @Test
    @Transactional
    void getAllSpendingsByNameCostIsNullOrNotNull() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where nameCost is not null
        defaultSpendingShouldBeFound("nameCost.specified=true");

        // Get all the spendingList where nameCost is null
        defaultSpendingShouldNotBeFound("nameCost.specified=false");
    }

    @Test
    @Transactional
    void getAllSpendingsByNameCostContainsSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where nameCost contains DEFAULT_NAME_COST
        defaultSpendingShouldBeFound("nameCost.contains=" + DEFAULT_NAME_COST);

        // Get all the spendingList where nameCost contains UPDATED_NAME_COST
        defaultSpendingShouldNotBeFound("nameCost.contains=" + UPDATED_NAME_COST);
    }

    @Test
    @Transactional
    void getAllSpendingsByNameCostNotContainsSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where nameCost does not contain DEFAULT_NAME_COST
        defaultSpendingShouldNotBeFound("nameCost.doesNotContain=" + DEFAULT_NAME_COST);

        // Get all the spendingList where nameCost does not contain UPDATED_NAME_COST
        defaultSpendingShouldBeFound("nameCost.doesNotContain=" + UPDATED_NAME_COST);
    }

    @Test
    @Transactional
    void getAllSpendingsByCostIsEqualToSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where cost equals to DEFAULT_COST
        defaultSpendingShouldBeFound("cost.equals=" + DEFAULT_COST);

        // Get all the spendingList where cost equals to UPDATED_COST
        defaultSpendingShouldNotBeFound("cost.equals=" + UPDATED_COST);
    }

    @Test
    @Transactional
    void getAllSpendingsByCostIsNotEqualToSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where cost not equals to DEFAULT_COST
        defaultSpendingShouldNotBeFound("cost.notEquals=" + DEFAULT_COST);

        // Get all the spendingList where cost not equals to UPDATED_COST
        defaultSpendingShouldBeFound("cost.notEquals=" + UPDATED_COST);
    }

    @Test
    @Transactional
    void getAllSpendingsByCostIsInShouldWork() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where cost in DEFAULT_COST or UPDATED_COST
        defaultSpendingShouldBeFound("cost.in=" + DEFAULT_COST + "," + UPDATED_COST);

        // Get all the spendingList where cost equals to UPDATED_COST
        defaultSpendingShouldNotBeFound("cost.in=" + UPDATED_COST);
    }

    @Test
    @Transactional
    void getAllSpendingsByCostIsNullOrNotNull() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where cost is not null
        defaultSpendingShouldBeFound("cost.specified=true");

        // Get all the spendingList where cost is null
        defaultSpendingShouldNotBeFound("cost.specified=false");
    }

    @Test
    @Transactional
    void getAllSpendingsByCostIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where cost is greater than or equal to DEFAULT_COST
        defaultSpendingShouldBeFound("cost.greaterThanOrEqual=" + DEFAULT_COST);

        // Get all the spendingList where cost is greater than or equal to UPDATED_COST
        defaultSpendingShouldNotBeFound("cost.greaterThanOrEqual=" + UPDATED_COST);
    }

    @Test
    @Transactional
    void getAllSpendingsByCostIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where cost is less than or equal to DEFAULT_COST
        defaultSpendingShouldBeFound("cost.lessThanOrEqual=" + DEFAULT_COST);

        // Get all the spendingList where cost is less than or equal to SMALLER_COST
        defaultSpendingShouldNotBeFound("cost.lessThanOrEqual=" + SMALLER_COST);
    }

    @Test
    @Transactional
    void getAllSpendingsByCostIsLessThanSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where cost is less than DEFAULT_COST
        defaultSpendingShouldNotBeFound("cost.lessThan=" + DEFAULT_COST);

        // Get all the spendingList where cost is less than UPDATED_COST
        defaultSpendingShouldBeFound("cost.lessThan=" + UPDATED_COST);
    }

    @Test
    @Transactional
    void getAllSpendingsByCostIsGreaterThanSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where cost is greater than DEFAULT_COST
        defaultSpendingShouldNotBeFound("cost.greaterThan=" + DEFAULT_COST);

        // Get all the spendingList where cost is greater than SMALLER_COST
        defaultSpendingShouldBeFound("cost.greaterThan=" + SMALLER_COST);
    }

    @Test
    @Transactional
    void getAllSpendingsByDescripcionIsEqualToSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where descripcion equals to DEFAULT_DESCRIPCION
        defaultSpendingShouldBeFound("descripcion.equals=" + DEFAULT_DESCRIPCION);

        // Get all the spendingList where descripcion equals to UPDATED_DESCRIPCION
        defaultSpendingShouldNotBeFound("descripcion.equals=" + UPDATED_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllSpendingsByDescripcionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where descripcion not equals to DEFAULT_DESCRIPCION
        defaultSpendingShouldNotBeFound("descripcion.notEquals=" + DEFAULT_DESCRIPCION);

        // Get all the spendingList where descripcion not equals to UPDATED_DESCRIPCION
        defaultSpendingShouldBeFound("descripcion.notEquals=" + UPDATED_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllSpendingsByDescripcionIsInShouldWork() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where descripcion in DEFAULT_DESCRIPCION or UPDATED_DESCRIPCION
        defaultSpendingShouldBeFound("descripcion.in=" + DEFAULT_DESCRIPCION + "," + UPDATED_DESCRIPCION);

        // Get all the spendingList where descripcion equals to UPDATED_DESCRIPCION
        defaultSpendingShouldNotBeFound("descripcion.in=" + UPDATED_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllSpendingsByDescripcionIsNullOrNotNull() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where descripcion is not null
        defaultSpendingShouldBeFound("descripcion.specified=true");

        // Get all the spendingList where descripcion is null
        defaultSpendingShouldNotBeFound("descripcion.specified=false");
    }

    @Test
    @Transactional
    void getAllSpendingsByDescripcionContainsSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where descripcion contains DEFAULT_DESCRIPCION
        defaultSpendingShouldBeFound("descripcion.contains=" + DEFAULT_DESCRIPCION);

        // Get all the spendingList where descripcion contains UPDATED_DESCRIPCION
        defaultSpendingShouldNotBeFound("descripcion.contains=" + UPDATED_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllSpendingsByDescripcionNotContainsSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where descripcion does not contain DEFAULT_DESCRIPCION
        defaultSpendingShouldNotBeFound("descripcion.doesNotContain=" + DEFAULT_DESCRIPCION);

        // Get all the spendingList where descripcion does not contain UPDATED_DESCRIPCION
        defaultSpendingShouldBeFound("descripcion.doesNotContain=" + UPDATED_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllSpendingsByPaidIsEqualToSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where paid equals to DEFAULT_PAID
        defaultSpendingShouldBeFound("paid.equals=" + DEFAULT_PAID);

        // Get all the spendingList where paid equals to UPDATED_PAID
        defaultSpendingShouldNotBeFound("paid.equals=" + UPDATED_PAID);
    }

    @Test
    @Transactional
    void getAllSpendingsByPaidIsNotEqualToSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where paid not equals to DEFAULT_PAID
        defaultSpendingShouldNotBeFound("paid.notEquals=" + DEFAULT_PAID);

        // Get all the spendingList where paid not equals to UPDATED_PAID
        defaultSpendingShouldBeFound("paid.notEquals=" + UPDATED_PAID);
    }

    @Test
    @Transactional
    void getAllSpendingsByPaidIsInShouldWork() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where paid in DEFAULT_PAID or UPDATED_PAID
        defaultSpendingShouldBeFound("paid.in=" + DEFAULT_PAID + "," + UPDATED_PAID);

        // Get all the spendingList where paid equals to UPDATED_PAID
        defaultSpendingShouldNotBeFound("paid.in=" + UPDATED_PAID);
    }

    @Test
    @Transactional
    void getAllSpendingsByPaidIsNullOrNotNull() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where paid is not null
        defaultSpendingShouldBeFound("paid.specified=true");

        // Get all the spendingList where paid is null
        defaultSpendingShouldNotBeFound("paid.specified=false");
    }

    @Test
    @Transactional
    void getAllSpendingsByPaidIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where paid is greater than or equal to DEFAULT_PAID
        defaultSpendingShouldBeFound("paid.greaterThanOrEqual=" + DEFAULT_PAID);

        // Get all the spendingList where paid is greater than or equal to UPDATED_PAID
        defaultSpendingShouldNotBeFound("paid.greaterThanOrEqual=" + UPDATED_PAID);
    }

    @Test
    @Transactional
    void getAllSpendingsByPaidIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where paid is less than or equal to DEFAULT_PAID
        defaultSpendingShouldBeFound("paid.lessThanOrEqual=" + DEFAULT_PAID);

        // Get all the spendingList where paid is less than or equal to SMALLER_PAID
        defaultSpendingShouldNotBeFound("paid.lessThanOrEqual=" + SMALLER_PAID);
    }

    @Test
    @Transactional
    void getAllSpendingsByPaidIsLessThanSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where paid is less than DEFAULT_PAID
        defaultSpendingShouldNotBeFound("paid.lessThan=" + DEFAULT_PAID);

        // Get all the spendingList where paid is less than UPDATED_PAID
        defaultSpendingShouldBeFound("paid.lessThan=" + UPDATED_PAID);
    }

    @Test
    @Transactional
    void getAllSpendingsByPaidIsGreaterThanSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where paid is greater than DEFAULT_PAID
        defaultSpendingShouldNotBeFound("paid.greaterThan=" + DEFAULT_PAID);

        // Get all the spendingList where paid is greater than SMALLER_PAID
        defaultSpendingShouldBeFound("paid.greaterThan=" + SMALLER_PAID);
    }

    @Test
    @Transactional
    void getAllSpendingsByPendingIsEqualToSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where pending equals to DEFAULT_PENDING
        defaultSpendingShouldBeFound("pending.equals=" + DEFAULT_PENDING);

        // Get all the spendingList where pending equals to UPDATED_PENDING
        defaultSpendingShouldNotBeFound("pending.equals=" + UPDATED_PENDING);
    }

    @Test
    @Transactional
    void getAllSpendingsByPendingIsNotEqualToSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where pending not equals to DEFAULT_PENDING
        defaultSpendingShouldNotBeFound("pending.notEquals=" + DEFAULT_PENDING);

        // Get all the spendingList where pending not equals to UPDATED_PENDING
        defaultSpendingShouldBeFound("pending.notEquals=" + UPDATED_PENDING);
    }

    @Test
    @Transactional
    void getAllSpendingsByPendingIsInShouldWork() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where pending in DEFAULT_PENDING or UPDATED_PENDING
        defaultSpendingShouldBeFound("pending.in=" + DEFAULT_PENDING + "," + UPDATED_PENDING);

        // Get all the spendingList where pending equals to UPDATED_PENDING
        defaultSpendingShouldNotBeFound("pending.in=" + UPDATED_PENDING);
    }

    @Test
    @Transactional
    void getAllSpendingsByPendingIsNullOrNotNull() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where pending is not null
        defaultSpendingShouldBeFound("pending.specified=true");

        // Get all the spendingList where pending is null
        defaultSpendingShouldNotBeFound("pending.specified=false");
    }

    @Test
    @Transactional
    void getAllSpendingsByPendingIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where pending is greater than or equal to DEFAULT_PENDING
        defaultSpendingShouldBeFound("pending.greaterThanOrEqual=" + DEFAULT_PENDING);

        // Get all the spendingList where pending is greater than or equal to UPDATED_PENDING
        defaultSpendingShouldNotBeFound("pending.greaterThanOrEqual=" + UPDATED_PENDING);
    }

    @Test
    @Transactional
    void getAllSpendingsByPendingIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where pending is less than or equal to DEFAULT_PENDING
        defaultSpendingShouldBeFound("pending.lessThanOrEqual=" + DEFAULT_PENDING);

        // Get all the spendingList where pending is less than or equal to SMALLER_PENDING
        defaultSpendingShouldNotBeFound("pending.lessThanOrEqual=" + SMALLER_PENDING);
    }

    @Test
    @Transactional
    void getAllSpendingsByPendingIsLessThanSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where pending is less than DEFAULT_PENDING
        defaultSpendingShouldNotBeFound("pending.lessThan=" + DEFAULT_PENDING);

        // Get all the spendingList where pending is less than UPDATED_PENDING
        defaultSpendingShouldBeFound("pending.lessThan=" + UPDATED_PENDING);
    }

    @Test
    @Transactional
    void getAllSpendingsByPendingIsGreaterThanSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where pending is greater than DEFAULT_PENDING
        defaultSpendingShouldNotBeFound("pending.greaterThan=" + DEFAULT_PENDING);

        // Get all the spendingList where pending is greater than SMALLER_PENDING
        defaultSpendingShouldBeFound("pending.greaterThan=" + SMALLER_PENDING);
    }

    @Test
    @Transactional
    void getAllSpendingsByFinishedIsEqualToSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where finished equals to DEFAULT_FINISHED
        defaultSpendingShouldBeFound("finished.equals=" + DEFAULT_FINISHED);

        // Get all the spendingList where finished equals to UPDATED_FINISHED
        defaultSpendingShouldNotBeFound("finished.equals=" + UPDATED_FINISHED);
    }

    @Test
    @Transactional
    void getAllSpendingsByFinishedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where finished not equals to DEFAULT_FINISHED
        defaultSpendingShouldNotBeFound("finished.notEquals=" + DEFAULT_FINISHED);

        // Get all the spendingList where finished not equals to UPDATED_FINISHED
        defaultSpendingShouldBeFound("finished.notEquals=" + UPDATED_FINISHED);
    }

    @Test
    @Transactional
    void getAllSpendingsByFinishedIsInShouldWork() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where finished in DEFAULT_FINISHED or UPDATED_FINISHED
        defaultSpendingShouldBeFound("finished.in=" + DEFAULT_FINISHED + "," + UPDATED_FINISHED);

        // Get all the spendingList where finished equals to UPDATED_FINISHED
        defaultSpendingShouldNotBeFound("finished.in=" + UPDATED_FINISHED);
    }

    @Test
    @Transactional
    void getAllSpendingsByFinishedIsNullOrNotNull() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        // Get all the spendingList where finished is not null
        defaultSpendingShouldBeFound("finished.specified=true");

        // Get all the spendingList where finished is null
        defaultSpendingShouldNotBeFound("finished.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSpendingShouldBeFound(String filter) throws Exception {
        restSpendingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(spending.getId().intValue())))
            .andExpect(jsonPath("$.[*].payer").value(hasItem(DEFAULT_PAYER)))
            .andExpect(jsonPath("$.[*].nameCost").value(hasItem(DEFAULT_NAME_COST)))
            .andExpect(jsonPath("$.[*].cost").value(hasItem(DEFAULT_COST.doubleValue())))
            .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64Utils.encodeToString(DEFAULT_PHOTO))))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].paid").value(hasItem(DEFAULT_PAID.doubleValue())))
            .andExpect(jsonPath("$.[*].pending").value(hasItem(DEFAULT_PENDING.doubleValue())))
            .andExpect(jsonPath("$.[*].finished").value(hasItem(DEFAULT_FINISHED.booleanValue())));

        // Check, that the count call also returns 1
        restSpendingMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSpendingShouldNotBeFound(String filter) throws Exception {
        restSpendingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSpendingMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSpending() throws Exception {
        // Get the spending
        restSpendingMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSpending() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        int databaseSizeBeforeUpdate = spendingRepository.findAll().size();

        // Update the spending
        Spending updatedSpending = spendingRepository.findById(spending.getId()).get();
        // Disconnect from session so that the updates on updatedSpending are not directly saved in db
        em.detach(updatedSpending);
        updatedSpending
            .payer(UPDATED_PAYER)
            .nameCost(UPDATED_NAME_COST)
            .cost(UPDATED_COST)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .descripcion(UPDATED_DESCRIPCION)
            .paid(UPDATED_PAID)
            .pending(UPDATED_PENDING)
            .finished(UPDATED_FINISHED);

        restSpendingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSpending.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSpending))
            )
            .andExpect(status().isOk());

        // Validate the Spending in the database
        List<Spending> spendingList = spendingRepository.findAll();
        assertThat(spendingList).hasSize(databaseSizeBeforeUpdate);
        Spending testSpending = spendingList.get(spendingList.size() - 1);
        assertThat(testSpending.getPayer()).isEqualTo(UPDATED_PAYER);
        assertThat(testSpending.getNameCost()).isEqualTo(UPDATED_NAME_COST);
        assertThat(testSpending.getCost()).isEqualTo(UPDATED_COST);
        assertThat(testSpending.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testSpending.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testSpending.getDescripcion()).isEqualTo(UPDATED_DESCRIPCION);
        assertThat(testSpending.getPaid()).isEqualTo(UPDATED_PAID);
        assertThat(testSpending.getPending()).isEqualTo(UPDATED_PENDING);
        assertThat(testSpending.getFinished()).isEqualTo(UPDATED_FINISHED);
    }

    @Test
    @Transactional
    void putNonExistingSpending() throws Exception {
        int databaseSizeBeforeUpdate = spendingRepository.findAll().size();
        spending.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSpendingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, spending.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(spending))
            )
            .andExpect(status().isBadRequest());

        // Validate the Spending in the database
        List<Spending> spendingList = spendingRepository.findAll();
        assertThat(spendingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSpending() throws Exception {
        int databaseSizeBeforeUpdate = spendingRepository.findAll().size();
        spending.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpendingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(spending))
            )
            .andExpect(status().isBadRequest());

        // Validate the Spending in the database
        List<Spending> spendingList = spendingRepository.findAll();
        assertThat(spendingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSpending() throws Exception {
        int databaseSizeBeforeUpdate = spendingRepository.findAll().size();
        spending.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpendingMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(spending)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Spending in the database
        List<Spending> spendingList = spendingRepository.findAll();
        assertThat(spendingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSpendingWithPatch() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        int databaseSizeBeforeUpdate = spendingRepository.findAll().size();

        // Update the spending using partial update
        Spending partialUpdatedSpending = new Spending();
        partialUpdatedSpending.setId(spending.getId());

        partialUpdatedSpending
            .payer(UPDATED_PAYER)
            .nameCost(UPDATED_NAME_COST)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .descripcion(UPDATED_DESCRIPCION)
            .paid(UPDATED_PAID)
            .pending(UPDATED_PENDING)
            .finished(UPDATED_FINISHED);

        restSpendingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSpending.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSpending))
            )
            .andExpect(status().isOk());

        // Validate the Spending in the database
        List<Spending> spendingList = spendingRepository.findAll();
        assertThat(spendingList).hasSize(databaseSizeBeforeUpdate);
        Spending testSpending = spendingList.get(spendingList.size() - 1);
        assertThat(testSpending.getPayer()).isEqualTo(UPDATED_PAYER);
        assertThat(testSpending.getNameCost()).isEqualTo(UPDATED_NAME_COST);
        assertThat(testSpending.getCost()).isEqualTo(DEFAULT_COST);
        assertThat(testSpending.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testSpending.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testSpending.getDescripcion()).isEqualTo(UPDATED_DESCRIPCION);
        assertThat(testSpending.getPaid()).isEqualTo(UPDATED_PAID);
        assertThat(testSpending.getPending()).isEqualTo(UPDATED_PENDING);
        assertThat(testSpending.getFinished()).isEqualTo(UPDATED_FINISHED);
    }

    @Test
    @Transactional
    void fullUpdateSpendingWithPatch() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        int databaseSizeBeforeUpdate = spendingRepository.findAll().size();

        // Update the spending using partial update
        Spending partialUpdatedSpending = new Spending();
        partialUpdatedSpending.setId(spending.getId());

        partialUpdatedSpending
            .payer(UPDATED_PAYER)
            .nameCost(UPDATED_NAME_COST)
            .cost(UPDATED_COST)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .descripcion(UPDATED_DESCRIPCION)
            .paid(UPDATED_PAID)
            .pending(UPDATED_PENDING)
            .finished(UPDATED_FINISHED);

        restSpendingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSpending.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSpending))
            )
            .andExpect(status().isOk());

        // Validate the Spending in the database
        List<Spending> spendingList = spendingRepository.findAll();
        assertThat(spendingList).hasSize(databaseSizeBeforeUpdate);
        Spending testSpending = spendingList.get(spendingList.size() - 1);
        assertThat(testSpending.getPayer()).isEqualTo(UPDATED_PAYER);
        assertThat(testSpending.getNameCost()).isEqualTo(UPDATED_NAME_COST);
        assertThat(testSpending.getCost()).isEqualTo(UPDATED_COST);
        assertThat(testSpending.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testSpending.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testSpending.getDescripcion()).isEqualTo(UPDATED_DESCRIPCION);
        assertThat(testSpending.getPaid()).isEqualTo(UPDATED_PAID);
        assertThat(testSpending.getPending()).isEqualTo(UPDATED_PENDING);
        assertThat(testSpending.getFinished()).isEqualTo(UPDATED_FINISHED);
    }

    @Test
    @Transactional
    void patchNonExistingSpending() throws Exception {
        int databaseSizeBeforeUpdate = spendingRepository.findAll().size();
        spending.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSpendingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, spending.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(spending))
            )
            .andExpect(status().isBadRequest());

        // Validate the Spending in the database
        List<Spending> spendingList = spendingRepository.findAll();
        assertThat(spendingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSpending() throws Exception {
        int databaseSizeBeforeUpdate = spendingRepository.findAll().size();
        spending.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpendingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(spending))
            )
            .andExpect(status().isBadRequest());

        // Validate the Spending in the database
        List<Spending> spendingList = spendingRepository.findAll();
        assertThat(spendingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSpending() throws Exception {
        int databaseSizeBeforeUpdate = spendingRepository.findAll().size();
        spending.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpendingMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(spending)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Spending in the database
        List<Spending> spendingList = spendingRepository.findAll();
        assertThat(spendingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSpending() throws Exception {
        // Initialize the database
        spendingRepository.saveAndFlush(spending);

        int databaseSizeBeforeDelete = spendingRepository.findAll().size();

        // Delete the spending
        restSpendingMockMvc
            .perform(delete(ENTITY_API_URL_ID, spending.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Spending> spendingList = spendingRepository.findAll();
        assertThat(spendingList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
