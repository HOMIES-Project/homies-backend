package com.homies.app.web.rest.errors.Task;

import com.homies.app.web.rest.errors.BadRequestAlertException;
import com.homies.app.web.rest.errors.ErrorConstants;

public class TaskDoesNotExist extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public TaskDoesNotExist() {
        super(ErrorConstants.TASK_IDTASK_NOT_Exist, "Task doesn`t exist", "userManagement", "TaslDoesNotExist");
    }
}
