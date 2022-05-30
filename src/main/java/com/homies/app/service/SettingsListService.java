package com.homies.app.service;

import com.homies.app.domain.SettingsList;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link SettingsList}.
 */
public interface SettingsListService {
    /**
     * Save a settingsList.
     *
     * @param settingsList the entity to save.
     * @return the persisted entity.
     */
    SettingsList save(SettingsList settingsList);

    /**
     * Partially updates a settingsList.
     *
     * @param settingsList the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SettingsList> partialUpdate(SettingsList settingsList);

    /**
     * Get all the settingsLists.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SettingsList> findAll(Pageable pageable);

    /**
     * Get the "id" settingsList.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SettingsList> findOne(Long id);

    /**
     * Delete the "id" settingsList.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
