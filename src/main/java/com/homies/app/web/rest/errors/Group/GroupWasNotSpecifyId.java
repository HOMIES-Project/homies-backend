package com.homies.app.web.rest.errors.Group;

import com.homies.app.web.rest.errors.BadRequestAlertException;
import com.homies.app.web.rest.errors.ErrorConstants;

public class GroupWasNotSpecifyId extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public GroupWasNotSpecifyId() {
        super(ErrorConstants.GROUP_ID_NOT_SPECIFY, "Id was not specify", "userManagement", "idWasNotSpecify");
    }
}
