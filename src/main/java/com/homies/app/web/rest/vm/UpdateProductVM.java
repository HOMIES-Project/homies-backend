package com.homies.app.web.rest.vm;

import javax.persistence.Column;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Size;

public class UpdateProductVM {

    private String login;

    private Long idGroup;

    private Long idProduct;

    private String typeUnit;

    @Size(min = 3, max = 20)
    private String name;

    @DecimalMin(value = "1")
    private Float units;

    private boolean purchased;


    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Long getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(Long idGroup) {
        this.idGroup = idGroup;
    }

    public Long getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(Long idProduct) {
        this.idProduct = idProduct;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getUnits() {
        return units;
    }

    public void setUnits(Float units) {
        this.units = units;
    }

    public String getTypeUnit() {
        return typeUnit;
    }

    public void setTypeUnit(String typeUnit) {
        this.typeUnit = typeUnit;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    @Override
    public String toString() {
        return "UpdateProductVM{" +
            "login='" + login + '\'' +
            ", idGroup=" + idGroup +
            ", idProduct=" + idProduct +
            ", typeUnit='" + typeUnit + '\'' +
            ", name='" + name + '\'' +
            ", units=" + units +
            ", purchased=" + purchased +
            '}';
    }
}
