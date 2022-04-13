package com.homies.app.web.rest.errors.Task;

import com.homies.app.web.rest.errors.BadRequestAlertException;
import com.homies.app.web.rest.errors.ErrorConstants;

public class TaskWasNotSpecifyIdTask extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public TaskWasNotSpecifyIdTask() {
        super(ErrorConstants.TASK_IDTASK_NOT_SPECIFY, "ID Task was not specify", "userManagement", "IdTaskWasNotSpecify");
    }
}
