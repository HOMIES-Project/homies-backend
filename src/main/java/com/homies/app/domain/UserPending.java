package com.homies.app.domain;

import java.io.Serializable;
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
