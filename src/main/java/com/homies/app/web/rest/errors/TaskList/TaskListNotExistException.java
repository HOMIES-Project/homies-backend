package com.homies.app.web.rest.errors.TaskList;

import com.homies.app.web.rest.errors.BadRequestAlertException;
import com.homies.app.web.rest.errors.ErrorConstants;

public class TaskListNotExistException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public TaskListNotExistException() {
        super(ErrorConstants.TASKLIST_NOT_EXIST, "TaskList doesn`t exist", "userManagement", "TaskListDoesNotExist");
    }
}
