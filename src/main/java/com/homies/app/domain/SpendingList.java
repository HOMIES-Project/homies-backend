package com.homies.app.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SpendingList.
 */
@Entity
@Table(name = "spending_list")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SpendingList implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @DecimalMin(value = "0")
    @Column(name = "total")
    private Float total;

    @NotNull
    @Size(min = 3, max = 20)
    @Column(name = "name_spend_list", length = 20, nullable = false)
    private String nameSpendList;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SpendingList id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getTotal() {
        return this.total;
    }

    public SpendingList total(Float total) {
        this.setTotal(total);
        return this;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public String getNameSpendList() {
        return this.nameSpendList;
    }

    public SpendingList nameSpendList(String nameSpendList) {
        this.setNameSpendList(nameSpendList);
        return this;
    }

    public void setNameSpendList(String nameSpendList) {
        this.nameSpendList = nameSpendList;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpendingList)) {
            return false;
        }
        return id != null && id.equals(((SpendingList) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SpendingList{" +
            "id=" + getId() +
            ", total=" + getTotal() +
            ", nameSpendList='" + getNameSpendList() + "'" +
            "}";
    }
}
