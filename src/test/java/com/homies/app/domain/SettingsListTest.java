package com.homies.app.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.homies.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SettingsListTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SettingsList.class);
        SettingsList settingsList1 = new SettingsList();
        settingsList1.setId(1L);
        SettingsList settingsList2 = new SettingsList();
        settingsList2.setId(settingsList1.getId());
        assertThat(settingsList1).isEqualTo(settingsList2);
        settingsList2.setId(2L);
        assertThat(settingsList1).isNotEqualTo(settingsList2);
        settingsList1.setId(null);
        assertThat(settingsList1).isNotEqualTo(settingsList2);
    }
}
