package com.homies.app.web.rest.errors.Group;

import com.homies.app.web.rest.errors.BadRequestAlertException;
import com.homies.app.web.rest.errors.ErrorConstants;

public class GroupWasNotSpecifyIdGroup extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public GroupWasNotSpecifyIdGroup() {
        super(ErrorConstants.GROUP_IDGROUP_NOT_SPECIFY, "Id group was not specify", "userManagement", "idGroupWasNotSpecify");
    }
}
