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
 * Criteria class for the {@link com.homies.app.domain.SettingsList} entity. This class is used
 * in {@link com.homies.app.web.rest.SettingsListResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /settings-lists?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class SettingsListCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BooleanFilter settingOne;

    private BooleanFilter settingTwo;

    private BooleanFilter settingThree;

    private BooleanFilter settingFour;

    private BooleanFilter settingFive;

    private BooleanFilter settingSix;

    private BooleanFilter settingSeven;

    private LongFilter spendingListId;

    private LongFilter userPendingId;

    private LongFilter groupId;

    private Boolean distinct;

    public SettingsListCriteria() {}

    public SettingsListCriteria(SettingsListCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.settingOne = other.settingOne == null ? null : other.settingOne.copy();
        this.settingTwo = other.settingTwo == null ? null : other.settingTwo.copy();
        this.settingThree = other.settingThree == null ? null : other.settingThree.copy();
        this.settingFour = other.settingFour == null ? null : other.settingFour.copy();
        this.settingFive = other.settingFive == null ? null : other.settingFive.copy();
        this.settingSix = other.settingSix == null ? null : other.settingSix.copy();
        this.settingSeven = other.settingSeven == null ? null : other.settingSeven.copy();
        this.spendingListId = other.spendingListId == null ? null : other.spendingListId.copy();
        this.userPendingId = other.userPendingId == null ? null : other.userPendingId.copy();
        this.groupId = other.groupId == null ? null : other.groupId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public SettingsListCriteria copy() {
        return new SettingsListCriteria(this);
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

    public BooleanFilter getSettingOne() {
        return settingOne;
    }

    public BooleanFilter settingOne() {
        if (settingOne == null) {
            settingOne = new BooleanFilter();
        }
        return settingOne;
    }

    public void setSettingOne(BooleanFilter settingOne) {
        this.settingOne = settingOne;
    }

    public BooleanFilter getSettingTwo() {
        return settingTwo;
    }

    public BooleanFilter settingTwo() {
        if (settingTwo == null) {
            settingTwo = new BooleanFilter();
        }
        return settingTwo;
    }

    public void setSettingTwo(BooleanFilter settingTwo) {
        this.settingTwo = settingTwo;
    }

    public BooleanFilter getSettingThree() {
        return settingThree;
    }

    public BooleanFilter settingThree() {
        if (settingThree == null) {
            settingThree = new BooleanFilter();
        }
        return settingThree;
    }

    public void setSettingThree(BooleanFilter settingThree) {
        this.settingThree = settingThree;
    }

    public BooleanFilter getSettingFour() {
        return settingFour;
    }

    public BooleanFilter settingFour() {
        if (settingFour == null) {
            settingFour = new BooleanFilter();
        }
        return settingFour;
    }

    public void setSettingFour(BooleanFilter settingFour) {
        this.settingFour = settingFour;
    }

    public BooleanFilter getSettingFive() {
        return settingFive;
    }

    public BooleanFilter settingFive() {
        if (settingFive == null) {
            settingFive = new BooleanFilter();
        }
        return settingFive;
    }

    public void setSettingFive(BooleanFilter settingFive) {
        this.settingFive = settingFive;
    }

    public BooleanFilter getSettingSix() {
        return settingSix;
    }

    public BooleanFilter settingSix() {
        if (settingSix == null) {
            settingSix = new BooleanFilter();
        }
        return settingSix;
    }

    public void setSettingSix(BooleanFilter settingSix) {
        this.settingSix = settingSix;
    }

    public BooleanFilter getSettingSeven() {
        return settingSeven;
    }

    public BooleanFilter settingSeven() {
        if (settingSeven == null) {
            settingSeven = new BooleanFilter();
        }
        return settingSeven;
    }

    public void setSettingSeven(BooleanFilter settingSeven) {
        this.settingSeven = settingSeven;
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

    public LongFilter getUserPendingId() {
        return userPendingId;
    }

    public LongFilter userPendingId() {
        if (userPendingId == null) {
            userPendingId = new LongFilter();
        }
        return userPendingId;
    }

    public void setUserPendingId(LongFilter userPendingId) {
        this.userPendingId = userPendingId;
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
        final SettingsListCriteria that = (SettingsListCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(settingOne, that.settingOne) &&
            Objects.equals(settingTwo, that.settingTwo) &&
            Objects.equals(settingThree, that.settingThree) &&
            Objects.equals(settingFour, that.settingFour) &&
            Objects.equals(settingFive, that.settingFive) &&
            Objects.equals(settingSix, that.settingSix) &&
            Objects.equals(settingSeven, that.settingSeven) &&
            Objects.equals(spendingListId, that.spendingListId) &&
            Objects.equals(userPendingId, that.userPendingId) &&
            Objects.equals(groupId, that.groupId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            settingOne,
            settingTwo,
            settingThree,
            settingFour,
            settingFive,
            settingSix,
            settingSeven,
            spendingListId,
            userPendingId,
            groupId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SettingsListCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (settingOne != null ? "settingOne=" + settingOne + ", " : "") +
            (settingTwo != null ? "settingTwo=" + settingTwo + ", " : "") +
            (settingThree != null ? "settingThree=" + settingThree + ", " : "") +
            (settingFour != null ? "settingFour=" + settingFour + ", " : "") +
            (settingFive != null ? "settingFive=" + settingFive + ", " : "") +
            (settingSix != null ? "settingSix=" + settingSix + ", " : "") +
            (settingSeven != null ? "settingSeven=" + settingSeven + ", " : "") +
            (spendingListId != null ? "spendingListId=" + spendingListId + ", " : "") +
            (userPendingId != null ? "userPendingId=" + userPendingId + ", " : "") +
            (groupId != null ? "groupId=" + groupId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
