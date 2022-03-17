package com.homies.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.homies.app.IntegrationTest;
import com.homies.app.domain.Products;
import com.homies.app.repository.ProductsRepository;
import com.homies.app.service.criteria.ProductsCriteria;
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
 * Integration tests for the {@link ProductsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProductsResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Float DEFAULT_PRICE = 1F;
    private static final Float UPDATED_PRICE = 2F;
    private static final Float SMALLER_PRICE = 1F - 1F;

    private static final byte[] DEFAULT_PHOTO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PHOTO = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PHOTO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PHOTO_CONTENT_TYPE = "image/png";

    private static final Float DEFAULT_UNITS = 1F;
    private static final Float UPDATED_UNITS = 2F;
    private static final Float SMALLER_UNITS = 1F - 1F;

    private static final String DEFAULT_TYPE_UNIT = "AAAAAAAAAA";
    private static final String UPDATED_TYPE_UNIT = "BBBBBBBBBB";

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATA_CREATED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_CREATED = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATA_CREATED = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_SHOPPING_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_SHOPPING_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_SHOPPING_DATE = LocalDate.ofEpochDay(-1L);

    private static final Boolean DEFAULT_PURCHASED = false;
    private static final Boolean UPDATED_PURCHASED = true;

    private static final Integer DEFAULT_USER_CREATED = 1;
    private static final Integer UPDATED_USER_CREATED = 2;
    private static final Integer SMALLER_USER_CREATED = 1 - 1;

    private static final String ENTITY_API_URL = "/api/products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductsMockMvc;

    private Products products;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Products createEntity(EntityManager em) {
        Products products = new Products()
            .name(DEFAULT_NAME)
            .price(DEFAULT_PRICE)
            .photo(DEFAULT_PHOTO)
            .photoContentType(DEFAULT_PHOTO_CONTENT_TYPE)
            .units(DEFAULT_UNITS)
            .typeUnit(DEFAULT_TYPE_UNIT)
            .note(DEFAULT_NOTE)
            .dataCreated(DEFAULT_DATA_CREATED)
            .shoppingDate(DEFAULT_SHOPPING_DATE)
            .purchased(DEFAULT_PURCHASED)
            .userCreated(DEFAULT_USER_CREATED);
        return products;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Products createUpdatedEntity(EntityManager em) {
        Products products = new Products()
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .units(UPDATED_UNITS)
            .typeUnit(UPDATED_TYPE_UNIT)
            .note(UPDATED_NOTE)
            .dataCreated(UPDATED_DATA_CREATED)
            .shoppingDate(UPDATED_SHOPPING_DATE)
            .purchased(UPDATED_PURCHASED)
            .userCreated(UPDATED_USER_CREATED);
        return products;
    }

    @BeforeEach
    public void initTest() {
        products = createEntity(em);
    }

    @Test
    @Transactional
    void createProducts() throws Exception {
        int databaseSizeBeforeCreate = productsRepository.findAll().size();
        // Create the Products
        restProductsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(products)))
            .andExpect(status().isCreated());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeCreate + 1);
        Products testProducts = productsList.get(productsList.size() - 1);
        assertThat(testProducts.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProducts.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testProducts.getPhoto()).isEqualTo(DEFAULT_PHOTO);
        assertThat(testProducts.getPhotoContentType()).isEqualTo(DEFAULT_PHOTO_CONTENT_TYPE);
        assertThat(testProducts.getUnits()).isEqualTo(DEFAULT_UNITS);
        assertThat(testProducts.getTypeUnit()).isEqualTo(DEFAULT_TYPE_UNIT);
        assertThat(testProducts.getNote()).isEqualTo(DEFAULT_NOTE);
        assertThat(testProducts.getDataCreated()).isEqualTo(DEFAULT_DATA_CREATED);
        assertThat(testProducts.getShoppingDate()).isEqualTo(DEFAULT_SHOPPING_DATE);
        assertThat(testProducts.getPurchased()).isEqualTo(DEFAULT_PURCHASED);
        assertThat(testProducts.getUserCreated()).isEqualTo(DEFAULT_USER_CREATED);
    }

    @Test
    @Transactional
    void createProductsWithExistingId() throws Exception {
        // Create the Products with an existing ID
        products.setId(1L);

        int databaseSizeBeforeCreate = productsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(products)))
            .andExpect(status().isBadRequest());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = productsRepository.findAll().size();
        // set the field null
        products.setName(null);

        // Create the Products, which fails.

        restProductsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(products)))
            .andExpect(status().isBadRequest());

        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUnitsIsRequired() throws Exception {
        int databaseSizeBeforeTest = productsRepository.findAll().size();
        // set the field null
        products.setUnits(null);

        // Create the Products, which fails.

        restProductsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(products)))
            .andExpect(status().isBadRequest());

        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProducts() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList
        restProductsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(products.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64Utils.encodeToString(DEFAULT_PHOTO))))
            .andExpect(jsonPath("$.[*].units").value(hasItem(DEFAULT_UNITS.doubleValue())))
            .andExpect(jsonPath("$.[*].typeUnit").value(hasItem(DEFAULT_TYPE_UNIT)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].dataCreated").value(hasItem(DEFAULT_DATA_CREATED.toString())))
            .andExpect(jsonPath("$.[*].shoppingDate").value(hasItem(DEFAULT_SHOPPING_DATE.toString())))
            .andExpect(jsonPath("$.[*].purchased").value(hasItem(DEFAULT_PURCHASED.booleanValue())))
            .andExpect(jsonPath("$.[*].userCreated").value(hasItem(DEFAULT_USER_CREATED)));
    }

    @Test
    @Transactional
    void getProducts() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get the products
        restProductsMockMvc
            .perform(get(ENTITY_API_URL_ID, products.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(products.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()))
            .andExpect(jsonPath("$.photoContentType").value(DEFAULT_PHOTO_CONTENT_TYPE))
            .andExpect(jsonPath("$.photo").value(Base64Utils.encodeToString(DEFAULT_PHOTO)))
            .andExpect(jsonPath("$.units").value(DEFAULT_UNITS.doubleValue()))
            .andExpect(jsonPath("$.typeUnit").value(DEFAULT_TYPE_UNIT))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE))
            .andExpect(jsonPath("$.dataCreated").value(DEFAULT_DATA_CREATED.toString()))
            .andExpect(jsonPath("$.shoppingDate").value(DEFAULT_SHOPPING_DATE.toString()))
            .andExpect(jsonPath("$.purchased").value(DEFAULT_PURCHASED.booleanValue()))
            .andExpect(jsonPath("$.userCreated").value(DEFAULT_USER_CREATED));
    }

    @Test
    @Transactional
    void getProductsByIdFiltering() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        Long id = products.getId();

        defaultProductsShouldBeFound("id.equals=" + id);
        defaultProductsShouldNotBeFound("id.notEquals=" + id);

        defaultProductsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProductsShouldNotBeFound("id.greaterThan=" + id);

        defaultProductsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProductsShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProductsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where name equals to DEFAULT_NAME
        defaultProductsShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the productsList where name equals to UPDATED_NAME
        defaultProductsShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where name not equals to DEFAULT_NAME
        defaultProductsShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the productsList where name not equals to UPDATED_NAME
        defaultProductsShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where name in DEFAULT_NAME or UPDATED_NAME
        defaultProductsShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the productsList where name equals to UPDATED_NAME
        defaultProductsShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where name is not null
        defaultProductsShouldBeFound("name.specified=true");

        // Get all the productsList where name is null
        defaultProductsShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByNameContainsSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where name contains DEFAULT_NAME
        defaultProductsShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the productsList where name contains UPDATED_NAME
        defaultProductsShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where name does not contain DEFAULT_NAME
        defaultProductsShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the productsList where name does not contain UPDATED_NAME
        defaultProductsShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where price equals to DEFAULT_PRICE
        defaultProductsShouldBeFound("price.equals=" + DEFAULT_PRICE);

        // Get all the productsList where price equals to UPDATED_PRICE
        defaultProductsShouldNotBeFound("price.equals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where price not equals to DEFAULT_PRICE
        defaultProductsShouldNotBeFound("price.notEquals=" + DEFAULT_PRICE);

        // Get all the productsList where price not equals to UPDATED_PRICE
        defaultProductsShouldBeFound("price.notEquals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsInShouldWork() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where price in DEFAULT_PRICE or UPDATED_PRICE
        defaultProductsShouldBeFound("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE);

        // Get all the productsList where price equals to UPDATED_PRICE
        defaultProductsShouldNotBeFound("price.in=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where price is not null
        defaultProductsShouldBeFound("price.specified=true");

        // Get all the productsList where price is null
        defaultProductsShouldNotBeFound("price.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where price is greater than or equal to DEFAULT_PRICE
        defaultProductsShouldBeFound("price.greaterThanOrEqual=" + DEFAULT_PRICE);

        // Get all the productsList where price is greater than or equal to UPDATED_PRICE
        defaultProductsShouldNotBeFound("price.greaterThanOrEqual=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where price is less than or equal to DEFAULT_PRICE
        defaultProductsShouldBeFound("price.lessThanOrEqual=" + DEFAULT_PRICE);

        // Get all the productsList where price is less than or equal to SMALLER_PRICE
        defaultProductsShouldNotBeFound("price.lessThanOrEqual=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where price is less than DEFAULT_PRICE
        defaultProductsShouldNotBeFound("price.lessThan=" + DEFAULT_PRICE);

        // Get all the productsList where price is less than UPDATED_PRICE
        defaultProductsShouldBeFound("price.lessThan=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where price is greater than DEFAULT_PRICE
        defaultProductsShouldNotBeFound("price.greaterThan=" + DEFAULT_PRICE);

        // Get all the productsList where price is greater than SMALLER_PRICE
        defaultProductsShouldBeFound("price.greaterThan=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByUnitsIsEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where units equals to DEFAULT_UNITS
        defaultProductsShouldBeFound("units.equals=" + DEFAULT_UNITS);

        // Get all the productsList where units equals to UPDATED_UNITS
        defaultProductsShouldNotBeFound("units.equals=" + UPDATED_UNITS);
    }

    @Test
    @Transactional
    void getAllProductsByUnitsIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where units not equals to DEFAULT_UNITS
        defaultProductsShouldNotBeFound("units.notEquals=" + DEFAULT_UNITS);

        // Get all the productsList where units not equals to UPDATED_UNITS
        defaultProductsShouldBeFound("units.notEquals=" + UPDATED_UNITS);
    }

    @Test
    @Transactional
    void getAllProductsByUnitsIsInShouldWork() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where units in DEFAULT_UNITS or UPDATED_UNITS
        defaultProductsShouldBeFound("units.in=" + DEFAULT_UNITS + "," + UPDATED_UNITS);

        // Get all the productsList where units equals to UPDATED_UNITS
        defaultProductsShouldNotBeFound("units.in=" + UPDATED_UNITS);
    }

    @Test
    @Transactional
    void getAllProductsByUnitsIsNullOrNotNull() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where units is not null
        defaultProductsShouldBeFound("units.specified=true");

        // Get all the productsList where units is null
        defaultProductsShouldNotBeFound("units.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByUnitsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where units is greater than or equal to DEFAULT_UNITS
        defaultProductsShouldBeFound("units.greaterThanOrEqual=" + DEFAULT_UNITS);

        // Get all the productsList where units is greater than or equal to UPDATED_UNITS
        defaultProductsShouldNotBeFound("units.greaterThanOrEqual=" + UPDATED_UNITS);
    }

    @Test
    @Transactional
    void getAllProductsByUnitsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where units is less than or equal to DEFAULT_UNITS
        defaultProductsShouldBeFound("units.lessThanOrEqual=" + DEFAULT_UNITS);

        // Get all the productsList where units is less than or equal to SMALLER_UNITS
        defaultProductsShouldNotBeFound("units.lessThanOrEqual=" + SMALLER_UNITS);
    }

    @Test
    @Transactional
    void getAllProductsByUnitsIsLessThanSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where units is less than DEFAULT_UNITS
        defaultProductsShouldNotBeFound("units.lessThan=" + DEFAULT_UNITS);

        // Get all the productsList where units is less than UPDATED_UNITS
        defaultProductsShouldBeFound("units.lessThan=" + UPDATED_UNITS);
    }

    @Test
    @Transactional
    void getAllProductsByUnitsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where units is greater than DEFAULT_UNITS
        defaultProductsShouldNotBeFound("units.greaterThan=" + DEFAULT_UNITS);

        // Get all the productsList where units is greater than SMALLER_UNITS
        defaultProductsShouldBeFound("units.greaterThan=" + SMALLER_UNITS);
    }

    @Test
    @Transactional
    void getAllProductsByTypeUnitIsEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where typeUnit equals to DEFAULT_TYPE_UNIT
        defaultProductsShouldBeFound("typeUnit.equals=" + DEFAULT_TYPE_UNIT);

        // Get all the productsList where typeUnit equals to UPDATED_TYPE_UNIT
        defaultProductsShouldNotBeFound("typeUnit.equals=" + UPDATED_TYPE_UNIT);
    }

    @Test
    @Transactional
    void getAllProductsByTypeUnitIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where typeUnit not equals to DEFAULT_TYPE_UNIT
        defaultProductsShouldNotBeFound("typeUnit.notEquals=" + DEFAULT_TYPE_UNIT);

        // Get all the productsList where typeUnit not equals to UPDATED_TYPE_UNIT
        defaultProductsShouldBeFound("typeUnit.notEquals=" + UPDATED_TYPE_UNIT);
    }

    @Test
    @Transactional
    void getAllProductsByTypeUnitIsInShouldWork() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where typeUnit in DEFAULT_TYPE_UNIT or UPDATED_TYPE_UNIT
        defaultProductsShouldBeFound("typeUnit.in=" + DEFAULT_TYPE_UNIT + "," + UPDATED_TYPE_UNIT);

        // Get all the productsList where typeUnit equals to UPDATED_TYPE_UNIT
        defaultProductsShouldNotBeFound("typeUnit.in=" + UPDATED_TYPE_UNIT);
    }

    @Test
    @Transactional
    void getAllProductsByTypeUnitIsNullOrNotNull() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where typeUnit is not null
        defaultProductsShouldBeFound("typeUnit.specified=true");

        // Get all the productsList where typeUnit is null
        defaultProductsShouldNotBeFound("typeUnit.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByTypeUnitContainsSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where typeUnit contains DEFAULT_TYPE_UNIT
        defaultProductsShouldBeFound("typeUnit.contains=" + DEFAULT_TYPE_UNIT);

        // Get all the productsList where typeUnit contains UPDATED_TYPE_UNIT
        defaultProductsShouldNotBeFound("typeUnit.contains=" + UPDATED_TYPE_UNIT);
    }

    @Test
    @Transactional
    void getAllProductsByTypeUnitNotContainsSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where typeUnit does not contain DEFAULT_TYPE_UNIT
        defaultProductsShouldNotBeFound("typeUnit.doesNotContain=" + DEFAULT_TYPE_UNIT);

        // Get all the productsList where typeUnit does not contain UPDATED_TYPE_UNIT
        defaultProductsShouldBeFound("typeUnit.doesNotContain=" + UPDATED_TYPE_UNIT);
    }

    @Test
    @Transactional
    void getAllProductsByNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where note equals to DEFAULT_NOTE
        defaultProductsShouldBeFound("note.equals=" + DEFAULT_NOTE);

        // Get all the productsList where note equals to UPDATED_NOTE
        defaultProductsShouldNotBeFound("note.equals=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllProductsByNoteIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where note not equals to DEFAULT_NOTE
        defaultProductsShouldNotBeFound("note.notEquals=" + DEFAULT_NOTE);

        // Get all the productsList where note not equals to UPDATED_NOTE
        defaultProductsShouldBeFound("note.notEquals=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllProductsByNoteIsInShouldWork() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where note in DEFAULT_NOTE or UPDATED_NOTE
        defaultProductsShouldBeFound("note.in=" + DEFAULT_NOTE + "," + UPDATED_NOTE);

        // Get all the productsList where note equals to UPDATED_NOTE
        defaultProductsShouldNotBeFound("note.in=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllProductsByNoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where note is not null
        defaultProductsShouldBeFound("note.specified=true");

        // Get all the productsList where note is null
        defaultProductsShouldNotBeFound("note.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByNoteContainsSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where note contains DEFAULT_NOTE
        defaultProductsShouldBeFound("note.contains=" + DEFAULT_NOTE);

        // Get all the productsList where note contains UPDATED_NOTE
        defaultProductsShouldNotBeFound("note.contains=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllProductsByNoteNotContainsSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where note does not contain DEFAULT_NOTE
        defaultProductsShouldNotBeFound("note.doesNotContain=" + DEFAULT_NOTE);

        // Get all the productsList where note does not contain UPDATED_NOTE
        defaultProductsShouldBeFound("note.doesNotContain=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllProductsByDataCreatedIsEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where dataCreated equals to DEFAULT_DATA_CREATED
        defaultProductsShouldBeFound("dataCreated.equals=" + DEFAULT_DATA_CREATED);

        // Get all the productsList where dataCreated equals to UPDATED_DATA_CREATED
        defaultProductsShouldNotBeFound("dataCreated.equals=" + UPDATED_DATA_CREATED);
    }

    @Test
    @Transactional
    void getAllProductsByDataCreatedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where dataCreated not equals to DEFAULT_DATA_CREATED
        defaultProductsShouldNotBeFound("dataCreated.notEquals=" + DEFAULT_DATA_CREATED);

        // Get all the productsList where dataCreated not equals to UPDATED_DATA_CREATED
        defaultProductsShouldBeFound("dataCreated.notEquals=" + UPDATED_DATA_CREATED);
    }

    @Test
    @Transactional
    void getAllProductsByDataCreatedIsInShouldWork() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where dataCreated in DEFAULT_DATA_CREATED or UPDATED_DATA_CREATED
        defaultProductsShouldBeFound("dataCreated.in=" + DEFAULT_DATA_CREATED + "," + UPDATED_DATA_CREATED);

        // Get all the productsList where dataCreated equals to UPDATED_DATA_CREATED
        defaultProductsShouldNotBeFound("dataCreated.in=" + UPDATED_DATA_CREATED);
    }

    @Test
    @Transactional
    void getAllProductsByDataCreatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where dataCreated is not null
        defaultProductsShouldBeFound("dataCreated.specified=true");

        // Get all the productsList where dataCreated is null
        defaultProductsShouldNotBeFound("dataCreated.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByDataCreatedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where dataCreated is greater than or equal to DEFAULT_DATA_CREATED
        defaultProductsShouldBeFound("dataCreated.greaterThanOrEqual=" + DEFAULT_DATA_CREATED);

        // Get all the productsList where dataCreated is greater than or equal to UPDATED_DATA_CREATED
        defaultProductsShouldNotBeFound("dataCreated.greaterThanOrEqual=" + UPDATED_DATA_CREATED);
    }

    @Test
    @Transactional
    void getAllProductsByDataCreatedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where dataCreated is less than or equal to DEFAULT_DATA_CREATED
        defaultProductsShouldBeFound("dataCreated.lessThanOrEqual=" + DEFAULT_DATA_CREATED);

        // Get all the productsList where dataCreated is less than or equal to SMALLER_DATA_CREATED
        defaultProductsShouldNotBeFound("dataCreated.lessThanOrEqual=" + SMALLER_DATA_CREATED);
    }

    @Test
    @Transactional
    void getAllProductsByDataCreatedIsLessThanSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where dataCreated is less than DEFAULT_DATA_CREATED
        defaultProductsShouldNotBeFound("dataCreated.lessThan=" + DEFAULT_DATA_CREATED);

        // Get all the productsList where dataCreated is less than UPDATED_DATA_CREATED
        defaultProductsShouldBeFound("dataCreated.lessThan=" + UPDATED_DATA_CREATED);
    }

    @Test
    @Transactional
    void getAllProductsByDataCreatedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where dataCreated is greater than DEFAULT_DATA_CREATED
        defaultProductsShouldNotBeFound("dataCreated.greaterThan=" + DEFAULT_DATA_CREATED);

        // Get all the productsList where dataCreated is greater than SMALLER_DATA_CREATED
        defaultProductsShouldBeFound("dataCreated.greaterThan=" + SMALLER_DATA_CREATED);
    }

    @Test
    @Transactional
    void getAllProductsByShoppingDateIsEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where shoppingDate equals to DEFAULT_SHOPPING_DATE
        defaultProductsShouldBeFound("shoppingDate.equals=" + DEFAULT_SHOPPING_DATE);

        // Get all the productsList where shoppingDate equals to UPDATED_SHOPPING_DATE
        defaultProductsShouldNotBeFound("shoppingDate.equals=" + UPDATED_SHOPPING_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByShoppingDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where shoppingDate not equals to DEFAULT_SHOPPING_DATE
        defaultProductsShouldNotBeFound("shoppingDate.notEquals=" + DEFAULT_SHOPPING_DATE);

        // Get all the productsList where shoppingDate not equals to UPDATED_SHOPPING_DATE
        defaultProductsShouldBeFound("shoppingDate.notEquals=" + UPDATED_SHOPPING_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByShoppingDateIsInShouldWork() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where shoppingDate in DEFAULT_SHOPPING_DATE or UPDATED_SHOPPING_DATE
        defaultProductsShouldBeFound("shoppingDate.in=" + DEFAULT_SHOPPING_DATE + "," + UPDATED_SHOPPING_DATE);

        // Get all the productsList where shoppingDate equals to UPDATED_SHOPPING_DATE
        defaultProductsShouldNotBeFound("shoppingDate.in=" + UPDATED_SHOPPING_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByShoppingDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where shoppingDate is not null
        defaultProductsShouldBeFound("shoppingDate.specified=true");

        // Get all the productsList where shoppingDate is null
        defaultProductsShouldNotBeFound("shoppingDate.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByShoppingDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where shoppingDate is greater than or equal to DEFAULT_SHOPPING_DATE
        defaultProductsShouldBeFound("shoppingDate.greaterThanOrEqual=" + DEFAULT_SHOPPING_DATE);

        // Get all the productsList where shoppingDate is greater than or equal to UPDATED_SHOPPING_DATE
        defaultProductsShouldNotBeFound("shoppingDate.greaterThanOrEqual=" + UPDATED_SHOPPING_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByShoppingDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where shoppingDate is less than or equal to DEFAULT_SHOPPING_DATE
        defaultProductsShouldBeFound("shoppingDate.lessThanOrEqual=" + DEFAULT_SHOPPING_DATE);

        // Get all the productsList where shoppingDate is less than or equal to SMALLER_SHOPPING_DATE
        defaultProductsShouldNotBeFound("shoppingDate.lessThanOrEqual=" + SMALLER_SHOPPING_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByShoppingDateIsLessThanSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where shoppingDate is less than DEFAULT_SHOPPING_DATE
        defaultProductsShouldNotBeFound("shoppingDate.lessThan=" + DEFAULT_SHOPPING_DATE);

        // Get all the productsList where shoppingDate is less than UPDATED_SHOPPING_DATE
        defaultProductsShouldBeFound("shoppingDate.lessThan=" + UPDATED_SHOPPING_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByShoppingDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where shoppingDate is greater than DEFAULT_SHOPPING_DATE
        defaultProductsShouldNotBeFound("shoppingDate.greaterThan=" + DEFAULT_SHOPPING_DATE);

        // Get all the productsList where shoppingDate is greater than SMALLER_SHOPPING_DATE
        defaultProductsShouldBeFound("shoppingDate.greaterThan=" + SMALLER_SHOPPING_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByPurchasedIsEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where purchased equals to DEFAULT_PURCHASED
        defaultProductsShouldBeFound("purchased.equals=" + DEFAULT_PURCHASED);

        // Get all the productsList where purchased equals to UPDATED_PURCHASED
        defaultProductsShouldNotBeFound("purchased.equals=" + UPDATED_PURCHASED);
    }

    @Test
    @Transactional
    void getAllProductsByPurchasedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where purchased not equals to DEFAULT_PURCHASED
        defaultProductsShouldNotBeFound("purchased.notEquals=" + DEFAULT_PURCHASED);

        // Get all the productsList where purchased not equals to UPDATED_PURCHASED
        defaultProductsShouldBeFound("purchased.notEquals=" + UPDATED_PURCHASED);
    }

    @Test
    @Transactional
    void getAllProductsByPurchasedIsInShouldWork() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where purchased in DEFAULT_PURCHASED or UPDATED_PURCHASED
        defaultProductsShouldBeFound("purchased.in=" + DEFAULT_PURCHASED + "," + UPDATED_PURCHASED);

        // Get all the productsList where purchased equals to UPDATED_PURCHASED
        defaultProductsShouldNotBeFound("purchased.in=" + UPDATED_PURCHASED);
    }

    @Test
    @Transactional
    void getAllProductsByPurchasedIsNullOrNotNull() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where purchased is not null
        defaultProductsShouldBeFound("purchased.specified=true");

        // Get all the productsList where purchased is null
        defaultProductsShouldNotBeFound("purchased.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByUserCreatedIsEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where userCreated equals to DEFAULT_USER_CREATED
        defaultProductsShouldBeFound("userCreated.equals=" + DEFAULT_USER_CREATED);

        // Get all the productsList where userCreated equals to UPDATED_USER_CREATED
        defaultProductsShouldNotBeFound("userCreated.equals=" + UPDATED_USER_CREATED);
    }

    @Test
    @Transactional
    void getAllProductsByUserCreatedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where userCreated not equals to DEFAULT_USER_CREATED
        defaultProductsShouldNotBeFound("userCreated.notEquals=" + DEFAULT_USER_CREATED);

        // Get all the productsList where userCreated not equals to UPDATED_USER_CREATED
        defaultProductsShouldBeFound("userCreated.notEquals=" + UPDATED_USER_CREATED);
    }

    @Test
    @Transactional
    void getAllProductsByUserCreatedIsInShouldWork() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where userCreated in DEFAULT_USER_CREATED or UPDATED_USER_CREATED
        defaultProductsShouldBeFound("userCreated.in=" + DEFAULT_USER_CREATED + "," + UPDATED_USER_CREATED);

        // Get all the productsList where userCreated equals to UPDATED_USER_CREATED
        defaultProductsShouldNotBeFound("userCreated.in=" + UPDATED_USER_CREATED);
    }

    @Test
    @Transactional
    void getAllProductsByUserCreatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where userCreated is not null
        defaultProductsShouldBeFound("userCreated.specified=true");

        // Get all the productsList where userCreated is null
        defaultProductsShouldNotBeFound("userCreated.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByUserCreatedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where userCreated is greater than or equal to DEFAULT_USER_CREATED
        defaultProductsShouldBeFound("userCreated.greaterThanOrEqual=" + DEFAULT_USER_CREATED);

        // Get all the productsList where userCreated is greater than or equal to UPDATED_USER_CREATED
        defaultProductsShouldNotBeFound("userCreated.greaterThanOrEqual=" + UPDATED_USER_CREATED);
    }

    @Test
    @Transactional
    void getAllProductsByUserCreatedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where userCreated is less than or equal to DEFAULT_USER_CREATED
        defaultProductsShouldBeFound("userCreated.lessThanOrEqual=" + DEFAULT_USER_CREATED);

        // Get all the productsList where userCreated is less than or equal to SMALLER_USER_CREATED
        defaultProductsShouldNotBeFound("userCreated.lessThanOrEqual=" + SMALLER_USER_CREATED);
    }

    @Test
    @Transactional
    void getAllProductsByUserCreatedIsLessThanSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where userCreated is less than DEFAULT_USER_CREATED
        defaultProductsShouldNotBeFound("userCreated.lessThan=" + DEFAULT_USER_CREATED);

        // Get all the productsList where userCreated is less than UPDATED_USER_CREATED
        defaultProductsShouldBeFound("userCreated.lessThan=" + UPDATED_USER_CREATED);
    }

    @Test
    @Transactional
    void getAllProductsByUserCreatedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where userCreated is greater than DEFAULT_USER_CREATED
        defaultProductsShouldNotBeFound("userCreated.greaterThan=" + DEFAULT_USER_CREATED);

        // Get all the productsList where userCreated is greater than SMALLER_USER_CREATED
        defaultProductsShouldBeFound("userCreated.greaterThan=" + SMALLER_USER_CREATED);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProductsShouldBeFound(String filter) throws Exception {
        restProductsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(products.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64Utils.encodeToString(DEFAULT_PHOTO))))
            .andExpect(jsonPath("$.[*].units").value(hasItem(DEFAULT_UNITS.doubleValue())))
            .andExpect(jsonPath("$.[*].typeUnit").value(hasItem(DEFAULT_TYPE_UNIT)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].dataCreated").value(hasItem(DEFAULT_DATA_CREATED.toString())))
            .andExpect(jsonPath("$.[*].shoppingDate").value(hasItem(DEFAULT_SHOPPING_DATE.toString())))
            .andExpect(jsonPath("$.[*].purchased").value(hasItem(DEFAULT_PURCHASED.booleanValue())))
            .andExpect(jsonPath("$.[*].userCreated").value(hasItem(DEFAULT_USER_CREATED)));

        // Check, that the count call also returns 1
        restProductsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProductsShouldNotBeFound(String filter) throws Exception {
        restProductsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProductsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProducts() throws Exception {
        // Get the products
        restProductsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProducts() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        int databaseSizeBeforeUpdate = productsRepository.findAll().size();

        // Update the products
        Products updatedProducts = productsRepository.findById(products.getId()).get();
        // Disconnect from session so that the updates on updatedProducts are not directly saved in db
        em.detach(updatedProducts);
        updatedProducts
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .units(UPDATED_UNITS)
            .typeUnit(UPDATED_TYPE_UNIT)
            .note(UPDATED_NOTE)
            .dataCreated(UPDATED_DATA_CREATED)
            .shoppingDate(UPDATED_SHOPPING_DATE)
            .purchased(UPDATED_PURCHASED)
            .userCreated(UPDATED_USER_CREATED);

        restProductsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProducts.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedProducts))
            )
            .andExpect(status().isOk());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
        Products testProducts = productsList.get(productsList.size() - 1);
        assertThat(testProducts.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProducts.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testProducts.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testProducts.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testProducts.getUnits()).isEqualTo(UPDATED_UNITS);
        assertThat(testProducts.getTypeUnit()).isEqualTo(UPDATED_TYPE_UNIT);
        assertThat(testProducts.getNote()).isEqualTo(UPDATED_NOTE);
        assertThat(testProducts.getDataCreated()).isEqualTo(UPDATED_DATA_CREATED);
        assertThat(testProducts.getShoppingDate()).isEqualTo(UPDATED_SHOPPING_DATE);
        assertThat(testProducts.getPurchased()).isEqualTo(UPDATED_PURCHASED);
        assertThat(testProducts.getUserCreated()).isEqualTo(UPDATED_USER_CREATED);
    }

    @Test
    @Transactional
    void putNonExistingProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, products.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(products))
            )
            .andExpect(status().isBadRequest());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(products))
            )
            .andExpect(status().isBadRequest());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(products)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProductsWithPatch() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        int databaseSizeBeforeUpdate = productsRepository.findAll().size();

        // Update the products using partial update
        Products partialUpdatedProducts = new Products();
        partialUpdatedProducts.setId(products.getId());

        partialUpdatedProducts
            .name(UPDATED_NAME)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .shoppingDate(UPDATED_SHOPPING_DATE)
            .purchased(UPDATED_PURCHASED)
            .userCreated(UPDATED_USER_CREATED);

        restProductsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProducts.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProducts))
            )
            .andExpect(status().isOk());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
        Products testProducts = productsList.get(productsList.size() - 1);
        assertThat(testProducts.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProducts.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testProducts.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testProducts.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testProducts.getUnits()).isEqualTo(DEFAULT_UNITS);
        assertThat(testProducts.getTypeUnit()).isEqualTo(DEFAULT_TYPE_UNIT);
        assertThat(testProducts.getNote()).isEqualTo(DEFAULT_NOTE);
        assertThat(testProducts.getDataCreated()).isEqualTo(DEFAULT_DATA_CREATED);
        assertThat(testProducts.getShoppingDate()).isEqualTo(UPDATED_SHOPPING_DATE);
        assertThat(testProducts.getPurchased()).isEqualTo(UPDATED_PURCHASED);
        assertThat(testProducts.getUserCreated()).isEqualTo(UPDATED_USER_CREATED);
    }

    @Test
    @Transactional
    void fullUpdateProductsWithPatch() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        int databaseSizeBeforeUpdate = productsRepository.findAll().size();

        // Update the products using partial update
        Products partialUpdatedProducts = new Products();
        partialUpdatedProducts.setId(products.getId());

        partialUpdatedProducts
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .units(UPDATED_UNITS)
            .typeUnit(UPDATED_TYPE_UNIT)
            .note(UPDATED_NOTE)
            .dataCreated(UPDATED_DATA_CREATED)
            .shoppingDate(UPDATED_SHOPPING_DATE)
            .purchased(UPDATED_PURCHASED)
            .userCreated(UPDATED_USER_CREATED);

        restProductsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProducts.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProducts))
            )
            .andExpect(status().isOk());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
        Products testProducts = productsList.get(productsList.size() - 1);
        assertThat(testProducts.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProducts.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testProducts.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testProducts.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testProducts.getUnits()).isEqualTo(UPDATED_UNITS);
        assertThat(testProducts.getTypeUnit()).isEqualTo(UPDATED_TYPE_UNIT);
        assertThat(testProducts.getNote()).isEqualTo(UPDATED_NOTE);
        assertThat(testProducts.getDataCreated()).isEqualTo(UPDATED_DATA_CREATED);
        assertThat(testProducts.getShoppingDate()).isEqualTo(UPDATED_SHOPPING_DATE);
        assertThat(testProducts.getPurchased()).isEqualTo(UPDATED_PURCHASED);
        assertThat(testProducts.getUserCreated()).isEqualTo(UPDATED_USER_CREATED);
    }

    @Test
    @Transactional
    void patchNonExistingProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, products.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(products))
            )
            .andExpect(status().isBadRequest());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(products))
            )
            .andExpect(status().isBadRequest());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(products)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProducts() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        int databaseSizeBeforeDelete = productsRepository.findAll().size();

        // Delete the products
        restProductsMockMvc
            .perform(delete(ENTITY_API_URL_ID, products.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
