package com.homies.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.homies.app.IntegrationTest;
import com.homies.app.domain.ShoppingList;
import com.homies.app.repository.ShoppingListRepository;
import com.homies.app.service.criteria.ShoppingListCriteria;
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
 * Integration tests for the {@link ShoppingListResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ShoppingListResourceIT {

    private static final Float DEFAULT_TOTAL = 1F;
    private static final Float UPDATED_TOTAL = 2F;
    private static final Float SMALLER_TOTAL = 1F - 1F;

    private static final String DEFAULT_NAME_SHOP_LIST = "AAAAAAAAAA";
    private static final String UPDATED_NAME_SHOP_LIST = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/shopping-lists";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restShoppingListMockMvc;

    private ShoppingList shoppingList;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShoppingList createEntity(EntityManager em) {
        ShoppingList shoppingList = new ShoppingList().total(DEFAULT_TOTAL).nameShopList(DEFAULT_NAME_SHOP_LIST);
        return shoppingList;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShoppingList createUpdatedEntity(EntityManager em) {
        ShoppingList shoppingList = new ShoppingList().total(UPDATED_TOTAL).nameShopList(UPDATED_NAME_SHOP_LIST);
        return shoppingList;
    }

    @BeforeEach
    public void initTest() {
        shoppingList = createEntity(em);
    }

    @Test
    @Transactional
    void createShoppingList() throws Exception {
        int databaseSizeBeforeCreate = shoppingListRepository.findAll().size();
        // Create the ShoppingList
        restShoppingListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shoppingList)))
            .andExpect(status().isCreated());

        // Validate the ShoppingList in the database
        List<ShoppingList> shoppingListList = shoppingListRepository.findAll();
        assertThat(shoppingListList).hasSize(databaseSizeBeforeCreate + 1);
        ShoppingList testShoppingList = shoppingListList.get(shoppingListList.size() - 1);
        assertThat(testShoppingList.getTotal()).isEqualTo(DEFAULT_TOTAL);
        assertThat(testShoppingList.getNameShopList()).isEqualTo(DEFAULT_NAME_SHOP_LIST);
    }

    @Test
    @Transactional
    void createShoppingListWithExistingId() throws Exception {
        // Create the ShoppingList with an existing ID
        shoppingList.setId(1L);

        int databaseSizeBeforeCreate = shoppingListRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restShoppingListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shoppingList)))
            .andExpect(status().isBadRequest());

        // Validate the ShoppingList in the database
        List<ShoppingList> shoppingListList = shoppingListRepository.findAll();
        assertThat(shoppingListList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameShopListIsRequired() throws Exception {
        int databaseSizeBeforeTest = shoppingListRepository.findAll().size();
        // set the field null
        shoppingList.setNameShopList(null);

        // Create the ShoppingList, which fails.

        restShoppingListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shoppingList)))
            .andExpect(status().isBadRequest());

        List<ShoppingList> shoppingListList = shoppingListRepository.findAll();
        assertThat(shoppingListList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllShoppingLists() throws Exception {
        // Initialize the database
        shoppingListRepository.saveAndFlush(shoppingList);

        // Get all the shoppingListList
        restShoppingListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shoppingList.getId().intValue())))
            .andExpect(jsonPath("$.[*].total").value(hasItem(DEFAULT_TOTAL.doubleValue())))
            .andExpect(jsonPath("$.[*].nameShopList").value(hasItem(DEFAULT_NAME_SHOP_LIST)));
    }

    @Test
    @Transactional
    void getShoppingList() throws Exception {
        // Initialize the database
        shoppingListRepository.saveAndFlush(shoppingList);

        // Get the shoppingList
        restShoppingListMockMvc
            .perform(get(ENTITY_API_URL_ID, shoppingList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(shoppingList.getId().intValue()))
            .andExpect(jsonPath("$.total").value(DEFAULT_TOTAL.doubleValue()))
            .andExpect(jsonPath("$.nameShopList").value(DEFAULT_NAME_SHOP_LIST));
    }

    @Test
    @Transactional
    void getShoppingListsByIdFiltering() throws Exception {
        // Initialize the database
        shoppingListRepository.saveAndFlush(shoppingList);

        Long id = shoppingList.getId();

        defaultShoppingListShouldBeFound("id.equals=" + id);
        defaultShoppingListShouldNotBeFound("id.notEquals=" + id);

        defaultShoppingListShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultShoppingListShouldNotBeFound("id.greaterThan=" + id);

        defaultShoppingListShouldBeFound("id.lessThanOrEqual=" + id);
        defaultShoppingListShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllShoppingListsByTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        shoppingListRepository.saveAndFlush(shoppingList);

        // Get all the shoppingListList where total equals to DEFAULT_TOTAL
        defaultShoppingListShouldBeFound("total.equals=" + DEFAULT_TOTAL);

        // Get all the shoppingListList where total equals to UPDATED_TOTAL
        defaultShoppingListShouldNotBeFound("total.equals=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllShoppingListsByTotalIsNotEqualToSomething() throws Exception {
        // Initialize the database
        shoppingListRepository.saveAndFlush(shoppingList);

        // Get all the shoppingListList where total not equals to DEFAULT_TOTAL
        defaultShoppingListShouldNotBeFound("total.notEquals=" + DEFAULT_TOTAL);

        // Get all the shoppingListList where total not equals to UPDATED_TOTAL
        defaultShoppingListShouldBeFound("total.notEquals=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllShoppingListsByTotalIsInShouldWork() throws Exception {
        // Initialize the database
        shoppingListRepository.saveAndFlush(shoppingList);

        // Get all the shoppingListList where total in DEFAULT_TOTAL or UPDATED_TOTAL
        defaultShoppingListShouldBeFound("total.in=" + DEFAULT_TOTAL + "," + UPDATED_TOTAL);

        // Get all the shoppingListList where total equals to UPDATED_TOTAL
        defaultShoppingListShouldNotBeFound("total.in=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllShoppingListsByTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        shoppingListRepository.saveAndFlush(shoppingList);

        // Get all the shoppingListList where total is not null
        defaultShoppingListShouldBeFound("total.specified=true");

        // Get all the shoppingListList where total is null
        defaultShoppingListShouldNotBeFound("total.specified=false");
    }

    @Test
    @Transactional
    void getAllShoppingListsByTotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        shoppingListRepository.saveAndFlush(shoppingList);

        // Get all the shoppingListList where total is greater than or equal to DEFAULT_TOTAL
        defaultShoppingListShouldBeFound("total.greaterThanOrEqual=" + DEFAULT_TOTAL);

        // Get all the shoppingListList where total is greater than or equal to UPDATED_TOTAL
        defaultShoppingListShouldNotBeFound("total.greaterThanOrEqual=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllShoppingListsByTotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        shoppingListRepository.saveAndFlush(shoppingList);

        // Get all the shoppingListList where total is less than or equal to DEFAULT_TOTAL
        defaultShoppingListShouldBeFound("total.lessThanOrEqual=" + DEFAULT_TOTAL);

        // Get all the shoppingListList where total is less than or equal to SMALLER_TOTAL
        defaultShoppingListShouldNotBeFound("total.lessThanOrEqual=" + SMALLER_TOTAL);
    }

    @Test
    @Transactional
    void getAllShoppingListsByTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        shoppingListRepository.saveAndFlush(shoppingList);

        // Get all the shoppingListList where total is less than DEFAULT_TOTAL
        defaultShoppingListShouldNotBeFound("total.lessThan=" + DEFAULT_TOTAL);

        // Get all the shoppingListList where total is less than UPDATED_TOTAL
        defaultShoppingListShouldBeFound("total.lessThan=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllShoppingListsByTotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        shoppingListRepository.saveAndFlush(shoppingList);

        // Get all the shoppingListList where total is greater than DEFAULT_TOTAL
        defaultShoppingListShouldNotBeFound("total.greaterThan=" + DEFAULT_TOTAL);

        // Get all the shoppingListList where total is greater than SMALLER_TOTAL
        defaultShoppingListShouldBeFound("total.greaterThan=" + SMALLER_TOTAL);
    }

    @Test
    @Transactional
    void getAllShoppingListsByNameShopListIsEqualToSomething() throws Exception {
        // Initialize the database
        shoppingListRepository.saveAndFlush(shoppingList);

        // Get all the shoppingListList where nameShopList equals to DEFAULT_NAME_SHOP_LIST
        defaultShoppingListShouldBeFound("nameShopList.equals=" + DEFAULT_NAME_SHOP_LIST);

        // Get all the shoppingListList where nameShopList equals to UPDATED_NAME_SHOP_LIST
        defaultShoppingListShouldNotBeFound("nameShopList.equals=" + UPDATED_NAME_SHOP_LIST);
    }

    @Test
    @Transactional
    void getAllShoppingListsByNameShopListIsNotEqualToSomething() throws Exception {
        // Initialize the database
        shoppingListRepository.saveAndFlush(shoppingList);

        // Get all the shoppingListList where nameShopList not equals to DEFAULT_NAME_SHOP_LIST
        defaultShoppingListShouldNotBeFound("nameShopList.notEquals=" + DEFAULT_NAME_SHOP_LIST);

        // Get all the shoppingListList where nameShopList not equals to UPDATED_NAME_SHOP_LIST
        defaultShoppingListShouldBeFound("nameShopList.notEquals=" + UPDATED_NAME_SHOP_LIST);
    }

    @Test
    @Transactional
    void getAllShoppingListsByNameShopListIsInShouldWork() throws Exception {
        // Initialize the database
        shoppingListRepository.saveAndFlush(shoppingList);

        // Get all the shoppingListList where nameShopList in DEFAULT_NAME_SHOP_LIST or UPDATED_NAME_SHOP_LIST
        defaultShoppingListShouldBeFound("nameShopList.in=" + DEFAULT_NAME_SHOP_LIST + "," + UPDATED_NAME_SHOP_LIST);

        // Get all the shoppingListList where nameShopList equals to UPDATED_NAME_SHOP_LIST
        defaultShoppingListShouldNotBeFound("nameShopList.in=" + UPDATED_NAME_SHOP_LIST);
    }

    @Test
    @Transactional
    void getAllShoppingListsByNameShopListIsNullOrNotNull() throws Exception {
        // Initialize the database
        shoppingListRepository.saveAndFlush(shoppingList);

        // Get all the shoppingListList where nameShopList is not null
        defaultShoppingListShouldBeFound("nameShopList.specified=true");

        // Get all the shoppingListList where nameShopList is null
        defaultShoppingListShouldNotBeFound("nameShopList.specified=false");
    }

    @Test
    @Transactional
    void getAllShoppingListsByNameShopListContainsSomething() throws Exception {
        // Initialize the database
        shoppingListRepository.saveAndFlush(shoppingList);

        // Get all the shoppingListList where nameShopList contains DEFAULT_NAME_SHOP_LIST
        defaultShoppingListShouldBeFound("nameShopList.contains=" + DEFAULT_NAME_SHOP_LIST);

        // Get all the shoppingListList where nameShopList contains UPDATED_NAME_SHOP_LIST
        defaultShoppingListShouldNotBeFound("nameShopList.contains=" + UPDATED_NAME_SHOP_LIST);
    }

    @Test
    @Transactional
    void getAllShoppingListsByNameShopListNotContainsSomething() throws Exception {
        // Initialize the database
        shoppingListRepository.saveAndFlush(shoppingList);

        // Get all the shoppingListList where nameShopList does not contain DEFAULT_NAME_SHOP_LIST
        defaultShoppingListShouldNotBeFound("nameShopList.doesNotContain=" + DEFAULT_NAME_SHOP_LIST);

        // Get all the shoppingListList where nameShopList does not contain UPDATED_NAME_SHOP_LIST
        defaultShoppingListShouldBeFound("nameShopList.doesNotContain=" + UPDATED_NAME_SHOP_LIST);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultShoppingListShouldBeFound(String filter) throws Exception {
        restShoppingListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shoppingList.getId().intValue())))
            .andExpect(jsonPath("$.[*].total").value(hasItem(DEFAULT_TOTAL.doubleValue())))
            .andExpect(jsonPath("$.[*].nameShopList").value(hasItem(DEFAULT_NAME_SHOP_LIST)));

        // Check, that the count call also returns 1
        restShoppingListMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultShoppingListShouldNotBeFound(String filter) throws Exception {
        restShoppingListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restShoppingListMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingShoppingList() throws Exception {
        // Get the shoppingList
        restShoppingListMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewShoppingList() throws Exception {
        // Initialize the database
        shoppingListRepository.saveAndFlush(shoppingList);

        int databaseSizeBeforeUpdate = shoppingListRepository.findAll().size();

        // Update the shoppingList
        ShoppingList updatedShoppingList = shoppingListRepository.findById(shoppingList.getId()).get();
        // Disconnect from session so that the updates on updatedShoppingList are not directly saved in db
        em.detach(updatedShoppingList);
        updatedShoppingList.total(UPDATED_TOTAL).nameShopList(UPDATED_NAME_SHOP_LIST);

        restShoppingListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedShoppingList.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedShoppingList))
            )
            .andExpect(status().isOk());

        // Validate the ShoppingList in the database
        List<ShoppingList> shoppingListList = shoppingListRepository.findAll();
        assertThat(shoppingListList).hasSize(databaseSizeBeforeUpdate);
        ShoppingList testShoppingList = shoppingListList.get(shoppingListList.size() - 1);
        assertThat(testShoppingList.getTotal()).isEqualTo(UPDATED_TOTAL);
        assertThat(testShoppingList.getNameShopList()).isEqualTo(UPDATED_NAME_SHOP_LIST);
    }

    @Test
    @Transactional
    void putNonExistingShoppingList() throws Exception {
        int databaseSizeBeforeUpdate = shoppingListRepository.findAll().size();
        shoppingList.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShoppingListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shoppingList.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(shoppingList))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShoppingList in the database
        List<ShoppingList> shoppingListList = shoppingListRepository.findAll();
        assertThat(shoppingListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchShoppingList() throws Exception {
        int databaseSizeBeforeUpdate = shoppingListRepository.findAll().size();
        shoppingList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoppingListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(shoppingList))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShoppingList in the database
        List<ShoppingList> shoppingListList = shoppingListRepository.findAll();
        assertThat(shoppingListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamShoppingList() throws Exception {
        int databaseSizeBeforeUpdate = shoppingListRepository.findAll().size();
        shoppingList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoppingListMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shoppingList)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShoppingList in the database
        List<ShoppingList> shoppingListList = shoppingListRepository.findAll();
        assertThat(shoppingListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateShoppingListWithPatch() throws Exception {
        // Initialize the database
        shoppingListRepository.saveAndFlush(shoppingList);

        int databaseSizeBeforeUpdate = shoppingListRepository.findAll().size();

        // Update the shoppingList using partial update
        ShoppingList partialUpdatedShoppingList = new ShoppingList();
        partialUpdatedShoppingList.setId(shoppingList.getId());

        partialUpdatedShoppingList.total(UPDATED_TOTAL);

        restShoppingListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShoppingList.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedShoppingList))
            )
            .andExpect(status().isOk());

        // Validate the ShoppingList in the database
        List<ShoppingList> shoppingListList = shoppingListRepository.findAll();
        assertThat(shoppingListList).hasSize(databaseSizeBeforeUpdate);
        ShoppingList testShoppingList = shoppingListList.get(shoppingListList.size() - 1);
        assertThat(testShoppingList.getTotal()).isEqualTo(UPDATED_TOTAL);
        assertThat(testShoppingList.getNameShopList()).isEqualTo(DEFAULT_NAME_SHOP_LIST);
    }

    @Test
    @Transactional
    void fullUpdateShoppingListWithPatch() throws Exception {
        // Initialize the database
        shoppingListRepository.saveAndFlush(shoppingList);

        int databaseSizeBeforeUpdate = shoppingListRepository.findAll().size();

        // Update the shoppingList using partial update
        ShoppingList partialUpdatedShoppingList = new ShoppingList();
        partialUpdatedShoppingList.setId(shoppingList.getId());

        partialUpdatedShoppingList.total(UPDATED_TOTAL).nameShopList(UPDATED_NAME_SHOP_LIST);

        restShoppingListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShoppingList.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedShoppingList))
            )
            .andExpect(status().isOk());

        // Validate the ShoppingList in the database
        List<ShoppingList> shoppingListList = shoppingListRepository.findAll();
        assertThat(shoppingListList).hasSize(databaseSizeBeforeUpdate);
        ShoppingList testShoppingList = shoppingListList.get(shoppingListList.size() - 1);
        assertThat(testShoppingList.getTotal()).isEqualTo(UPDATED_TOTAL);
        assertThat(testShoppingList.getNameShopList()).isEqualTo(UPDATED_NAME_SHOP_LIST);
    }

    @Test
    @Transactional
    void patchNonExistingShoppingList() throws Exception {
        int databaseSizeBeforeUpdate = shoppingListRepository.findAll().size();
        shoppingList.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShoppingListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, shoppingList.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(shoppingList))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShoppingList in the database
        List<ShoppingList> shoppingListList = shoppingListRepository.findAll();
        assertThat(shoppingListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchShoppingList() throws Exception {
        int databaseSizeBeforeUpdate = shoppingListRepository.findAll().size();
        shoppingList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoppingListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(shoppingList))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShoppingList in the database
        List<ShoppingList> shoppingListList = shoppingListRepository.findAll();
        assertThat(shoppingListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamShoppingList() throws Exception {
        int databaseSizeBeforeUpdate = shoppingListRepository.findAll().size();
        shoppingList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoppingListMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(shoppingList))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShoppingList in the database
        List<ShoppingList> shoppingListList = shoppingListRepository.findAll();
        assertThat(shoppingListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteShoppingList() throws Exception {
        // Initialize the database
        shoppingListRepository.saveAndFlush(shoppingList);

        int databaseSizeBeforeDelete = shoppingListRepository.findAll().size();

        // Delete the shoppingList
        restShoppingListMockMvc
            .perform(delete(ENTITY_API_URL_ID, shoppingList.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ShoppingList> shoppingListList = shoppingListRepository.findAll();
        assertThat(shoppingListList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
