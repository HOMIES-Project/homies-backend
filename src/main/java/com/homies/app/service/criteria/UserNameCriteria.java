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

    private StringFilter email;

    private StringFilter nick;

    private StringFilter password;

    private StringFilter name;

    private StringFilter surname;

    private StringFilter phone;

    private BooleanFilter premium;

    private LocalDateFilter birthDate;

    private LocalDateFilter addDate;

    private StringFilter token;

    private LongFilter groupId;

    private Boolean distinct;

    public UserNameCriteria() {}

    public UserNameCriteria(UserNameCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.email = other.email == null ? null : other.email.copy();
        this.nick = other.nick == null ? null : other.nick.copy();
        this.password = other.password == null ? null : other.password.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.surname = other.surname == null ? null : other.surname.copy();
        this.phone = other.phone == null ? null : other.phone.copy();
        this.premium = other.premium == null ? null : other.premium.copy();
        this.birthDate = other.birthDate == null ? null : other.birthDate.copy();
        this.addDate = other.addDate == null ? null : other.addDate.copy();
        this.token = other.token == null ? null : other.token.copy();
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

    public StringFilter getEmail() {
        return email;
    }

    public StringFilter email() {
        if (email == null) {
            email = new StringFilter();
        }
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public StringFilter getNick() {
        return nick;
    }

    public StringFilter nick() {
        if (nick == null) {
            nick = new StringFilter();
        }
        return nick;
    }

    public void setNick(StringFilter nick) {
        this.nick = nick;
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

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getSurname() {
        return surname;
    }

    public StringFilter surname() {
        if (surname == null) {
            surname = new StringFilter();
        }
        return surname;
    }

    public void setSurname(StringFilter surname) {
        this.surname = surname;
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
            Objects.equals(email, that.email) &&
            Objects.equals(nick, that.nick) &&
            Objects.equals(password, that.password) &&
            Objects.equals(name, that.name) &&
            Objects.equals(surname, that.surname) &&
            Objects.equals(phone, that.phone) &&
            Objects.equals(premium, that.premium) &&
            Objects.equals(birthDate, that.birthDate) &&
            Objects.equals(addDate, that.addDate) &&
            Objects.equals(token, that.token) &&
            Objects.equals(groupId, that.groupId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, nick, password, name, surname, phone, premium, birthDate, addDate, token, groupId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserNameCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (email != null ? "email=" + email + ", " : "") +
            (nick != null ? "nick=" + nick + ", " : "") +
            (password != null ? "password=" + password + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (surname != null ? "surname=" + surname + ", " : "") +
            (phone != null ? "phone=" + phone + ", " : "") +
            (premium != null ? "premium=" + premium + ", " : "") +
            (birthDate != null ? "birthDate=" + birthDate + ", " : "") +
            (addDate != null ? "addDate=" + addDate + ", " : "") +
            (token != null ? "token=" + token + ", " : "") +
            (groupId != null ? "groupId=" + groupId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
