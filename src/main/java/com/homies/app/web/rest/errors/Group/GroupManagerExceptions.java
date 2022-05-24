package com.homies.app.web.rest.errors.Group;

import com.homies.app.web.rest.errors.BadRequestAlertException;
import com.homies.app.web.rest.errors.ErrorConstants;

public class GroupManagerExceptions extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public GroupManagerExceptions() {
        super(ErrorConstants.GROUP_NOT_EXIST, "Group doesn`t exist", "userManagement", "GroupDoesNotExist");
    }

    public void GroupError() {

    }
}
