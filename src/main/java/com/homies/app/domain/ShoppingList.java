package com.homies.app.domain;

import java.io.Serializable;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 20)
    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @Column(name = "total")
    private Float total;

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

    public String getName() {
        return this.name;
    }

    public ShoppingList name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
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
            ", name='" + getName() + "'" +
            ", total=" + getTotal() +
            "}";
    }
}
