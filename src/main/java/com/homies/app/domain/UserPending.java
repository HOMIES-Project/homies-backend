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
 * A UserPending.
 */
@Entity
@Table(name = "user_pending")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserPending implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @DecimalMin(value = "0")
    @Column(name = "pending")
    private Float pending;

    @Column(name = "paid")
    private Boolean paid;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = {  }, allowSetters = true)
    private SpendingList spendingList;

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH })
    @JoinTable(
        name = "rel_user_pending__spending",
        joinColumns = @JoinColumn(name = "user_pending_id"),
        inverseJoinColumns = @JoinColumn(name = "spending_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = {  }, allowSetters = true)
    private Set<Spending> spendings = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserPending id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getPending() {
        return this.pending;
    }

    public UserPending pending(Float pending) {
        this.setPending(pending);
        return this;
    }

    public void setPending(Float pending) {
        this.pending = pending;
    }

    public Boolean getPaid() {
        return this.paid;
    }

    public UserPending paid(Boolean paid) {
        this.setPaid(paid);
        return this;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public SpendingList getSpendingList() {
        return this.spendingList;
    }

    public void setSpendingList(SpendingList spendingList) {
        this.spendingList = spendingList;
    }

    public UserPending spendingList(SpendingList spendingList) {
        this.setSpendingList(spendingList);
        return this;
    }

    public Set<Spending> getSpendings() {
        return this.spendings;
    }

    public void setSpendings(Set<Spending> spendings) {
        this.spendings = spendings;
    }

    public UserPending spendings(Set<Spending> spendings) {
        this.setSpendings(spendings);
        return this;
    }

    public UserPending addSpending(Spending spending) {
        this.spendings.add(spending);
        spending.getUserPendings().add(this);
        return this;
    }

    public UserPending removeSpending(Spending spending) {
        this.spendings.remove(spending);
        spending.getUserPendings().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserPending)) {
            return false;
        }
        return id != null && id.equals(((UserPending) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserPending{" +
            "id=" + getId() +
            ", pending=" + getPending() +
            ", paid='" + getPaid() + "'" +
            "}";
    }
}
