package com.homies.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.homies.app.IntegrationTest;
import com.homies.app.domain.UserPending;
import com.homies.app.repository.UserPendingRepository;
import com.homies.app.service.criteria.UserPendingCriteria;
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
 * Integration tests for the {@link UserPendingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserPendingResourceIT {

    private static final Float DEFAULT_PENDING = 0F;
    private static final Float UPDATED_PENDING = 1F;
    private static final Float SMALLER_PENDING = 0F - 1F;

    private static final Boolean DEFAULT_PAID = false;
    private static final Boolean UPDATED_PAID = true;

    private static final String ENTITY_API_URL = "/api/user-pendings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserPendingRepository userPendingRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserPendingMockMvc;

    private UserPending userPending;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserPending createEntity(EntityManager em) {
        UserPending userPending = new UserPending().pending(DEFAULT_PENDING).paid(DEFAULT_PAID);
        return userPending;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserPending createUpdatedEntity(EntityManager em) {
        UserPending userPending = new UserPending().pending(UPDATED_PENDING).paid(UPDATED_PAID);
        return userPending;
    }

    @BeforeEach
    public void initTest() {
        userPending = createEntity(em);
    }

    @Test
    @Transactional
    void createUserPending() throws Exception {
        int databaseSizeBeforeCreate = userPendingRepository.findAll().size();
        // Create the UserPending
        restUserPendingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userPending)))
            .andExpect(status().isCreated());

        // Validate the UserPending in the database
        List<UserPending> userPendingList = userPendingRepository.findAll();
        assertThat(userPendingList).hasSize(databaseSizeBeforeCreate + 1);
        UserPending testUserPending = userPendingList.get(userPendingList.size() - 1);
        assertThat(testUserPending.getPending()).isEqualTo(DEFAULT_PENDING);
        assertThat(testUserPending.getPaid()).isEqualTo(DEFAULT_PAID);
    }

    @Test
    @Transactional
    void createUserPendingWithExistingId() throws Exception {
        // Create the UserPending with an existing ID
        userPending.setId(1L);

        int databaseSizeBeforeCreate = userPendingRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserPendingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userPending)))
            .andExpect(status().isBadRequest());

        // Validate the UserPending in the database
        List<UserPending> userPendingList = userPendingRepository.findAll();
        assertThat(userPendingList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllUserPendings() throws Exception {
        // Initialize the database
        userPendingRepository.saveAndFlush(userPending);

        // Get all the userPendingList
        restUserPendingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userPending.getId().intValue())))
            .andExpect(jsonPath("$.[*].pending").value(hasItem(DEFAULT_PENDING.doubleValue())))
            .andExpect(jsonPath("$.[*].paid").value(hasItem(DEFAULT_PAID.booleanValue())));
    }

    @Test
    @Transactional
    void getUserPending() throws Exception {
        // Initialize the database
        userPendingRepository.saveAndFlush(userPending);

        // Get the userPending
        restUserPendingMockMvc
            .perform(get(ENTITY_API_URL_ID, userPending.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userPending.getId().intValue()))
            .andExpect(jsonPath("$.pending").value(DEFAULT_PENDING.doubleValue()))
            .andExpect(jsonPath("$.paid").value(DEFAULT_PAID.booleanValue()));
    }

    @Test
    @Transactional
    void getUserPendingsByIdFiltering() throws Exception {
        // Initialize the database
        userPendingRepository.saveAndFlush(userPending);

        Long id = userPending.getId();

        defaultUserPendingShouldBeFound("id.equals=" + id);
        defaultUserPendingShouldNotBeFound("id.notEquals=" + id);

        defaultUserPendingShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultUserPendingShouldNotBeFound("id.greaterThan=" + id);

        defaultUserPendingShouldBeFound("id.lessThanOrEqual=" + id);
        defaultUserPendingShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllUserPendingsByPendingIsEqualToSomething() throws Exception {
        // Initialize the database
        userPendingRepository.saveAndFlush(userPending);

        // Get all the userPendingList where pending equals to DEFAULT_PENDING
        defaultUserPendingShouldBeFound("pending.equals=" + DEFAULT_PENDING);

        // Get all the userPendingList where pending equals to UPDATED_PENDING
        defaultUserPendingShouldNotBeFound("pending.equals=" + UPDATED_PENDING);
    }

    @Test
    @Transactional
    void getAllUserPendingsByPendingIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userPendingRepository.saveAndFlush(userPending);

        // Get all the userPendingList where pending not equals to DEFAULT_PENDING
        defaultUserPendingShouldNotBeFound("pending.notEquals=" + DEFAULT_PENDING);

        // Get all the userPendingList where pending not equals to UPDATED_PENDING
        defaultUserPendingShouldBeFound("pending.notEquals=" + UPDATED_PENDING);
    }

    @Test
    @Transactional
    void getAllUserPendingsByPendingIsInShouldWork() throws Exception {
        // Initialize the database
        userPendingRepository.saveAndFlush(userPending);

        // Get all the userPendingList where pending in DEFAULT_PENDING or UPDATED_PENDING
        defaultUserPendingShouldBeFound("pending.in=" + DEFAULT_PENDING + "," + UPDATED_PENDING);

        // Get all the userPendingList where pending equals to UPDATED_PENDING
        defaultUserPendingShouldNotBeFound("pending.in=" + UPDATED_PENDING);
    }

    @Test
    @Transactional
    void getAllUserPendingsByPendingIsNullOrNotNull() throws Exception {
        // Initialize the database
        userPendingRepository.saveAndFlush(userPending);

        // Get all the userPendingList where pending is not null
        defaultUserPendingShouldBeFound("pending.specified=true");

        // Get all the userPendingList where pending is null
        defaultUserPendingShouldNotBeFound("pending.specified=false");
    }

    @Test
    @Transactional
    void getAllUserPendingsByPendingIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userPendingRepository.saveAndFlush(userPending);

        // Get all the userPendingList where pending is greater than or equal to DEFAULT_PENDING
        defaultUserPendingShouldBeFound("pending.greaterThanOrEqual=" + DEFAULT_PENDING);

        // Get all the userPendingList where pending is greater than or equal to UPDATED_PENDING
        defaultUserPendingShouldNotBeFound("pending.greaterThanOrEqual=" + UPDATED_PENDING);
    }

    @Test
    @Transactional
    void getAllUserPendingsByPendingIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userPendingRepository.saveAndFlush(userPending);

        // Get all the userPendingList where pending is less than or equal to DEFAULT_PENDING
        defaultUserPendingShouldBeFound("pending.lessThanOrEqual=" + DEFAULT_PENDING);

        // Get all the userPendingList where pending is less than or equal to SMALLER_PENDING
        defaultUserPendingShouldNotBeFound("pending.lessThanOrEqual=" + SMALLER_PENDING);
    }

    @Test
    @Transactional
    void getAllUserPendingsByPendingIsLessThanSomething() throws Exception {
        // Initialize the database
        userPendingRepository.saveAndFlush(userPending);

        // Get all the userPendingList where pending is less than DEFAULT_PENDING
        defaultUserPendingShouldNotBeFound("pending.lessThan=" + DEFAULT_PENDING);

        // Get all the userPendingList where pending is less than UPDATED_PENDING
        defaultUserPendingShouldBeFound("pending.lessThan=" + UPDATED_PENDING);
    }

    @Test
    @Transactional
    void getAllUserPendingsByPendingIsGreaterThanSomething() throws Exception {
        // Initialize the database
        userPendingRepository.saveAndFlush(userPending);

        // Get all the userPendingList where pending is greater than DEFAULT_PENDING
        defaultUserPendingShouldNotBeFound("pending.greaterThan=" + DEFAULT_PENDING);

        // Get all the userPendingList where pending is greater than SMALLER_PENDING
        defaultUserPendingShouldBeFound("pending.greaterThan=" + SMALLER_PENDING);
    }

    @Test
    @Transactional
    void getAllUserPendingsByPaidIsEqualToSomething() throws Exception {
        // Initialize the database
        userPendingRepository.saveAndFlush(userPending);

        // Get all the userPendingList where paid equals to DEFAULT_PAID
        defaultUserPendingShouldBeFound("paid.equals=" + DEFAULT_PAID);

        // Get all the userPendingList where paid equals to UPDATED_PAID
        defaultUserPendingShouldNotBeFound("paid.equals=" + UPDATED_PAID);
    }

    @Test
    @Transactional
    void getAllUserPendingsByPaidIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userPendingRepository.saveAndFlush(userPending);

        // Get all the userPendingList where paid not equals to DEFAULT_PAID
        defaultUserPendingShouldNotBeFound("paid.notEquals=" + DEFAULT_PAID);

        // Get all the userPendingList where paid not equals to UPDATED_PAID
        defaultUserPendingShouldBeFound("paid.notEquals=" + UPDATED_PAID);
    }

    @Test
    @Transactional
    void getAllUserPendingsByPaidIsInShouldWork() throws Exception {
        // Initialize the database
        userPendingRepository.saveAndFlush(userPending);

        // Get all the userPendingList where paid in DEFAULT_PAID or UPDATED_PAID
        defaultUserPendingShouldBeFound("paid.in=" + DEFAULT_PAID + "," + UPDATED_PAID);

        // Get all the userPendingList where paid equals to UPDATED_PAID
        defaultUserPendingShouldNotBeFound("paid.in=" + UPDATED_PAID);
    }

    @Test
    @Transactional
    void getAllUserPendingsByPaidIsNullOrNotNull() throws Exception {
        // Initialize the database
        userPendingRepository.saveAndFlush(userPending);

        // Get all the userPendingList where paid is not null
        defaultUserPendingShouldBeFound("paid.specified=true");

        // Get all the userPendingList where paid is null
        defaultUserPendingShouldNotBeFound("paid.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultUserPendingShouldBeFound(String filter) throws Exception {
        restUserPendingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userPending.getId().intValue())))
            .andExpect(jsonPath("$.[*].pending").value(hasItem(DEFAULT_PENDING.doubleValue())))
            .andExpect(jsonPath("$.[*].paid").value(hasItem(DEFAULT_PAID.booleanValue())));

        // Check, that the count call also returns 1
        restUserPendingMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultUserPendingShouldNotBeFound(String filter) throws Exception {
        restUserPendingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUserPendingMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingUserPending() throws Exception {
        // Get the userPending
        restUserPendingMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewUserPending() throws Exception {
        // Initialize the database
        userPendingRepository.saveAndFlush(userPending);

        int databaseSizeBeforeUpdate = userPendingRepository.findAll().size();

        // Update the userPending
        UserPending updatedUserPending = userPendingRepository.findById(userPending.getId()).get();
        // Disconnect from session so that the updates on updatedUserPending are not directly saved in db
        em.detach(updatedUserPending);
        updatedUserPending.pending(UPDATED_PENDING).paid(UPDATED_PAID);

        restUserPendingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUserPending.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedUserPending))
            )
            .andExpect(status().isOk());

        // Validate the UserPending in the database
        List<UserPending> userPendingList = userPendingRepository.findAll();
        assertThat(userPendingList).hasSize(databaseSizeBeforeUpdate);
        UserPending testUserPending = userPendingList.get(userPendingList.size() - 1);
        assertThat(testUserPending.getPending()).isEqualTo(UPDATED_PENDING);
        assertThat(testUserPending.getPaid()).isEqualTo(UPDATED_PAID);
    }

    @Test
    @Transactional
    void putNonExistingUserPending() throws Exception {
        int databaseSizeBeforeUpdate = userPendingRepository.findAll().size();
        userPending.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserPendingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userPending.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userPending))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserPending in the database
        List<UserPending> userPendingList = userPendingRepository.findAll();
        assertThat(userPendingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserPending() throws Exception {
        int databaseSizeBeforeUpdate = userPendingRepository.findAll().size();
        userPending.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserPendingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userPending))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserPending in the database
        List<UserPending> userPendingList = userPendingRepository.findAll();
        assertThat(userPendingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserPending() throws Exception {
        int databaseSizeBeforeUpdate = userPendingRepository.findAll().size();
        userPending.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserPendingMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userPending)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserPending in the database
        List<UserPending> userPendingList = userPendingRepository.findAll();
        assertThat(userPendingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserPendingWithPatch() throws Exception {
        // Initialize the database
        userPendingRepository.saveAndFlush(userPending);

        int databaseSizeBeforeUpdate = userPendingRepository.findAll().size();

        // Update the userPending using partial update
        UserPending partialUpdatedUserPending = new UserPending();
        partialUpdatedUserPending.setId(userPending.getId());

        partialUpdatedUserPending.paid(UPDATED_PAID);

        restUserPendingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserPending.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserPending))
            )
            .andExpect(status().isOk());

        // Validate the UserPending in the database
        List<UserPending> userPendingList = userPendingRepository.findAll();
        assertThat(userPendingList).hasSize(databaseSizeBeforeUpdate);
        UserPending testUserPending = userPendingList.get(userPendingList.size() - 1);
        assertThat(testUserPending.getPending()).isEqualTo(DEFAULT_PENDING);
        assertThat(testUserPending.getPaid()).isEqualTo(UPDATED_PAID);
    }

    @Test
    @Transactional
    void fullUpdateUserPendingWithPatch() throws Exception {
        // Initialize the database
        userPendingRepository.saveAndFlush(userPending);

        int databaseSizeBeforeUpdate = userPendingRepository.findAll().size();

        // Update the userPending using partial update
        UserPending partialUpdatedUserPending = new UserPending();
        partialUpdatedUserPending.setId(userPending.getId());

        partialUpdatedUserPending.pending(UPDATED_PENDING).paid(UPDATED_PAID);

        restUserPendingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserPending.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserPending))
            )
            .andExpect(status().isOk());

        // Validate the UserPending in the database
        List<UserPending> userPendingList = userPendingRepository.findAll();
        assertThat(userPendingList).hasSize(databaseSizeBeforeUpdate);
        UserPending testUserPending = userPendingList.get(userPendingList.size() - 1);
        assertThat(testUserPending.getPending()).isEqualTo(UPDATED_PENDING);
        assertThat(testUserPending.getPaid()).isEqualTo(UPDATED_PAID);
    }

    @Test
    @Transactional
    void patchNonExistingUserPending() throws Exception {
        int databaseSizeBeforeUpdate = userPendingRepository.findAll().size();
        userPending.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserPendingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userPending.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userPending))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserPending in the database
        List<UserPending> userPendingList = userPendingRepository.findAll();
        assertThat(userPendingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserPending() throws Exception {
        int databaseSizeBeforeUpdate = userPendingRepository.findAll().size();
        userPending.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserPendingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userPending))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserPending in the database
        List<UserPending> userPendingList = userPendingRepository.findAll();
        assertThat(userPendingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserPending() throws Exception {
        int databaseSizeBeforeUpdate = userPendingRepository.findAll().size();
        userPending.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserPendingMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(userPending))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserPending in the database
        List<UserPending> userPendingList = userPendingRepository.findAll();
        assertThat(userPendingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserPending() throws Exception {
        // Initialize the database
        userPendingRepository.saveAndFlush(userPending);

        int databaseSizeBeforeDelete = userPendingRepository.findAll().size();

        // Delete the userPending
        restUserPendingMockMvc
            .perform(delete(ENTITY_API_URL_ID, userPending.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserPending> userPendingList = userPendingRepository.findAll();
        assertThat(userPendingList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
