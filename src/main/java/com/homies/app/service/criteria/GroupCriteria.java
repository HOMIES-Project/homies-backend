package com.homies.app.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LocalDateFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.homies.app.domain.Group} entity. This class is used
 * in {@link com.homies.app.web.rest.GroupResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /groups?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class GroupCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter groupKey;

    private StringFilter groupPassword;

    private StringFilter groupName;

    private StringFilter groupRelationName;

    private LocalDateFilter addGroupDate;

    private LongFilter idUserName;

    private LongFilter userNameId;

    private Boolean distinct;

    public GroupCriteria() {}

    public GroupCriteria(GroupCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.groupKey = other.groupKey == null ? null : other.groupKey.copy();
        this.groupPassword = other.groupPassword == null ? null : other.groupPassword.copy();
        this.groupName = other.groupName == null ? null : other.groupName.copy();
        this.groupRelationName = other.groupRelationName == null ? null : other.groupRelationName.copy();
        this.addGroupDate = other.addGroupDate == null ? null : other.addGroupDate.copy();
        this.idUserName = other.idUserName == null ? null : other.idUserName.copy();
        this.userNameId = other.userNameId == null ? null : other.userNameId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public GroupCriteria copy() {
        return new GroupCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getGroupKey() {
        return groupKey;
    }

    public StringFilter groupKey() {
        if (groupKey == null) {
            groupKey = new StringFilter();
        }
        return groupKey;
    }

    public void setGroupKey(StringFilter groupKey) {
        this.groupKey = groupKey;
    }

    public StringFilter getGroupPassword() {
        return groupPassword;
    }

    public StringFilter groupPassword() {
        if (groupPassword == null) {
            groupPassword = new StringFilter();
        }
        return groupPassword;
    }

    public void setGroupPassword(StringFilter groupPassword) {
        this.groupPassword = groupPassword;
    }

    public StringFilter getGroupName() {
        return groupName;
    }

    public StringFilter groupName() {
        if (groupName == null) {
            groupName = new StringFilter();
        }
        return groupName;
    }

    public void setGroupName(StringFilter groupName) {
        this.groupName = groupName;
    }

    public StringFilter getGroupRelationName() {
        return groupRelationName;
    }

    public StringFilter groupRelationName() {
        if (groupRelationName == null) {
            groupRelationName = new StringFilter();
        }
        return groupRelationName;
    }

    public void setGroupRelationName(StringFilter groupRelationName) {
        this.groupRelationName = groupRelationName;
    }

    public LocalDateFilter getAddGroupDate() {
        return addGroupDate;
    }

    public LocalDateFilter addGroupDate() {
        if (addGroupDate == null) {
            addGroupDate = new LocalDateFilter();
        }
        return addGroupDate;
    }

    public void setAddGroupDate(LocalDateFilter addGroupDate) {
        this.addGroupDate = addGroupDate;
    }

    public LongFilter getIdUserName() {
        return idUserName;
    }

    public LongFilter idUserName() {
        if (idUserName == null) {
            idUserName = new LongFilter();
        }
        return idUserName;
    }

    public void setIdUserName(LongFilter idUserName) {
        this.idUserName = idUserName;
    }

    public LongFilter getUserNameId() {
        return userNameId;
    }

    public LongFilter userNameId() {
        if (userNameId == null) {
            userNameId = new LongFilter();
        }
        return userNameId;
    }

    public void setUserNameId(LongFilter userNameId) {
        this.userNameId = userNameId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final GroupCriteria that = (GroupCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(groupKey, that.groupKey) &&
            Objects.equals(groupPassword, that.groupPassword) &&
            Objects.equals(groupName, that.groupName) &&
            Objects.equals(groupRelationName, that.groupRelationName) &&
            Objects.equals(addGroupDate, that.addGroupDate) &&
            Objects.equals(idUserName, that.idUserName) &&
            Objects.equals(userNameId, that.userNameId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, groupKey, groupPassword, groupName, groupRelationName, addGroupDate, idUserName, userNameId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GroupCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (groupKey != null ? "groupKey=" + groupKey + ", " : "") +
            (groupPassword != null ? "groupPassword=" + groupPassword + ", " : "") +
            (groupName != null ? "groupName=" + groupName + ", " : "") +
            (groupRelationName != null ? "groupRelationName=" + groupRelationName + ", " : "") +
            (addGroupDate != null ? "addGroupDate=" + addGroupDate + ", " : "") +
            (idUserName != null ? "idUserName=" + idUserName + ", " : "") +
            (userNameId != null ? "userNameId=" + userNameId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
