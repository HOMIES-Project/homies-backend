package com.homies.app.web.rest.errors.Task;

import com.homies.app.web.rest.errors.BadRequestAlertException;
import com.homies.app.web.rest.errors.ErrorConstants;

public class TaskUserDoesNotExist extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public TaskUserDoesNotExist() {
        super(ErrorConstants.TASK_USER_NOT_EXIST, "User doesn`t exist", "userManagement", "UserDoesNotExist");
    }
}
