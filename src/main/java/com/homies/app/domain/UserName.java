package com.homies.app.domain;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A UserName.
 */
@Entity
@Table(name = "user_name")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserName implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(name = "surname", length = 50, nullable = false)
    private String surname;

    @Lob
    @Column(name = "photo")
    private byte[] photo;

    @Column(name = "photo_content_type")
    private String photoContentType;

    @Size(min = 6, max = 50)
    @Column(name = "phone", length = 50)
    private String phone;

    @NotNull
    @Column(name = "premium", nullable = false)
    private Boolean premium;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "add_date")
    private LocalDate addDate;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserName id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public UserName name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return this.surname;
    }

    public UserName surname(String surname) {
        this.setSurname(surname);
        return this;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public byte[] getPhoto() {
        return this.photo;
    }

    public UserName photo(byte[] photo) {
        this.setPhoto(photo);
        return this;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getPhotoContentType() {
        return this.photoContentType;
    }

    public UserName photoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
        return this;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }

    public String getPhone() {
        return this.phone;
    }

    public UserName phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getPremium() {
        return this.premium;
    }

    public UserName premium(Boolean premium) {
        this.setPremium(premium);
        return this;
    }

    public void setPremium(Boolean premium) {
        this.premium = premium;
    }

    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    public UserName birthDate(LocalDate birthDate) {
        this.setBirthDate(birthDate);
        return this;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDate getAddDate() {
        return this.addDate;
    }

    public UserName addDate(LocalDate addDate) {
        this.setAddDate(addDate);
        return this;
    }

    public void setAddDate(LocalDate addDate) {
        this.addDate = addDate;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserName)) {
            return false;
        }
        return id != null && id.equals(((UserName) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserName{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", surname='" + getSurname() + "'" +
            ", photo='" + getPhoto() + "'" +
            ", photoContentType='" + getPhotoContentType() + "'" +
            ", phone='" + getPhone() + "'" +
            ", premium='" + getPremium() + "'" +
            ", birthDate='" + getBirthDate() + "'" +
            ", addDate='" + getAddDate() + "'" +
            "}";
    }
}
