package com.homies.app.web.rest.vm;

public class AddProductVM {

    Long idGroup;
    Long idUserData;
    String nameProduct;
    Float units;
    String typeUnit;

    public Long getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(Long idGroup) {
        this.idGroup = idGroup;
    }

    public Long getIdUserData() {
        return idUserData;
    }

    public void setIdUserData(Long idUserData) {
        this.idUserData = idUserData;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
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

    @Override
    public String toString() {
        return "AddProductVM{" +
            "idGroup=" + idGroup +
            ", idUserData=" + idUserData +
            ", nameProduct='" + nameProduct + '\'' +
            ", units=" + units +
            ", typeUnit='" + typeUnit + '\'' +
            '}';
    }
}
