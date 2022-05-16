package com.homies.app.web.rest.vm;

import javax.persistence.Column;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Size;

public class UpdateProductVM {

    private String login;

    private Long idGroup;

    private Long idProduct;

    @Size(min = 3, max = 20)
    private String name;

    @DecimalMin(value = "1")
    private Float units;
    
}
