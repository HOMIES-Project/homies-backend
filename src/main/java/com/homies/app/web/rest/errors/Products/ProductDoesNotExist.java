package com.homies.app.web.rest.errors.Products;

import com.homies.app.web.rest.errors.BadRequestAlertException;
import com.homies.app.web.rest.errors.ErrorConstants;

public class ProductDoesNotExist extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public ProductDoesNotExist() {
        super(ErrorConstants.PRODUCT_DOES_NOT_EXIST, "Product doesn`t exist", "userManagement", "ProductDoesNotExist");
    }
}
