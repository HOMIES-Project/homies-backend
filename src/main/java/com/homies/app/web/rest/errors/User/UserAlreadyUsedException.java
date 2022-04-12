package com.homies.app.web.rest.errors.User;

import com.homies.app.web.rest.errors.BadRequestAlertException;
import com.homies.app.web.rest.errors.ErrorConstants;

public class UserAlreadyUsedException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public UserAlreadyUsedException() {
        super(ErrorConstants.USER_ALREADY_USED_TYPE, "User is already in use!", "userManagement", "userexists");
    }
}
