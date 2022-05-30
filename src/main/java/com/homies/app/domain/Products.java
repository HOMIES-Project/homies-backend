package com.homies.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Products.
 */
@Entity
@Table(name = "products")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Products implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 20)
    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @Column(name = "price")
    private Float price;

    @Lob
    @Column(name = "photo")
    private byte[] photo;

    @Column(name = "photo_content_type")
    private String photoContentType;

    @NotNull
    @DecimalMin(value = "1")
    @Column(name = "units", nullable = false)
    private Float units;

    @Column(name = "type_unit")
    private String typeUnit;

    @Size(max = 256)
    @Column(name = "note", length = 256)
    private String note;

    @Column(name = "data_created")
    private LocalDate dataCreated;

    @Column(name = "shopping_date")
    private LocalDate shoppingDate;

    @Column(name = "purchased")
    private Boolean purchased;

    @Column(name = "user_created")
    private Integer userCreated;

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "adminGroups", "taskAsigneds", "productCreateds", "groups" }, allowSetters = true)
    private UserData userCreator;

    @ManyToOne
    @JsonIgnoreProperties(value = { "products", "group" }, allowSetters = true)
    private ShoppingList shoppingList;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Products id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Products name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getPrice() {
        return this.price;
    }

    public Products price(Float price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public byte[] getPhoto() {
        return this.photo;
    }

    public Products photo(byte[] photo) {
        this.setPhoto(photo);
        return this;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getPhotoContentType() {
        return this.photoContentType;
    }

    public Products photoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
        return this;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }

    public Float getUnits() {
        return this.units;
    }

    public Products units(Float units) {
        this.setUnits(units);
        return this;
    }

    public void setUnits(Float units) {
        this.units = units;
    }

    public String getTypeUnit() {
        return this.typeUnit;
    }

    public Products typeUnit(String typeUnit) {
        this.setTypeUnit(typeUnit);
        return this;
    }

    public void setTypeUnit(String typeUnit) {
        this.typeUnit = typeUnit;
    }

    public String getNote() {
        return this.note;
    }

    public Products note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDate getDataCreated() {
        return this.dataCreated;
    }

    public Products dataCreated(LocalDate dataCreated) {
        this.setDataCreated(dataCreated);
        return this;
    }

    public void setDataCreated(LocalDate dataCreated) {
        this.dataCreated = dataCreated;
    }

    public LocalDate getShoppingDate() {
        return this.shoppingDate;
    }

    public Products shoppingDate(LocalDate shoppingDate) {
        this.setShoppingDate(shoppingDate);
        return this;
    }

    public void setShoppingDate(LocalDate shoppingDate) {
        this.shoppingDate = shoppingDate;
    }

    public Boolean getPurchased() {
        return this.purchased;
    }

    public Products purchased(Boolean purchased) {
        this.setPurchased(purchased);
        return this;
    }

    public void setPurchased(Boolean purchased) {
        this.purchased = purchased;
    }

    public Integer getUserCreated() {
        return this.userCreated;
    }

    public Products userCreated(Integer userCreated) {
        this.setUserCreated(userCreated);
        return this;
    }

    public void setUserCreated(Integer userCreated) {
        this.userCreated = userCreated;
    }

    public UserData getUserCreator() {
        return this.userCreator;
    }

    public void setUserCreator(UserData userData) {
        this.userCreator = userData;
    }

    public Products userCreator(UserData userData) {
        this.setUserCreator(userData);
        return this;
    }

    public ShoppingList getShoppingList() {
        return this.shoppingList;
    }

    public void setShoppingList(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
    }

    public Products shoppingList(ShoppingList shoppingList) {
        this.setShoppingList(shoppingList);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Products)) {
            return false;
        }
        return id != null && id.equals(((Products) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Products{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", price=" + getPrice() +
            ", photo='" + getPhoto() + "'" +
            ", photoContentType='" + getPhotoContentType() + "'" +
            ", units=" + getUnits() +
            ", typeUnit='" + getTypeUnit() + "'" +
            ", note='" + getNote() + "'" +
            ", dataCreated='" + getDataCreated() + "'" +
            ", shoppingDate='" + getShoppingDate() + "'" +
            ", purchased='" + getPurchased() + "'" +
            ", userCreated=" + getUserCreated() +
            "}";
    }
}
