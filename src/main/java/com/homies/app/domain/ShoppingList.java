package com.homies.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ShoppingList.
 */
@Entity
@Table(name = "shopping_list")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ShoppingList implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "total")
    private Float total;

    @NotNull
    @Size(min = 3, max = 20)
    @Column(name = "name_shop_list", length = 20, nullable = false)
    private String nameShopList;

    @OneToMany(mappedBy = "shoppingList", cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "userCreator", "shoppingList" }, allowSetters = true)
    private Set<Products> products = new HashSet<>();

    @JsonIgnoreProperties(
        value = { "userAdmin", "taskList", "spendingList", "shoppingList", "settingsList", "userData" },
        allowSetters = true
    )

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Group group;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ShoppingList id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getTotal() {
        return this.total;
    }

    public ShoppingList total(Float total) {
        this.setTotal(total);
        return this;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public String getNameShopList() {
        return this.nameShopList;
    }

    public ShoppingList nameShopList(String nameShopList) {
        this.setNameShopList(nameShopList);
        return this;
    }

    public void setNameShopList(String nameShopList) {
        this.nameShopList = nameShopList;
    }

    public Set<Products> getProducts() {
        return this.products;
    }

    public void setProducts(Set<Products> products) {
        if (this.products != null) {
            this.products.forEach(i -> i.setShoppingList(null));
        }
        if (products != null) {
            products.forEach(i -> i.setShoppingList(this));
        }
        this.products = products;
    }

    public ShoppingList products(Set<Products> products) {
        this.setProducts(products);
        return this;
    }

    public ShoppingList addProducts(Products products) {
        this.products.add(products);
        products.setShoppingList(this);
        return this;
    }

    public ShoppingList removeProducts(Products products) {
        this.products.remove(products);
        products.setShoppingList(null);
        return this;
    }

    public Group getGroup() {
        return this.group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public ShoppingList group(Group group) {
        this.setGroup(group);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShoppingList)) {
            return false;
        }
        return id != null && id.equals(((ShoppingList) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShoppingList{" +
            "id=" + getId() +
            ", total=" + getTotal() +
            ", nameShopList='" + getNameShopList() + "'" +
            "}";
    }
}
