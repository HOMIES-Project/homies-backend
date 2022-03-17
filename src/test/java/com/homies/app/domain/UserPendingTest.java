package com.homies.app.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.homies.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserPendingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserPending.class);
        UserPending userPending1 = new UserPending();
        userPending1.setId(1L);
        UserPending userPending2 = new UserPending();
        userPending2.setId(userPending1.getId());
        assertThat(userPending1).isEqualTo(userPending2);
        userPending2.setId(2L);
        assertThat(userPending1).isNotEqualTo(userPending2);
        userPending1.setId(null);
        assertThat(userPending1).isNotEqualTo(userPending2);
    }
}
