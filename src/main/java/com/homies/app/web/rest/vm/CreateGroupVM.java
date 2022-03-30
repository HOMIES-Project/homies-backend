package com.homies.app.web.rest.vm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateGroupVM {

    @NotNull
    private Long user;

    @NotNull
    @Size(min = 3, max = 50)
    private String groupName;

    @NotNull
    @Size(min = 3, max = 100)
    private String groupRelation;

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
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
        return "CreateGroupVM{" +
            "user=" + user +
            ", groupName='" + groupName + '\'' +
            ", groupRelation='" + groupRelation + '\'' +
            '}';
    }
}
