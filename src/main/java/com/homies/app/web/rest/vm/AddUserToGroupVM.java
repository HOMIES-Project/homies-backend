package com.homies.app.web.rest.vm;

public class AddUserToGroupVM {

    private Long IdAdminGroup;

    private String login;

    private Long idGroup;

    public Long getIdAdminGroup() {
        return IdAdminGroup;
    }

    public void setIdAdminGroup(Long idAdminGroup) {
        IdAdminGroup = idAdminGroup;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Long getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(Long idGroup) {
        this.idGroup = idGroup;
    }

    @Override
    public String toString() {
        return "AddUserToGroupVM{" +
            "IdAdminGroup=" + IdAdminGroup +
            ", login='" + login + '\'' +
            ", idGroup=" + idGroup +
            '}';
    }
}
