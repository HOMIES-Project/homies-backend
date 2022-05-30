package com.homies.app.web.rest.errors.Group;

import com.homies.app.web.rest.errors.BadRequestAlertException;
import com.homies.app.web.rest.errors.ErrorConstants;

public class GroupWasNotSpecifyLogin extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public GroupWasNotSpecifyLogin() {
        super(ErrorConstants.GROUP_LOGIN_NOT_SPECIFY, "Login was not specify", "userManagement", "LoginWasNotSpecify");
    }
}
