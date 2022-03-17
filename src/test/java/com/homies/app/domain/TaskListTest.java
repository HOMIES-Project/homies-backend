package com.homies.app.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.homies.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TaskListTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaskList.class);
        TaskList taskList1 = new TaskList();
        taskList1.setId(1L);
        TaskList taskList2 = new TaskList();
        taskList2.setId(taskList1.getId());
        assertThat(taskList1).isEqualTo(taskList2);
        taskList2.setId(2L);
        assertThat(taskList1).isNotEqualTo(taskList2);
        taskList1.setId(null);
        assertThat(taskList1).isNotEqualTo(taskList2);
    }
}
