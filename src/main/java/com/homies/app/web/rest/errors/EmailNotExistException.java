package com.homies.app.web.rest.errors;

public class EmailNotExistException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public EmailNotExistException() {
        super(ErrorConstants.EMAIL_NOT_EXIST_TYPE, "Password reset requested for non existing mail!", "userManagement", "emailnotexists");
    }
}
