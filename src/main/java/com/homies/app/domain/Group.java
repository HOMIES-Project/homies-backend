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
    @Column(name = "id")
    private Long id;

    @Size(min = 10)
    @Column(name = "group_key", unique = true)
    private String groupKey;

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

    @ManyToMany
    @JoinTable(
        name = "rel_jhi_group__user_data",
        joinColumns = @JoinColumn(name = "jhi_group_id"),
        inverseJoinColumns = @JoinColumn(name = "user_data_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "groups", "user", "adminGroups", "taskAsigneds" }, allowSetters = true)
    private Set<UserData> userData = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "groups", "user", "adminGroups", "taskAsigneds" }, allowSetters = true)
    private UserData userAdmin;

    @JsonIgnoreProperties(value = { "group", "tasks" }, allowSetters = true)
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private TaskList taskList;

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

    public Set<UserData> getUserData() {
        return this.userData;
    }

    public void setUserData(Set<UserData> userData) {
        this.userData = userData;
    }

    public Group userData(Set<UserData> userData) {
        this.setUserData(userData);
        return this;
    }

    public Group addUserData(UserData userData) {
        this.userData.add(userData);
        userData.getGroups().add(this);
        return this;
    }

    public Group removeUserData(UserData userData) {
        this.userData.remove(userData);
        userData.getGroups().remove(this);
        return this;
    }

    public UserData getUserAdmin() {
        return this.userAdmin;
    }

    public void setUserAdmin(UserData userData) {
        this.userAdmin = userData;
    }

    public Group userAdmin(UserData userData) {
        this.setUserAdmin(userData);
        return this;
    }

    public TaskList getTaskList() {
        return this.taskList;
    }

    public void setTaskList(TaskList taskList) {
        this.taskList = taskList;
    }

    public Group taskList(TaskList taskList) {
        this.setTaskList(taskList);
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
            ", groupName='" + getGroupName() + "'" +
            ", groupRelationName='" + getGroupRelationName() + "'" +
            ", addGroupDate='" + getAddGroupDate() + "'" +
            "}";
    }
}
