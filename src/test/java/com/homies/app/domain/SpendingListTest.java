package com.homies.app.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.homies.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SpendingListTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SpendingList.class);
        SpendingList spendingList1 = new SpendingList();
        spendingList1.setId(1L);
        SpendingList spendingList2 = new SpendingList();
        spendingList2.setId(spendingList1.getId());
        assertThat(spendingList1).isEqualTo(spendingList2);
        spendingList2.setId(2L);
        assertThat(spendingList1).isNotEqualTo(spendingList2);
        spendingList1.setId(null);
        assertThat(spendingList1).isNotEqualTo(spendingList2);
    }
}
