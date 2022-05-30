package com.homies.app.web.rest.errors.ShoppingList;

import com.homies.app.web.rest.errors.BadRequestAlertException;
import com.homies.app.web.rest.errors.ErrorConstants;

public class ShoppingListDoesNotExist extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public ShoppingListDoesNotExist() {
        super(ErrorConstants.SHOPPINGlIST_DOES_NOT_GROUP, "ShoppingList doesn`t exist", "userManagement", "ShoppingListDoesNotExist");
    }
}
