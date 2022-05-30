package com.homies.app.web.rest.errors.User;

import com.homies.app.web.rest.errors.BadRequestAlertException;
import com.homies.app.web.rest.errors.ErrorConstants;

public class UserWasNotSpecifyLogin extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public UserWasNotSpecifyLogin() {
        super(ErrorConstants.USER_LOGIN_NOT_SPECIFY, "Login was not specify", "userManagement", "LoginWasNotSpecify");
    }
}
