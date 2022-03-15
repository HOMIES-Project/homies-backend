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
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.homies.app.domain.UserName} entity. This class is used
 * in {@link com.homies.app.web.rest.UserNameResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /user-names?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class UserNameCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter user_name;

    private StringFilter password;

    private StringFilter token;

    private LongFilter userDataId;

    private LongFilter groupId;

    private Boolean distinct;

    public UserNameCriteria() {}

    public UserNameCriteria(UserNameCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.user_name = other.user_name == null ? null : other.user_name.copy();
        this.password = other.password == null ? null : other.password.copy();
        this.token = other.token == null ? null : other.token.copy();
        this.userDataId = other.userDataId == null ? null : other.userDataId.copy();
        this.groupId = other.groupId == null ? null : other.groupId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public UserNameCriteria copy() {
        return new UserNameCriteria(this);
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

    public StringFilter getUser_name() {
        return user_name;
    }

    public StringFilter user_name() {
        if (user_name == null) {
            user_name = new StringFilter();
        }
        return user_name;
    }

    public void setUser_name(StringFilter user_name) {
        this.user_name = user_name;
    }

    public StringFilter getPassword() {
        return password;
    }

    public StringFilter password() {
        if (password == null) {
            password = new StringFilter();
        }
        return password;
    }

    public void setPassword(StringFilter password) {
        this.password = password;
    }

    public StringFilter getToken() {
        return token;
    }

    public StringFilter token() {
        if (token == null) {
            token = new StringFilter();
        }
        return token;
    }

    public void setToken(StringFilter token) {
        this.token = token;
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
        final UserNameCriteria that = (UserNameCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(user_name, that.user_name) &&
            Objects.equals(password, that.password) &&
            Objects.equals(token, that.token) &&
            Objects.equals(userDataId, that.userDataId) &&
            Objects.equals(groupId, that.groupId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user_name, password, token, userDataId, groupId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserNameCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (user_name != null ? "user_name=" + user_name + ", " : "") +
            (password != null ? "password=" + password + ", " : "") +
            (token != null ? "token=" + token + ", " : "") +
            (userDataId != null ? "userDataId=" + userDataId + ", " : "") +
            (groupId != null ? "groupId=" + groupId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
