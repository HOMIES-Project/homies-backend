package com.homies.app.domain;

import java.io.Serializable;
import java.time.LocalDate;
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
