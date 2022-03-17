package com.homies.app.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LocalDateFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.homies.app.domain.Products} entity. This class is used
 * in {@link com.homies.app.web.rest.ProductsResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /products?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class ProductsCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private FloatFilter price;

    private FloatFilter units;

    private StringFilter typeUnit;

    private StringFilter note;

    private LocalDateFilter dataCreated;

    private LocalDateFilter shoppingDate;

    private BooleanFilter purchased;

    private IntegerFilter userCreated;

    private Boolean distinct;

    public ProductsCriteria() {}

    public ProductsCriteria(ProductsCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.price = other.price == null ? null : other.price.copy();
        this.units = other.units == null ? null : other.units.copy();
        this.typeUnit = other.typeUnit == null ? null : other.typeUnit.copy();
        this.note = other.note == null ? null : other.note.copy();
        this.dataCreated = other.dataCreated == null ? null : other.dataCreated.copy();
        this.shoppingDate = other.shoppingDate == null ? null : other.shoppingDate.copy();
        this.purchased = other.purchased == null ? null : other.purchased.copy();
        this.userCreated = other.userCreated == null ? null : other.userCreated.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ProductsCriteria copy() {
        return new ProductsCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public FloatFilter getPrice() {
        return price;
    }

    public FloatFilter price() {
        if (price == null) {
            price = new FloatFilter();
        }
        return price;
    }

    public void setPrice(FloatFilter price) {
        this.price = price;
    }

    public FloatFilter getUnits() {
        return units;
    }

    public FloatFilter units() {
        if (units == null) {
            units = new FloatFilter();
        }
        return units;
    }

    public void setUnits(FloatFilter units) {
        this.units = units;
    }

    public StringFilter getTypeUnit() {
        return typeUnit;
    }

    public StringFilter typeUnit() {
        if (typeUnit == null) {
            typeUnit = new StringFilter();
        }
        return typeUnit;
    }

    public void setTypeUnit(StringFilter typeUnit) {
        this.typeUnit = typeUnit;
    }

    public StringFilter getNote() {
        return note;
    }

    public StringFilter note() {
        if (note == null) {
            note = new StringFilter();
        }
        return note;
    }

    public void setNote(StringFilter note) {
        this.note = note;
    }

    public LocalDateFilter getDataCreated() {
        return dataCreated;
    }

    public LocalDateFilter dataCreated() {
        if (dataCreated == null) {
            dataCreated = new LocalDateFilter();
        }
        return dataCreated;
    }

    public void setDataCreated(LocalDateFilter dataCreated) {
        this.dataCreated = dataCreated;
    }

    public LocalDateFilter getShoppingDate() {
        return shoppingDate;
    }

    public LocalDateFilter shoppingDate() {
        if (shoppingDate == null) {
            shoppingDate = new LocalDateFilter();
        }
        return shoppingDate;
    }

    public void setShoppingDate(LocalDateFilter shoppingDate) {
        this.shoppingDate = shoppingDate;
    }

    public BooleanFilter getPurchased() {
        return purchased;
    }

    public BooleanFilter purchased() {
        if (purchased == null) {
            purchased = new BooleanFilter();
        }
        return purchased;
    }

    public void setPurchased(BooleanFilter purchased) {
        this.purchased = purchased;
    }

    public IntegerFilter getUserCreated() {
        return userCreated;
    }

    public IntegerFilter userCreated() {
        if (userCreated == null) {
            userCreated = new IntegerFilter();
        }
        return userCreated;
    }

    public void setUserCreated(IntegerFilter userCreated) {
        this.userCreated = userCreated;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProductsCriteria that = (ProductsCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(price, that.price) &&
            Objects.equals(units, that.units) &&
            Objects.equals(typeUnit, that.typeUnit) &&
            Objects.equals(note, that.note) &&
            Objects.equals(dataCreated, that.dataCreated) &&
            Objects.equals(shoppingDate, that.shoppingDate) &&
            Objects.equals(purchased, that.purchased) &&
            Objects.equals(userCreated, that.userCreated) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, units, typeUnit, note, dataCreated, shoppingDate, purchased, userCreated, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductsCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (price != null ? "price=" + price + ", " : "") +
            (units != null ? "units=" + units + ", " : "") +
            (typeUnit != null ? "typeUnit=" + typeUnit + ", " : "") +
            (note != null ? "note=" + note + ", " : "") +
            (dataCreated != null ? "dataCreated=" + dataCreated + ", " : "") +
            (shoppingDate != null ? "shoppingDate=" + shoppingDate + ", " : "") +
            (purchased != null ? "purchased=" + purchased + ", " : "") +
            (userCreated != null ? "userCreated=" + userCreated + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
