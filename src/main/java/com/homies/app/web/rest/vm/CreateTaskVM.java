package com.homies.app.web.rest.vm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateTaskVM {

    @NotNull
    private Long id;

    @NotNull
    private Long idGroup;

    @NotNull
    @Size(min = 3, max = 50)
    private String taskName;

    @NotNull
    @Size(min = 3, max = 100)
    private String description;

    @NotNull
    @Size(min = 1, max = 50)
    private String login;

    public Long getUser() {
        return id;
    }

    public void setUser(Long id) {
        this.id = id;
    }

    public Long getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(Long idGroup) {
        this.idGroup = idGroup;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return "CreateTaskVM{" +
            "id=" + id +
            ", idGroup=" + idGroup +
            ", taskName='" + taskName + '\'' +
            ", description='" + description + '\'' +
            ", login='" + login + '\'' +
            '}';
    }
}
