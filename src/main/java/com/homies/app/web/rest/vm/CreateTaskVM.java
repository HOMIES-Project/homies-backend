package com.homies.app.web.rest.vm;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateTaskVM {

    @NotNull
    private Long user;

    @NotNull
    private Long idGroup;

    @NotNull
    @Size(min = 3, max = 50)
    private String taskName;

    @NotNull
    @Size(min = 3, max = 100)
    private String description;

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
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

    @Override
    public String toString() {
        return "CreateTaskVM{" +
            "user=" + user +
            ", idGroup=" + idGroup +
            ", taskName='" + taskName + '\'' +
            ", description='" + description + '\'' +
            '}';
    }
}
