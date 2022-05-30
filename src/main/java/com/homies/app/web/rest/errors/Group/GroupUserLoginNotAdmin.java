package com.homies.app.web.rest.errors.Group;

import com.homies.app.web.rest.errors.BadRequestAlertException;
import com.homies.app.web.rest.errors.ErrorConstants;

public class GroupUserLoginNotAdmin extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public GroupUserLoginNotAdmin() {
        super(ErrorConstants.GROUP_USER_LOGIN_NOT_IS_ADMIN, "User login not is Admin of group", "userManagement", "LoginNotAdmin");
    }
}
