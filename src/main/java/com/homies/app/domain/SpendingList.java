package com.homies.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SpendingList.
 */
@Entity
@Table(name = "spending_list")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SpendingList implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private Long id;

    @DecimalMin(value = "0")
    @Column(name = "total")
    private Float total;

    @NotNull
    @Size(min = 3, max = 20)
    @Column(name = "name_spend_list", length = 20, nullable = false)
    private String nameSpendList;

    @OneToMany(mappedBy = "spendingList")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "spendingList", "spendings", "settingsList" }, allowSetters = true)
    private Set<UserPending> spendings = new HashSet<>();

    @OneToMany(mappedBy = "spendingList")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "spendingList", "userPendings", "group" }, allowSetters = true)
    private Set<SettingsList> settingsLists = new HashSet<>();

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

    public SpendingList id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getTotal() {
        return this.total;
    }

    public SpendingList total(Float total) {
        this.setTotal(total);
        return this;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public String getNameSpendList() {
        return this.nameSpendList;
    }

    public SpendingList nameSpendList(String nameSpendList) {
        this.setNameSpendList(nameSpendList);
        return this;
    }

    public void setNameSpendList(String nameSpendList) {
        this.nameSpendList = nameSpendList;
    }

    public Set<UserPending> getSpendings() {
        return this.spendings;
    }

    public void setSpendings(Set<UserPending> userPendings) {
        if (this.spendings != null) {
            this.spendings.forEach(i -> i.setSpendingList(null));
        }
        if (userPendings != null) {
            userPendings.forEach(i -> i.setSpendingList(this));
        }
        this.spendings = userPendings;
    }

    public SpendingList spendings(Set<UserPending> userPendings) {
        this.setSpendings(userPendings);
        return this;
    }

    public SpendingList addSpending(UserPending userPending) {
        this.spendings.add(userPending);
        userPending.setSpendingList(this);
        return this;
    }

    public SpendingList removeSpending(UserPending userPending) {
        this.spendings.remove(userPending);
        userPending.setSpendingList(null);
        return this;
    }

    public Set<SettingsList> getSettingsLists() {
        return this.settingsLists;
    }

    public void setSettingsLists(Set<SettingsList> settingsLists) {
        if (this.settingsLists != null) {
            this.settingsLists.forEach(i -> i.setSpendingList(null));
        }
        if (settingsLists != null) {
            settingsLists.forEach(i -> i.setSpendingList(this));
        }
        this.settingsLists = settingsLists;
    }

    public SpendingList settingsLists(Set<SettingsList> settingsLists) {
        this.setSettingsLists(settingsLists);
        return this;
    }

    public SpendingList addSettingsList(SettingsList settingsList) {
        this.settingsLists.add(settingsList);
        settingsList.setSpendingList(this);
        return this;
    }

    public SpendingList removeSettingsList(SettingsList settingsList) {
        this.settingsLists.remove(settingsList);
        settingsList.setSpendingList(null);
        return this;
    }

    public Group getGroup() {
        return this.group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public SpendingList group(Group group) {
        this.setGroup(group);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpendingList)) {
            return false;
        }
        return id != null && id.equals(((SpendingList) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SpendingList{" +
            "id=" + getId() +
            ", total=" + getTotal() +
            ", nameSpendList='" + getNameSpendList() + "'" +
            "}";
    }
}
