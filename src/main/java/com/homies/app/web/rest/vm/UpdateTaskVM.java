package com.homies.app.web.rest.vm;

import javax.validation.constraints.Size;

public class UpdateTaskVM {

    private Long idTask;

    private Long idGroup;

    private String login;

    @Size(min = 3, max = 50)
    private String taskName;

    @Size(min = 3, max = 100)
    private String description;

    private boolean cancel;

    public Long getIdTask() {
        return idTask;
    }

    public void setIdTask(Long idTask) {
        this.idTask = idTask;
    }

    public Long getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(Long idGroup) {
        this.idGroup = idGroup;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public String toString() {
        return "UpdateTaskVM{" +
            "idTask=" + idTask +
            ", idGroup=" + idGroup +
            ", login='" + login + '\'' +
            ", taskName='" + taskName + '\'' +
            ", description='" + description + '\'' +
            ", cancel=" + cancel +
            '}';
    }
}
