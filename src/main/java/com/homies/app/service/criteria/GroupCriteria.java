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

    private StringFilter groupName;

    private StringFilter groupRelationName;

    private LocalDateFilter addGroupDate;

    private LongFilter userDataId;

    private LongFilter userAdminId;

    private LongFilter taskListId;

    private Boolean distinct;

    public GroupCriteria() {}

    public GroupCriteria(GroupCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.groupKey = other.groupKey == null ? null : other.groupKey.copy();
        this.groupName = other.groupName == null ? null : other.groupName.copy();
        this.groupRelationName = other.groupRelationName == null ? null : other.groupRelationName.copy();
        this.addGroupDate = other.addGroupDate == null ? null : other.addGroupDate.copy();
        this.userDataId = other.userDataId == null ? null : other.userDataId.copy();
        this.userAdminId = other.userAdminId == null ? null : other.userAdminId.copy();
        this.taskListId = other.taskListId == null ? null : other.taskListId.copy();
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

    public LongFilter getUserDataId() {
        return userDataId;
    }

    public LongFilter userDataId() {
        if (userDataId == null) {
            userDataId = new LongFilter();
        }
        return userDataId;
    }

    public void setUserDataId(LongFilter userDataId) {
        this.userDataId = userDataId;
    }

    public LongFilter getUserAdminId() {
        return userAdminId;
    }

    public LongFilter userAdminId() {
        if (userAdminId == null) {
            userAdminId = new LongFilter();
        }
        return userAdminId;
    }

    public void setUserAdminId(LongFilter userAdminId) {
        this.userAdminId = userAdminId;
    }

    public LongFilter getTaskListId() {
        return taskListId;
    }

    public LongFilter taskListId() {
        if (taskListId == null) {
            taskListId = new LongFilter();
        }
        return taskListId;
    }

    public void setTaskListId(LongFilter taskListId) {
        this.taskListId = taskListId;
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
            Objects.equals(groupName, that.groupName) &&
            Objects.equals(groupRelationName, that.groupRelationName) &&
            Objects.equals(addGroupDate, that.addGroupDate) &&
            Objects.equals(userDataId, that.userDataId) &&
            Objects.equals(userAdminId, that.userAdminId) &&
            Objects.equals(taskListId, that.taskListId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, groupKey, groupName, groupRelationName, addGroupDate, userDataId, userAdminId, taskListId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GroupCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (groupKey != null ? "groupKey=" + groupKey + ", " : "") +
            (groupName != null ? "groupName=" + groupName + ", " : "") +
            (groupRelationName != null ? "groupRelationName=" + groupRelationName + ", " : "") +
            (addGroupDate != null ? "addGroupDate=" + addGroupDate + ", " : "") +
            (userDataId != null ? "userDataId=" + userDataId + ", " : "") +
            (userAdminId != null ? "userAdminId=" + userAdminId + ", " : "") +
            (taskListId != null ? "taskListId=" + taskListId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
