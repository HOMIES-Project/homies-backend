package com.homies.app.web.rest.vm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UpdateGroupVM {

    @NotNull
    private String login;

    private Long idGroup;

    @NotNull
    @Size(min = 3, max = 50)
    private String groupName;

    @NotNull
    @Size(min = 3, max = 100)
    private String groupRelation;

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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupRelation() {
        return groupRelation;
    }

    public void setGroupRelation(String groupRelation) {
        this.groupRelation = groupRelation;
    }

    @Override
    public String toString() {
        return "UpdateGroupVM{" +
            "login='" + login + '\'' +
            ", idGroup=" + idGroup +
            ", groupName='" + groupName + '\'' +
            ", groupRelation='" + groupRelation + '\'' +
            '}';
    }
}
