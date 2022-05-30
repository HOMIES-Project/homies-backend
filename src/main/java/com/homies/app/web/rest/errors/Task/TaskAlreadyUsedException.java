package com.homies.app.web.rest.errors.Task;

import com.homies.app.web.rest.errors.BadRequestAlertException;
import com.homies.app.web.rest.errors.ErrorConstants;

public class TaskAlreadyUsedException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public TaskAlreadyUsedException() {
        super(ErrorConstants.TASK_ALREADY_USED_TYPE, "Name task is already in use!", "userManagement", "TaskExists");
    }
}
