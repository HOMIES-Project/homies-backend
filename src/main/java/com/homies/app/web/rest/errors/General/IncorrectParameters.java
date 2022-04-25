package com.homies.app.web.rest.errors.General;

import com.homies.app.web.rest.errors.BadRequestAlertException;
import com.homies.app.web.rest.errors.ErrorConstants;

public class IncorrectParameters extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public IncorrectParameters() {
        super(ErrorConstants.INCORRECT_PARAMETERS, "Incorrect parameters", "userManagement", "IncorrectpParameters");
    }
}
