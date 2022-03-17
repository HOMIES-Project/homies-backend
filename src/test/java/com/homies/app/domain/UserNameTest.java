package com.homies.app.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.homies.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserNameTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserName.class);
        UserName userName1 = new UserName();
        userName1.setId(1L);
        UserName userName2 = new UserName();
        userName2.setId(userName1.getId());
        assertThat(userName1).isEqualTo(userName2);
        userName2.setId(2L);
        assertThat(userName1).isNotEqualTo(userName2);
        userName1.setId(null);
        assertThat(userName1).isNotEqualTo(userName2);
    }
}
