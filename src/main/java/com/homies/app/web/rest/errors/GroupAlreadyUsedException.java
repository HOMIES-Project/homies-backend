package com.homies.app.web.rest.errors;

public class GroupAlreadyUsedException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public GroupAlreadyUsedException() {
        super(ErrorConstants.GROUP_ALREADY_USED_TYPE, "Group is already in use!", "userManagement", "groupExists");
    }
}
