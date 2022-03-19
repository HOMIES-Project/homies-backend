package com.homies.app.repository;

import com.homies.app.domain.SettingsList;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the SettingsList entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SettingsListRepository extends JpaRepository<SettingsList, Long>, JpaSpecificationExecutor<SettingsList> {}
