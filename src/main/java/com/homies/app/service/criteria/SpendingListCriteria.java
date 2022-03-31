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
 * Criteria class for the {@link com.homies.app.domain.SpendingList} entity. This class is used
 * in {@link com.homies.app.web.rest.SpendingListResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /spending-lists?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class SpendingListCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private FloatFilter total;

    private StringFilter nameSpendList;

    private LongFilter spendingId;

    private LongFilter settingsListId;

    private LongFilter groupId;

    private Boolean distinct;

    public SpendingListCriteria() {}

    public SpendingListCriteria(SpendingListCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.total = other.total == null ? null : other.total.copy();
        this.nameSpendList = other.nameSpendList == null ? null : other.nameSpendList.copy();
        this.spendingId = other.spendingId == null ? null : other.spendingId.copy();
        this.settingsListId = other.settingsListId == null ? null : other.settingsListId.copy();
        this.groupId = other.groupId == null ? null : other.groupId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public SpendingListCriteria copy() {
        return new SpendingListCriteria(this);
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

    public FloatFilter getTotal() {
        return total;
    }

    public FloatFilter total() {
        if (total == null) {
            total = new FloatFilter();
        }
        return total;
    }

    public void setTotal(FloatFilter total) {
        this.total = total;
    }

    public StringFilter getNameSpendList() {
        return nameSpendList;
    }

    public StringFilter nameSpendList() {
        if (nameSpendList == null) {
            nameSpendList = new StringFilter();
        }
        return nameSpendList;
    }

    public void setNameSpendList(StringFilter nameSpendList) {
        this.nameSpendList = nameSpendList;
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
        final SpendingListCriteria that = (SpendingListCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(total, that.total) &&
            Objects.equals(nameSpendList, that.nameSpendList) &&
            Objects.equals(spendingId, that.spendingId) &&
            Objects.equals(settingsListId, that.settingsListId) &&
            Objects.equals(groupId, that.groupId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, total, nameSpendList, spendingId, settingsListId, groupId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SpendingListCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (total != null ? "total=" + total + ", " : "") +
            (nameSpendList != null ? "nameSpendList=" + nameSpendList + ", " : "") +
            (spendingId != null ? "spendingId=" + spendingId + ", " : "") +
            (settingsListId != null ? "settingsListId=" + settingsListId + ", " : "") +
            (groupId != null ? "groupId=" + groupId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
