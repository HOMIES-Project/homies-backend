package com.homies.app.web.rest.errors.User;

import com.homies.app.web.rest.errors.BadRequestAlertException;
import com.homies.app.web.rest.errors.ErrorConstants;

public class UserDoesNotExistInGroup extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public UserDoesNotExistInGroup() {
        super(ErrorConstants.USER_DOES_NOT_GROUP, "User doesn`t exist in group", "userManagement", "UserDoesNotExistInGroup");
    }
}
