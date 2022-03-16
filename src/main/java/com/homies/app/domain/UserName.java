package com.homies.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
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
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotNull
    @Column(name = "nick", nullable = false, unique = true)
    private String nick;

    @NotNull
    @Size(min = 8, max = 16)
    @Column(name = "password", length = 16, nullable = false)
    private String password;

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

    @Column(name = "token")
    private String token;

    @ManyToMany(mappedBy = "userNames")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "userNames" }, allowSetters = true)
    private Set<Group> groups = new HashSet<>();

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

    public String getEmail() {
        return this.email;
    }

    public UserName email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNick() {
        return this.nick;
    }

    public UserName nick(String nick) {
        this.setNick(nick);
        return this;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPassword() {
        return this.password;
    }

    public UserName password(String password) {
        this.setPassword(password);
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getToken() {
        return this.token;
    }

    public UserName token(String token) {
        this.setToken(token);
        return this;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Set<Group> getGroups() {
        return this.groups;
    }

    public void setGroups(Set<Group> groups) {
        if (this.groups != null) {
            this.groups.forEach(i -> i.removeUserName(this));
        }
        if (groups != null) {
            groups.forEach(i -> i.addUserName(this));
        }
        this.groups = groups;
    }

    public UserName groups(Set<Group> groups) {
        this.setGroups(groups);
        return this;
    }

    public UserName addGroup(Group group) {
        this.groups.add(group);
        group.getUserNames().add(this);
        return this;
    }

    public UserName removeGroup(Group group) {
        this.groups.remove(group);
        group.getUserNames().remove(this);
        return this;
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
            ", email='" + getEmail() + "'" +
            ", nick='" + getNick() + "'" +
            ", password='" + getPassword() + "'" +
            ", name='" + getName() + "'" +
            ", surname='" + getSurname() + "'" +
            ", photo='" + getPhoto() + "'" +
            ", photoContentType='" + getPhotoContentType() + "'" +
            ", phone='" + getPhone() + "'" +
            ", premium='" + getPremium() + "'" +
            ", birthDate='" + getBirthDate() + "'" +
            ", addDate='" + getAddDate() + "'" +
            ", token='" + getToken() + "'" +
            "}";
    }
}
