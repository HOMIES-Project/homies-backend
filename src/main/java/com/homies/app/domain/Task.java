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

/**
 * A Task.
 */
@Entity
@Table(name = "task")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(name = "task_name", length = 50, nullable = false)
    private String taskName;

    @Column(name = "data_create")
    private LocalDate dataCreate;

    @Column(name = "data_end")
    private LocalDate dataEnd;

    @NotNull
    @Size(min = 3, max = 100)
    @Column(name = "description", length = 100, nullable = false)
    private String description;

    @Column(name = "cancel")
    private Boolean cancel;

    @Lob
    @Column(name = "photo")
    private byte[] photo;

    @Column(name = "photo_content_type")
    private String photoContentType;

    @Column(name = "puntuacion")
    private String puntuacion;

    @ManyToOne
    @JsonIgnoreProperties(value = { "group", "tasks" }, allowSetters = true)
    private TaskList taskList;

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "adminGroups", "taskAsigneds", "productCreateds", "groups" }, allowSetters = true)
    private UserData userData;

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "adminGroups", "taskAsigneds", "productCreateds", "groups" }, allowSetters = true)
    private UserData userCreator;

    @ManyToMany(mappedBy = "taskAsigneds", fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "user", "adminGroups", "taskAsigneds", "productCreateds", "groups" }, allowSetters = true)
    private Set<UserData> userAssigneds = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Task id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public Task taskName(String taskName) {
        this.setTaskName(taskName);
        return this;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public LocalDate getDataCreate() {
        return this.dataCreate;
    }

    public Task dataCreate(LocalDate dataCreate) {
        this.setDataCreate(dataCreate);
        return this;
    }

    public void setDataCreate(LocalDate dataCreate) {
        this.dataCreate = dataCreate;
    }

    public LocalDate getDataEnd() {
        return this.dataEnd;
    }

    public Task dataEnd(LocalDate dataEnd) {
        this.setDataEnd(dataEnd);
        return this;
    }

    public void setDataEnd(LocalDate dataEnd) {
        this.dataEnd = dataEnd;
    }

    public String getDescription() {
        return this.description;
    }

    public Task description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getCancel() {
        return this.cancel;
    }

    public Task cancel(Boolean cancel) {
        this.setCancel(cancel);
        return this;
    }

    public void setCancel(Boolean cancel) {
        this.cancel = cancel;
    }

    public byte[] getPhoto() {
        return this.photo;
    }

    public Task photo(byte[] photo) {
        this.setPhoto(photo);
        return this;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getPhotoContentType() {
        return this.photoContentType;
    }

    public Task photoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
        return this;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }

    public String getPuntuacion() {
        return this.puntuacion;
    }

    public Task puntuacion(String puntuacion) {
        this.setPuntuacion(puntuacion);
        return this;
    }

    public void setPuntuacion(String puntuacion) {
        this.puntuacion = puntuacion;
    }

    public TaskList getTaskList() {
        return this.taskList;
    }

    public void setTaskList(TaskList taskList) {
        this.taskList = taskList;
    }

    public Task taskList(TaskList taskList) {
        this.setTaskList(taskList);
        return this;
    }

    public UserData getUserData() {
        return this.userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public Task userData(UserData userData) {
        this.setUserData(userData);
        return this;
    }

    public UserData getUserCreator() {
        return this.userCreator;
    }

    public void setUserCreator(UserData userData) {
        this.userCreator = userData;
    }

    public Task userCreator(UserData userData) {
        this.setUserCreator(userData);
        return this;
    }

    public Set<UserData> getUserAssigneds() {
        return this.userAssigneds;
    }

    public void setUserAssigneds(Set<UserData> userData) {
        if (this.userAssigneds != null) {
            this.userAssigneds.forEach(i -> i.removeTaskAsigned(this));
        }
        if (userData != null) {
            userData.forEach(i -> i.addTaskAsigned(this));
        }
        this.userAssigneds = userData;
    }

    public Task userAssigneds(Set<UserData> userData) {
        this.setUserAssigneds(userData);
        return this;
    }

    public Task addUserAssigned(UserData userData) {
        this.userAssigneds.add(userData);
        userData.getTaskAsigneds().add(this);
        return this;
    }

    public Task removeUserAssigned(UserData userData) {
        this.userAssigneds.remove(userData);
        userData.getTaskAsigneds().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task)) {
            return false;
        }
        return id != null && id.equals(((Task) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Task{" +
            "id=" + getId() +
            ", taskName='" + getTaskName() + "'" +
            ", dataCreate='" + getDataCreate() + "'" +
            ", dataEnd='" + getDataEnd() + "'" +
            ", description='" + getDescription() + "'" +
            ", cancel='" + getCancel() + "'" +
            ", photo='" + getPhoto() + "'" +
            ", photoContentType='" + getPhotoContentType() + "'" +
            ", puntuacion='" + getPuntuacion() + "'" +
            "}";
    }
}
