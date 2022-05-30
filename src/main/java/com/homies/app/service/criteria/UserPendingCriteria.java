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
 * Criteria class for the {@link com.homies.app.domain.UserPending} entity. This class is used
 * in {@link com.homies.app.web.rest.UserPendingResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /user-pendings?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class UserPendingCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private FloatFilter pending;

    private BooleanFilter paid;

    private LongFilter spendingListId;

    private LongFilter spendingId;

    private LongFilter settingsListId;

    private Boolean distinct;

    public UserPendingCriteria() {}

    public UserPendingCriteria(UserPendingCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.pending = other.pending == null ? null : other.pending.copy();
        this.paid = other.paid == null ? null : other.paid.copy();
        this.spendingListId = other.spendingListId == null ? null : other.spendingListId.copy();
        this.spendingId = other.spendingId == null ? null : other.spendingId.copy();
        this.settingsListId = other.settingsListId == null ? null : other.settingsListId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public UserPendingCriteria copy() {
        return new UserPendingCriteria(this);
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

    public FloatFilter getPending() {
        return pending;
    }

    public FloatFilter pending() {
        if (pending == null) {
            pending = new FloatFilter();
        }
        return pending;
    }

    public void setPending(FloatFilter pending) {
        this.pending = pending;
    }

    public BooleanFilter getPaid() {
        return paid;
    }

    public BooleanFilter paid() {
        if (paid == null) {
            paid = new BooleanFilter();
        }
        return paid;
    }

    public void setPaid(BooleanFilter paid) {
        this.paid = paid;
    }

    public LongFilter getSpendingListId() {
        return spendingListId;
    }

    public LongFilter spendingListId() {
        if (spendingListId == null) {
            spendingListId = new LongFilter();
        }
        return spendingListId;
    }

    public void setSpendingListId(LongFilter spendingListId) {
        this.spendingListId = spendingListId;
    }

    public LongFilter getSpendingId() {
        return spendingId;
    }

    public LongFilter spendingId() {
        if (spendingId == null) {
            spendingId = new LongFilter();
        }
        return spendingId;
    }

    public void setSpendingId(LongFilter spendingId) {
        this.spendingId = spendingId;
    }

    public LongFilter getSettingsListId() {
        return settingsListId;
    }

    public LongFilter settingsListId() {
        if (settingsListId == null) {
            settingsListId = new LongFilter();
        }
        return settingsListId;
    }

    public void setSettingsListId(LongFilter settingsListId) {
        this.settingsListId = settingsListId;
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
        final UserPendingCriteria that = (UserPendingCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(pending, that.pending) &&
            Objects.equals(paid, that.paid) &&
            Objects.equals(spendingListId, that.spendingListId) &&
            Objects.equals(spendingId, that.spendingId) &&
            Objects.equals(settingsListId, that.settingsListId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pending, paid, spendingListId, spendingId, settingsListId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserPendingCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (pending != null ? "pending=" + pending + ", " : "") +
            (paid != null ? "paid=" + paid + ", " : "") +
            (spendingListId != null ? "spendingListId=" + spendingListId + ", " : "") +
            (spendingId != null ? "spendingId=" + spendingId + ", " : "") +
            (settingsListId != null ? "settingsListId=" + settingsListId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
