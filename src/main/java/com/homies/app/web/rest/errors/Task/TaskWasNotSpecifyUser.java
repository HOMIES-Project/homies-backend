package com.homies.app.web.rest.errors.Task;

import com.homies.app.web.rest.errors.BadRequestAlertException;
import com.homies.app.web.rest.errors.ErrorConstants;

public class TaskWasNotSpecifyUser extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public TaskWasNotSpecifyUser() {
        super(ErrorConstants.TASK_USER_NOT_SPECIFY, "User was not specify", "userManagement", "UserWasNotSpecify");
    }
}
