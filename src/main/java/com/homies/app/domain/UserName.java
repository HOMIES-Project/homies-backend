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
 * A UserName.
 */
@Entity
@Table(name = "user_name")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserName implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 10, max = 50)
    @Column(name = "user_name", length = 50, nullable = false)
    private String user_name;

    @NotNull
    @Size(min = 8, max = 16)
    @Column(name = "password", length = 16, nullable = false)
    private String password;

    @Column(name = "token")
    private String token;

    @JsonIgnoreProperties(value = { "userName" }, allowSetters = true)
    @OneToOne(optional = false)
    @NotNull
    @MapsId
    @JoinColumn(name = "id")
    private UserData userData;

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

    public String getUser_name() {
        return this.user_name;
    }

    public UserName user_name(String user_name) {
        this.setUser_name(user_name);
        return this;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
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

    public UserData getUserData() {
        return this.userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public UserName userData(UserData userData) {
        this.setUserData(userData);
        return this;
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
            ", user_name='" + getUser_name() + "'" +
            ", password='" + getPassword() + "'" +
            ", token='" + getToken() + "'" +
            "}";
    }
}
