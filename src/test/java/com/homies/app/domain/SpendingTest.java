package com.homies.app.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.homies.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SpendingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Spending.class);
        Spending spending1 = new Spending();
        spending1.setId(1L);
        Spending spending2 = new Spending();
        spending2.setId(spending1.getId());
        assertThat(spending1).isEqualTo(spending2);
        spending2.setId(2L);
        assertThat(spending1).isNotEqualTo(spending2);
        spending1.setId(null);
        assertThat(spending1).isNotEqualTo(spending2);
    }
}
