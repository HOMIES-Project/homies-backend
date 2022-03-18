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
 * Criteria class for the {@link com.homies.app.domain.UserData} entity. This class is used
 * in {@link com.homies.app.web.rest.UserDataResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /user-data?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class UserDataCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter phone;

    private BooleanFilter premium;

    private LocalDateFilter birthDate;

    private LocalDateFilter addDate;

    private LongFilter groupId;

    private LongFilter userId;

    private LongFilter adminGroupsId;

    private LongFilter taskAsignedId;

    private Boolean distinct;

    public UserDataCriteria() {}

    public UserDataCriteria(UserDataCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.phone = other.phone == null ? null : other.phone.copy();
        this.premium = other.premium == null ? null : other.premium.copy();
        this.birthDate = other.birthDate == null ? null : other.birthDate.copy();
        this.addDate = other.addDate == null ? null : other.addDate.copy();
        this.groupId = other.groupId == null ? null : other.groupId.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.adminGroupsId = other.adminGroupsId == null ? null : other.adminGroupsId.copy();
        this.taskAsignedId = other.taskAsignedId == null ? null : other.taskAsignedId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public UserDataCriteria copy() {
        return new UserDataCriteria(this);
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

    public StringFilter getPhone() {
        return phone;
    }

    public StringFilter phone() {
        if (phone == null) {
            phone = new StringFilter();
        }
        return phone;
    }

    public void setPhone(StringFilter phone) {
        this.phone = phone;
    }

    public BooleanFilter getPremium() {
        return premium;
    }

    public BooleanFilter premium() {
        if (premium == null) {
            premium = new BooleanFilter();
        }
        return premium;
    }

    public void setPremium(BooleanFilter premium) {
        this.premium = premium;
    }

    public LocalDateFilter getBirthDate() {
        return birthDate;
    }

    public LocalDateFilter birthDate() {
        if (birthDate == null) {
            birthDate = new LocalDateFilter();
        }
        return birthDate;
    }

    public void setBirthDate(LocalDateFilter birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDateFilter getAddDate() {
        return addDate;
    }

    public LocalDateFilter addDate() {
        if (addDate == null) {
            addDate = new LocalDateFilter();
        }
        return addDate;
    }

    public void setAddDate(LocalDateFilter addDate) {
        this.addDate = addDate;
    }

    public LongFilter getGroupId() {
        return groupId;
    }

    public LongFilter groupId() {
        if (groupId == null) {
            groupId = new LongFilter();
        }
        return groupId;
    }

    public void setGroupId(LongFilter groupId) {
        this.groupId = groupId;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public LongFilter userId() {
        if (userId == null) {
            userId = new LongFilter();
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public LongFilter getAdminGroupsId() {
        return adminGroupsId;
    }

    public LongFilter adminGroupsId() {
        if (adminGroupsId == null) {
            adminGroupsId = new LongFilter();
        }
        return adminGroupsId;
    }

    public void setAdminGroupsId(LongFilter adminGroupsId) {
        this.adminGroupsId = adminGroupsId;
    }

    public LongFilter getTaskAsignedId() {
        return taskAsignedId;
    }

    public LongFilter taskAsignedId() {
        if (taskAsignedId == null) {
            taskAsignedId = new LongFilter();
        }
        return taskAsignedId;
    }

    public void setTaskAsignedId(LongFilter taskAsignedId) {
        this.taskAsignedId = taskAsignedId;
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
        final UserDataCriteria that = (UserDataCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(phone, that.phone) &&
            Objects.equals(premium, that.premium) &&
            Objects.equals(birthDate, that.birthDate) &&
            Objects.equals(addDate, that.addDate) &&
            Objects.equals(groupId, that.groupId) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(adminGroupsId, that.adminGroupsId) &&
            Objects.equals(taskAsignedId, that.taskAsignedId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, phone, premium, birthDate, addDate, groupId, userId, adminGroupsId, taskAsignedId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserDataCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (phone != null ? "phone=" + phone + ", " : "") +
            (premium != null ? "premium=" + premium + ", " : "") +
            (birthDate != null ? "birthDate=" + birthDate + ", " : "") +
            (addDate != null ? "addDate=" + addDate + ", " : "") +
            (groupId != null ? "groupId=" + groupId + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (adminGroupsId != null ? "adminGroupsId=" + adminGroupsId + ", " : "") +
            (taskAsignedId != null ? "taskAsignedId=" + taskAsignedId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
