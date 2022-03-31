package com.homies.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SettingsList.
 */
@Entity
@Table(name = "settings_list")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SettingsList implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "setting_one")
    private Boolean settingOne;

    @Column(name = "setting_two")
    private Boolean settingTwo;

    @Column(name = "setting_three")
    private Boolean settingThree;

    @Column(name = "setting_four")
    private Boolean settingFour;

    @Column(name = "setting_five")
    private Boolean settingFive;

    @Column(name = "setting_six")
    private Boolean settingSix;

    @Column(name = "setting_seven")
    private Boolean settingSeven;

    @ManyToOne
    @JsonIgnoreProperties(value = { "spendings", "settingsLists", "group" }, allowSetters = true)
    private SpendingList spendingList;

    @OneToMany(mappedBy = "settingsList")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "spendingList", "spendings", "settingsList" }, allowSetters = true)
    private Set<UserPending> userPendings = new HashSet<>();

    @JsonIgnoreProperties(
        value = { "userAdmin", "taskList", "spendingList", "shoppingList", "settingsList", "userData" },
        allowSetters = true
    )
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Group group;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SettingsList id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getSettingOne() {
        return this.settingOne;
    }

    public SettingsList settingOne(Boolean settingOne) {
        this.setSettingOne(settingOne);
        return this;
    }

    public void setSettingOne(Boolean settingOne) {
        this.settingOne = settingOne;
    }

    public Boolean getSettingTwo() {
        return this.settingTwo;
    }

    public SettingsList settingTwo(Boolean settingTwo) {
        this.setSettingTwo(settingTwo);
        return this;
    }

    public void setSettingTwo(Boolean settingTwo) {
        this.settingTwo = settingTwo;
    }

    public Boolean getSettingThree() {
        return this.settingThree;
    }

    public SettingsList settingThree(Boolean settingThree) {
        this.setSettingThree(settingThree);
        return this;
    }

    public void setSettingThree(Boolean settingThree) {
        this.settingThree = settingThree;
    }

    public Boolean getSettingFour() {
        return this.settingFour;
    }

    public SettingsList settingFour(Boolean settingFour) {
        this.setSettingFour(settingFour);
        return this;
    }

    public void setSettingFour(Boolean settingFour) {
        this.settingFour = settingFour;
    }

    public Boolean getSettingFive() {
        return this.settingFive;
    }

    public SettingsList settingFive(Boolean settingFive) {
        this.setSettingFive(settingFive);
        return this;
    }

    public void setSettingFive(Boolean settingFive) {
        this.settingFive = settingFive;
    }

    public Boolean getSettingSix() {
        return this.settingSix;
    }

    public SettingsList settingSix(Boolean settingSix) {
        this.setSettingSix(settingSix);
        return this;
    }

    public void setSettingSix(Boolean settingSix) {
        this.settingSix = settingSix;
    }

    public Boolean getSettingSeven() {
        return this.settingSeven;
    }

    public SettingsList settingSeven(Boolean settingSeven) {
        this.setSettingSeven(settingSeven);
        return this;
    }

    public void setSettingSeven(Boolean settingSeven) {
        this.settingSeven = settingSeven;
    }

    public SpendingList getSpendingList() {
        return this.spendingList;
    }

    public void setSpendingList(SpendingList spendingList) {
        this.spendingList = spendingList;
    }

    public SettingsList spendingList(SpendingList spendingList) {
        this.setSpendingList(spendingList);
        return this;
    }

    public Set<UserPending> getUserPendings() {
        return this.userPendings;
    }

    public void setUserPendings(Set<UserPending> userPendings) {
        if (this.userPendings != null) {
            this.userPendings.forEach(i -> i.setSettingsList(null));
        }
        if (userPendings != null) {
            userPendings.forEach(i -> i.setSettingsList(this));
        }
        this.userPendings = userPendings;
    }

    public SettingsList userPendings(Set<UserPending> userPendings) {
        this.setUserPendings(userPendings);
        return this;
    }

    public SettingsList addUserPending(UserPending userPending) {
        this.userPendings.add(userPending);
        userPending.setSettingsList(this);
        return this;
    }

    public SettingsList removeUserPending(UserPending userPending) {
        this.userPendings.remove(userPending);
        userPending.setSettingsList(null);
        return this;
    }

    public Group getGroup() {
        return this.group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public SettingsList group(Group group) {
        this.setGroup(group);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SettingsList)) {
            return false;
        }
        return id != null && id.equals(((SettingsList) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SettingsList{" +
            "id=" + getId() +
            ", settingOne='" + getSettingOne() + "'" +
            ", settingTwo='" + getSettingTwo() + "'" +
            ", settingThree='" + getSettingThree() + "'" +
            ", settingFour='" + getSettingFour() + "'" +
            ", settingFive='" + getSettingFive() + "'" +
            ", settingSix='" + getSettingSix() + "'" +
            ", settingSeven='" + getSettingSeven() + "'" +
            "}";
    }
}
