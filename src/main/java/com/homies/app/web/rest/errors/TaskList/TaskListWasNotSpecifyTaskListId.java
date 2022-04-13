package com.homies.app.web.rest.errors.TaskList;

import com.homies.app.web.rest.errors.BadRequestAlertException;
import com.homies.app.web.rest.errors.ErrorConstants;

public class TaskListWasNotSpecifyTaskListId extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public TaskListWasNotSpecifyTaskListId() {
        super(ErrorConstants.TASKLIST_ID_NOT_SPECIFY, "TaskListID was not specify", "userManagement", "TaskListIDWasNotSpecify");
    }
}
