package com.homies.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.homies.app.IntegrationTest;
import com.homies.app.domain.SpendingList;
import com.homies.app.repository.SpendingListRepository;
import com.homies.app.service.criteria.SpendingListCriteria;
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
 * Integration tests for the {@link SpendingListResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SpendingListResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Float DEFAULT_TOTAL = 0F;
    private static final Float UPDATED_TOTAL = 1F;
    private static final Float SMALLER_TOTAL = 0F - 1F;

    private static final String ENTITY_API_URL = "/api/spending-lists";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SpendingListRepository spendingListRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSpendingListMockMvc;

    private SpendingList spendingList;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SpendingList createEntity(EntityManager em) {
        SpendingList spendingList = new SpendingList().name(DEFAULT_NAME).total(DEFAULT_TOTAL);
        return spendingList;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SpendingList createUpdatedEntity(EntityManager em) {
        SpendingList spendingList = new SpendingList().name(UPDATED_NAME).total(UPDATED_TOTAL);
        return spendingList;
    }

    @BeforeEach
    public void initTest() {
        spendingList = createEntity(em);
    }

    @Test
    @Transactional
    void createSpendingList() throws Exception {
        int databaseSizeBeforeCreate = spendingListRepository.findAll().size();
        // Create the SpendingList
        restSpendingListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(spendingList)))
            .andExpect(status().isCreated());

        // Validate the SpendingList in the database
        List<SpendingList> spendingListList = spendingListRepository.findAll();
        assertThat(spendingListList).hasSize(databaseSizeBeforeCreate + 1);
        SpendingList testSpendingList = spendingListList.get(spendingListList.size() - 1);
        assertThat(testSpendingList.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSpendingList.getTotal()).isEqualTo(DEFAULT_TOTAL);
    }

    @Test
    @Transactional
    void createSpendingListWithExistingId() throws Exception {
        // Create the SpendingList with an existing ID
        spendingList.setId(1L);

        int databaseSizeBeforeCreate = spendingListRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSpendingListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(spendingList)))
            .andExpect(status().isBadRequest());

        // Validate the SpendingList in the database
        List<SpendingList> spendingListList = spendingListRepository.findAll();
        assertThat(spendingListList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = spendingListRepository.findAll().size();
        // set the field null
        spendingList.setName(null);

        // Create the SpendingList, which fails.

        restSpendingListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(spendingList)))
            .andExpect(status().isBadRequest());

        List<SpendingList> spendingListList = spendingListRepository.findAll();
        assertThat(spendingListList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSpendingLists() throws Exception {
        // Initialize the database
        spendingListRepository.saveAndFlush(spendingList);

        // Get all the spendingListList
        restSpendingListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(spendingList.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].total").value(hasItem(DEFAULT_TOTAL.doubleValue())));
    }

    @Test
    @Transactional
    void getSpendingList() throws Exception {
        // Initialize the database
        spendingListRepository.saveAndFlush(spendingList);

        // Get the spendingList
        restSpendingListMockMvc
            .perform(get(ENTITY_API_URL_ID, spendingList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(spendingList.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.total").value(DEFAULT_TOTAL.doubleValue()));
    }

    @Test
    @Transactional
    void getSpendingListsByIdFiltering() throws Exception {
        // Initialize the database
        spendingListRepository.saveAndFlush(spendingList);

        Long id = spendingList.getId();

        defaultSpendingListShouldBeFound("id.equals=" + id);
        defaultSpendingListShouldNotBeFound("id.notEquals=" + id);

        defaultSpendingListShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSpendingListShouldNotBeFound("id.greaterThan=" + id);

        defaultSpendingListShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSpendingListShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSpendingListsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        spendingListRepository.saveAndFlush(spendingList);

        // Get all the spendingListList where name equals to DEFAULT_NAME
        defaultSpendingListShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the spendingListList where name equals to UPDATED_NAME
        defaultSpendingListShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSpendingListsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        spendingListRepository.saveAndFlush(spendingList);

        // Get all the spendingListList where name not equals to DEFAULT_NAME
        defaultSpendingListShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the spendingListList where name not equals to UPDATED_NAME
        defaultSpendingListShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSpendingListsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        spendingListRepository.saveAndFlush(spendingList);

        // Get all the spendingListList where name in DEFAULT_NAME or UPDATED_NAME
        defaultSpendingListShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the spendingListList where name equals to UPDATED_NAME
        defaultSpendingListShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSpendingListsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        spendingListRepository.saveAndFlush(spendingList);

        // Get all the spendingListList where name is not null
        defaultSpendingListShouldBeFound("name.specified=true");

        // Get all the spendingListList where name is null
        defaultSpendingListShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllSpendingListsByNameContainsSomething() throws Exception {
        // Initialize the database
        spendingListRepository.saveAndFlush(spendingList);

        // Get all the spendingListList where name contains DEFAULT_NAME
        defaultSpendingListShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the spendingListList where name contains UPDATED_NAME
        defaultSpendingListShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSpendingListsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        spendingListRepository.saveAndFlush(spendingList);

        // Get all the spendingListList where name does not contain DEFAULT_NAME
        defaultSpendingListShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the spendingListList where name does not contain UPDATED_NAME
        defaultSpendingListShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSpendingListsByTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        spendingListRepository.saveAndFlush(spendingList);

        // Get all the spendingListList where total equals to DEFAULT_TOTAL
        defaultSpendingListShouldBeFound("total.equals=" + DEFAULT_TOTAL);

        // Get all the spendingListList where total equals to UPDATED_TOTAL
        defaultSpendingListShouldNotBeFound("total.equals=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllSpendingListsByTotalIsNotEqualToSomething() throws Exception {
        // Initialize the database
        spendingListRepository.saveAndFlush(spendingList);

        // Get all the spendingListList where total not equals to DEFAULT_TOTAL
        defaultSpendingListShouldNotBeFound("total.notEquals=" + DEFAULT_TOTAL);

        // Get all the spendingListList where total not equals to UPDATED_TOTAL
        defaultSpendingListShouldBeFound("total.notEquals=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllSpendingListsByTotalIsInShouldWork() throws Exception {
        // Initialize the database
        spendingListRepository.saveAndFlush(spendingList);

        // Get all the spendingListList where total in DEFAULT_TOTAL or UPDATED_TOTAL
        defaultSpendingListShouldBeFound("total.in=" + DEFAULT_TOTAL + "," + UPDATED_TOTAL);

        // Get all the spendingListList where total equals to UPDATED_TOTAL
        defaultSpendingListShouldNotBeFound("total.in=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllSpendingListsByTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        spendingListRepository.saveAndFlush(spendingList);

        // Get all the spendingListList where total is not null
        defaultSpendingListShouldBeFound("total.specified=true");

        // Get all the spendingListList where total is null
        defaultSpendingListShouldNotBeFound("total.specified=false");
    }

    @Test
    @Transactional
    void getAllSpendingListsByTotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        spendingListRepository.saveAndFlush(spendingList);

        // Get all the spendingListList where total is greater than or equal to DEFAULT_TOTAL
        defaultSpendingListShouldBeFound("total.greaterThanOrEqual=" + DEFAULT_TOTAL);

        // Get all the spendingListList where total is greater than or equal to UPDATED_TOTAL
        defaultSpendingListShouldNotBeFound("total.greaterThanOrEqual=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllSpendingListsByTotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        spendingListRepository.saveAndFlush(spendingList);

        // Get all the spendingListList where total is less than or equal to DEFAULT_TOTAL
        defaultSpendingListShouldBeFound("total.lessThanOrEqual=" + DEFAULT_TOTAL);

        // Get all the spendingListList where total is less than or equal to SMALLER_TOTAL
        defaultSpendingListShouldNotBeFound("total.lessThanOrEqual=" + SMALLER_TOTAL);
    }

    @Test
    @Transactional
    void getAllSpendingListsByTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        spendingListRepository.saveAndFlush(spendingList);

        // Get all the spendingListList where total is less than DEFAULT_TOTAL
        defaultSpendingListShouldNotBeFound("total.lessThan=" + DEFAULT_TOTAL);

        // Get all the spendingListList where total is less than UPDATED_TOTAL
        defaultSpendingListShouldBeFound("total.lessThan=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllSpendingListsByTotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        spendingListRepository.saveAndFlush(spendingList);

        // Get all the spendingListList where total is greater than DEFAULT_TOTAL
        defaultSpendingListShouldNotBeFound("total.greaterThan=" + DEFAULT_TOTAL);

        // Get all the spendingListList where total is greater than SMALLER_TOTAL
        defaultSpendingListShouldBeFound("total.greaterThan=" + SMALLER_TOTAL);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSpendingListShouldBeFound(String filter) throws Exception {
        restSpendingListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(spendingList.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].total").value(hasItem(DEFAULT_TOTAL.doubleValue())));

        // Check, that the count call also returns 1
        restSpendingListMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSpendingListShouldNotBeFound(String filter) throws Exception {
        restSpendingListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSpendingListMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSpendingList() throws Exception {
        // Get the spendingList
        restSpendingListMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSpendingList() throws Exception {
        // Initialize the database
        spendingListRepository.saveAndFlush(spendingList);

        int databaseSizeBeforeUpdate = spendingListRepository.findAll().size();

        // Update the spendingList
        SpendingList updatedSpendingList = spendingListRepository.findById(spendingList.getId()).get();
        // Disconnect from session so that the updates on updatedSpendingList are not directly saved in db
        em.detach(updatedSpendingList);
        updatedSpendingList.name(UPDATED_NAME).total(UPDATED_TOTAL);

        restSpendingListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSpendingList.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSpendingList))
            )
            .andExpect(status().isOk());

        // Validate the SpendingList in the database
        List<SpendingList> spendingListList = spendingListRepository.findAll();
        assertThat(spendingListList).hasSize(databaseSizeBeforeUpdate);
        SpendingList testSpendingList = spendingListList.get(spendingListList.size() - 1);
        assertThat(testSpendingList.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSpendingList.getTotal()).isEqualTo(UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void putNonExistingSpendingList() throws Exception {
        int databaseSizeBeforeUpdate = spendingListRepository.findAll().size();
        spendingList.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSpendingListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, spendingList.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(spendingList))
            )
            .andExpect(status().isBadRequest());

        // Validate the SpendingList in the database
        List<SpendingList> spendingListList = spendingListRepository.findAll();
        assertThat(spendingListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSpendingList() throws Exception {
        int databaseSizeBeforeUpdate = spendingListRepository.findAll().size();
        spendingList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpendingListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(spendingList))
            )
            .andExpect(status().isBadRequest());

        // Validate the SpendingList in the database
        List<SpendingList> spendingListList = spendingListRepository.findAll();
        assertThat(spendingListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSpendingList() throws Exception {
        int databaseSizeBeforeUpdate = spendingListRepository.findAll().size();
        spendingList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpendingListMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(spendingList)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SpendingList in the database
        List<SpendingList> spendingListList = spendingListRepository.findAll();
        assertThat(spendingListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSpendingListWithPatch() throws Exception {
        // Initialize the database
        spendingListRepository.saveAndFlush(spendingList);

        int databaseSizeBeforeUpdate = spendingListRepository.findAll().size();

        // Update the spendingList using partial update
        SpendingList partialUpdatedSpendingList = new SpendingList();
        partialUpdatedSpendingList.setId(spendingList.getId());

        restSpendingListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSpendingList.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSpendingList))
            )
            .andExpect(status().isOk());

        // Validate the SpendingList in the database
        List<SpendingList> spendingListList = spendingListRepository.findAll();
        assertThat(spendingListList).hasSize(databaseSizeBeforeUpdate);
        SpendingList testSpendingList = spendingListList.get(spendingListList.size() - 1);
        assertThat(testSpendingList.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSpendingList.getTotal()).isEqualTo(DEFAULT_TOTAL);
    }

    @Test
    @Transactional
    void fullUpdateSpendingListWithPatch() throws Exception {
        // Initialize the database
        spendingListRepository.saveAndFlush(spendingList);

        int databaseSizeBeforeUpdate = spendingListRepository.findAll().size();

        // Update the spendingList using partial update
        SpendingList partialUpdatedSpendingList = new SpendingList();
        partialUpdatedSpendingList.setId(spendingList.getId());

        partialUpdatedSpendingList.name(UPDATED_NAME).total(UPDATED_TOTAL);

        restSpendingListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSpendingList.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSpendingList))
            )
            .andExpect(status().isOk());

        // Validate the SpendingList in the database
        List<SpendingList> spendingListList = spendingListRepository.findAll();
        assertThat(spendingListList).hasSize(databaseSizeBeforeUpdate);
        SpendingList testSpendingList = spendingListList.get(spendingListList.size() - 1);
        assertThat(testSpendingList.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSpendingList.getTotal()).isEqualTo(UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void patchNonExistingSpendingList() throws Exception {
        int databaseSizeBeforeUpdate = spendingListRepository.findAll().size();
        spendingList.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSpendingListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, spendingList.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(spendingList))
            )
            .andExpect(status().isBadRequest());

        // Validate the SpendingList in the database
        List<SpendingList> spendingListList = spendingListRepository.findAll();
        assertThat(spendingListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSpendingList() throws Exception {
        int databaseSizeBeforeUpdate = spendingListRepository.findAll().size();
        spendingList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpendingListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(spendingList))
            )
            .andExpect(status().isBadRequest());

        // Validate the SpendingList in the database
        List<SpendingList> spendingListList = spendingListRepository.findAll();
        assertThat(spendingListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSpendingList() throws Exception {
        int databaseSizeBeforeUpdate = spendingListRepository.findAll().size();
        spendingList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpendingListMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(spendingList))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SpendingList in the database
        List<SpendingList> spendingListList = spendingListRepository.findAll();
        assertThat(spendingListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSpendingList() throws Exception {
        // Initialize the database
        spendingListRepository.saveAndFlush(spendingList);

        int databaseSizeBeforeDelete = spendingListRepository.findAll().size();

        // Delete the spendingList
        restSpendingListMockMvc
            .perform(delete(ENTITY_API_URL_ID, spendingList.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SpendingList> spendingListList = spendingListRepository.findAll();
        assertThat(spendingListList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
