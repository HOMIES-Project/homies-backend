package com.homies.app.web.rest.errors.Group;

import com.homies.app.web.rest.errors.BadRequestAlertException;
import com.homies.app.web.rest.errors.ErrorConstants;

public class GroupNotExistException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public GroupNotExistException() {
        super(ErrorConstants.GROUP_NOT_EXIST, "Group doesn`t exist", "userManagement", "GroupDoesNotExist");
    }
}
