package com.homies.app.web.rest.vm;

public class GetGroupTaskListVM {

    private Long idGroup;

    private String login;

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

    @Override
    public String toString() {
        return "GetGroupTaskListVM{" +
            "idGroup=" + idGroup +
            ", login='" + login + '\'' +
            '}';
    }
}
