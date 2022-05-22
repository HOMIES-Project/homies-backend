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
 * A Spending.
 */
@Entity
@Table(name = "spending")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Spending implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "payer", nullable = false)
    private Integer payer;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(name = "name_cost", length = 50, nullable = false)
    private String nameCost;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "cost", nullable = false)
    private Float cost;

    @Lob
    @Column(name = "photo")
    private byte[] photo;

    @Column(name = "photo_content_type")
    private String photoContentType;

    @Size(max = 100)
    @Column(name = "descripcion", length = 100)
    private String descripcion;

    @DecimalMin(value = "0")
    @Column(name = "paid")
    private Float paid;

    @DecimalMin(value = "0")
    @Column(name = "pending")
    private Float pending;

    @Column(name = "finished")
    private Boolean finished;

    @ManyToMany(mappedBy = "spendings", fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "spendingList", "spendings", "settingsList" }, allowSetters = true)
    private Set<UserPending> userPendings = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Spending id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPayer() {
        return this.payer;
    }

    public Spending payer(Integer payer) {
        this.setPayer(payer);
        return this;
    }

    public void setPayer(Integer payer) {
        this.payer = payer;
    }

    public String getNameCost() {
        return this.nameCost;
    }

    public Spending nameCost(String nameCost) {
        this.setNameCost(nameCost);
        return this;
    }

    public void setNameCost(String nameCost) {
        this.nameCost = nameCost;
    }

    public Float getCost() {
        return this.cost;
    }

    public Spending cost(Float cost) {
        this.setCost(cost);
        return this;
    }

    public void setCost(Float cost) {
        this.cost = cost;
    }

    public byte[] getPhoto() {
        return this.photo;
    }

    public Spending photo(byte[] photo) {
        this.setPhoto(photo);
        return this;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getPhotoContentType() {
        return this.photoContentType;
    }

    public Spending photoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
        return this;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public Spending descripcion(String descripcion) {
        this.setDescripcion(descripcion);
        return this;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Float getPaid() {
        return this.paid;
    }

    public Spending paid(Float paid) {
        this.setPaid(paid);
        return this;
    }

    public void setPaid(Float paid) {
        this.paid = paid;
    }

    public Float getPending() {
        return this.pending;
    }

    public Spending pending(Float pending) {
        this.setPending(pending);
        return this;
    }

    public void setPending(Float pending) {
        this.pending = pending;
    }

    public Boolean getFinished() {
        return this.finished;
    }

    public Spending finished(Boolean finished) {
        this.setFinished(finished);
        return this;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }

    public Set<UserPending> getUserPendings() {
        return this.userPendings;
    }

    public void setUserPendings(Set<UserPending> userPendings) {
        if (this.userPendings != null) {
            this.userPendings.forEach(i -> i.removeSpending(this));
        }
        if (userPendings != null) {
            userPendings.forEach(i -> i.addSpending(this));
        }
        this.userPendings = userPendings;
    }

    public Spending userPendings(Set<UserPending> userPendings) {
        this.setUserPendings(userPendings);
        return this;
    }

    public Spending addUserPending(UserPending userPending) {
        this.userPendings.add(userPending);
        userPending.getSpendings().add(this);
        return this;
    }

    public Spending removeUserPending(UserPending userPending) {
        this.userPendings.remove(userPending);
        userPending.getSpendings().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Spending)) {
            return false;
        }
        return id != null && id.equals(((Spending) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Spending{" +
            "id=" + getId() +
            ", payer=" + getPayer() +
            ", nameCost='" + getNameCost() + "'" +
            ", cost=" + getCost() +
            ", photo='" + getPhoto() + "'" +
            ", photoContentType='" + getPhotoContentType() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", paid=" + getPaid() +
            ", pending=" + getPending() +
            ", finished='" + getFinished() + "'" +
            "}";
    }
}
