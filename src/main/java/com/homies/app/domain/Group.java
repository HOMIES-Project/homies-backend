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
 * A Group.
 */
@Entity
@Table(name = "jhi_group")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Group implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 10)
    @Column(name = "group_key", nullable = false, unique = true)
    private String groupKey;

    @NotNull
    @Size(min = 8)
    @Column(name = "group_password", nullable = false)
    private String groupPassword;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(name = "group_name", length = 50, nullable = false, unique = true)
    private String groupName;

    @NotNull
    @Size(min = 3, max = 100)
    @Column(name = "group_relation_name", length = 100, nullable = false)
    private String groupRelationName;

    @Column(name = "add_group_date")
    private LocalDate addGroupDate;

    @NotNull
    @Column(name = "id_user_name", nullable = false)
    private Long idUserName;

    @ManyToMany
    @JoinTable(
        name = "rel_jhi_group__user_name",
        joinColumns = @JoinColumn(name = "jhi_group_id"),
        inverseJoinColumns = @JoinColumn(name = "user_name_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "userData", "groups" }, allowSetters = true)
    private Set<UserName> userNames = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Group id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupKey() {
        return this.groupKey;
    }

    public Group groupKey(String groupKey) {
        this.setGroupKey(groupKey);
        return this;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public String getGroupPassword() {
        return this.groupPassword;
    }

    public Group groupPassword(String groupPassword) {
        this.setGroupPassword(groupPassword);
        return this;
    }

    public void setGroupPassword(String groupPassword) {
        this.groupPassword = groupPassword;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public Group groupName(String groupName) {
        this.setGroupName(groupName);
        return this;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupRelationName() {
        return this.groupRelationName;
    }

    public Group groupRelationName(String groupRelationName) {
        this.setGroupRelationName(groupRelationName);
        return this;
    }

    public void setGroupRelationName(String groupRelationName) {
        this.groupRelationName = groupRelationName;
    }

    public LocalDate getAddGroupDate() {
        return this.addGroupDate;
    }

    public Group addGroupDate(LocalDate addGroupDate) {
        this.setAddGroupDate(addGroupDate);
        return this;
    }

    public void setAddGroupDate(LocalDate addGroupDate) {
        this.addGroupDate = addGroupDate;
    }

    public Long getIdUserName() {
        return this.idUserName;
    }

    public Group idUserName(Long idUserName) {
        this.setIdUserName(idUserName);
        return this;
    }

    public void setIdUserName(Long idUserName) {
        this.idUserName = idUserName;
    }

    public Set<UserName> getUserNames() {
        return this.userNames;
    }

    public void setUserNames(Set<UserName> userNames) {
        this.userNames = userNames;
    }

    public Group userNames(Set<UserName> userNames) {
        this.setUserNames(userNames);
        return this;
    }

    public Group addUserName(UserName userName) {
        this.userNames.add(userName);
        userName.getGroups().add(this);
        return this;
    }

    public Group removeUserName(UserName userName) {
        this.userNames.remove(userName);
        userName.getGroups().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Group)) {
            return false;
        }
        return id != null && id.equals(((Group) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Group{" +
            "id=" + getId() +
            ", groupKey='" + getGroupKey() + "'" +
            ", groupPassword='" + getGroupPassword() + "'" +
            ", groupName='" + getGroupName() + "'" +
            ", groupRelationName='" + getGroupRelationName() + "'" +
            ", addGroupDate='" + getAddGroupDate() + "'" +
            ", idUserName=" + getIdUserName() +
            "}";
    }
}
