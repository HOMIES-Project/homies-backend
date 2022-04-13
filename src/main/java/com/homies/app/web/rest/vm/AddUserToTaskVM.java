package com.homies.app.web.rest.vm;

import javax.validation.constraints.NotNull;

public class AddUserToTaskVM {

    private Long idTask;

    private String login;

    private Long idList;

    public Long getIdTask() {
        return idTask;
    }

    public void setIdTask(Long id) {
        this.idTask = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Long getIdList() {
        return idList;
    }

    public void setIdList(Long idList) {
        this.idList = idList;
    }

    @Override
    public String toString() {
        return "AddUserToTaskVM{" +
            "id=" + idTask +
            ", login='" + login + '\'' +
            ", idList=" + idList +
            '}';
    }
}
