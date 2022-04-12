package com.homies.app.web.rest.errors.User;

import com.homies.app.web.rest.errors.BadRequestAlertException;
import com.homies.app.web.rest.errors.ErrorConstants;

public class UserDoesNotExist extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public UserDoesNotExist() {
        super(ErrorConstants.TASK_USER_NOT_EXIST, "User doesn`t exist", "userManagement", "UserDoesNotExist");
    }
}
