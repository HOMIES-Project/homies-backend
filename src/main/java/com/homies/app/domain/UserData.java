package com.homies.app.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * A UserData.
 */
@Entity
@Table(name = "user_data")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "photo")
    private byte[] photo;

    @Column(name = "photo_content_type")
    private String photoContentType;

    @Size(min = 6, max = 50)
    @Column(name = "phone", length = 50)
    private String phone;

    @NotNull
    @Column(name = "premium", nullable = false)
    private Boolean premium;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "add_date")
    private LocalDate addDate;

    @OneToOne(cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @OneToMany(mappedBy = "userAdmin", fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(
        value = { "userAdmin", "taskList", "spendingList", "shoppingList", "settingsList", "userData" },
        allowSetters = true
    )
    @NotFound(action = NotFoundAction.IGNORE)
    private Set<Group> adminGroups = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
        name = "rel_user_data__task_asigned",
        joinColumns = @JoinColumn(name = "user_data_id"),
        inverseJoinColumns = @JoinColumn(name = "task_asigned_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "taskList", "userData", "userCreator", "userAssigneds" }, allowSetters = true)
    @NotFound(action = NotFoundAction.IGNORE)
    private Set<Task> taskAsigneds = new HashSet<>();

    @OneToMany(mappedBy = "userCreator", fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "userCreator", "shoppingList" }, allowSetters = true)
    private Set<Products> productCreateds = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
        name = "rel_user_data__group",
        joinColumns = @JoinColumn(name = "user_data_id"),
        inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(
        value = { "userAdmin", "taskList", "spendingList", "shoppingList", "settingsList", "userData" },
        allowSetters = true
    )
    @NotFound(action = NotFoundAction.IGNORE)
    private Set<Group> groups = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserData id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getPhoto() {
        return this.photo;
    }

    public UserData photo(byte[] photo) {
        this.setPhoto(photo);
        return this;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getPhotoContentType() {
        return this.photoContentType;
    }

    public UserData photoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
        return this;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }

    public String getPhone() {
        return this.phone;
    }

    public UserData phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getPremium() {
        return this.premium;
    }

    public UserData premium(Boolean premium) {
        this.setPremium(premium);
        return this;
    }

    public void setPremium(Boolean premium) {
        this.premium = premium;
    }

    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    public UserData birthDate(LocalDate birthDate) {
        this.setBirthDate(birthDate);
        return this;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDate getAddDate() {
        return this.addDate;
    }

    public UserData addDate(LocalDate addDate) {
        this.setAddDate(addDate);
        return this;
    }

    public void setAddDate(LocalDate addDate) {
        this.addDate = addDate;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserData user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Group> getAdminGroups() {
        return this.adminGroups;
    }

    public void setAdminGroups(Set<Group> groups) {
        if (this.adminGroups != null) {
            this.adminGroups.forEach(i -> i.setUserAdmin(null));
        }
        if (groups != null) {
            groups.forEach(i -> i.setUserAdmin(this));
        }
        this.adminGroups = groups;
    }

    public UserData adminGroups(Set<Group> groups) {
        this.setAdminGroups(groups);
        return this;
    }

    public UserData addAdminGroups(Group group) {
        this.adminGroups.add(group);
        group.setUserAdmin(this);
        return this;
    }

    public UserData removeAdminGroups(Group group) {
        this.adminGroups.remove(group);
        group.setUserAdmin(null);
        return this;
    }

    public Set<Task> getTaskAsigneds() {
        return this.taskAsigneds;
    }

    public void setTaskAsigneds(Set<Task> tasks) {
        this.taskAsigneds = tasks;
    }

    public UserData taskAsigneds(Set<Task> tasks) {
        this.setTaskAsigneds(tasks);
        return this;
    }

    public UserData addTaskAsigned(Task task) {
        this.taskAsigneds.add(task);
        task.getUserAssigneds().add(this);
        return this;
    }

    public UserData removeTaskAsigned(Task task) {
        this.taskAsigneds.remove(task);
        task.getUserAssigneds().remove(this);
        return this;
    }

    public Set<Products> getProductCreateds() {
        return this.productCreateds;
    }

    public void setProductCreateds(Set<Products> products) {
        if (this.productCreateds != null) {
            this.productCreateds.forEach(i -> i.setUserCreator(null));
        }
        if (products != null) {
            products.forEach(i -> i.setUserCreator(this));
        }
        this.productCreateds = products;
    }

    public UserData productCreateds(Set<Products> products) {
        this.setProductCreateds(products);
        return this;
    }

    public UserData addProductCreated(Products products) {
        this.productCreateds.add(products);
        products.setUserCreator(this);
        return this;
    }

    public UserData removeProductCreated(Products products) {
        this.productCreateds.remove(products);
        products.setUserCreator(null);
        return this;
    }

    public Set<Group> getGroups() {
        return this.groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public UserData groups(Set<Group> groups) {
        this.setGroups(groups);
        return this;
    }

    public UserData addGroup(Group group) {
        this.groups.add(group);
        group.getUserData().add(this);
        return this;
    }

    public UserData removeGroup(Group group) {
        this.groups.remove(group);
        group.getUserData().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserData)) {
            return false;
        }
        return id != null && id.equals(((UserData) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "UserData{" +
            "id=" + getId() +
            ", photo='" + getPhoto() + "'" +
            ", photoContentType='" + getPhotoContentType() + "'" +
            ", phone='" + getPhone() + "'" +
            ", premium='" + getPremium() + "'" +
            ", birthDate='" + getBirthDate() + "'" +
            ", addDate='" + getAddDate() + "'" +
            //", user=" + getUser() +
            //", adminGroups=" + getAdminGroups() +
            //", taskAsigneds=" + getTaskAsigneds() +
            //", productCreateds=" + getProductCreateds() +
            //", groups=" + getGroups() +
            "}";
    }

}
