package com.homies.app.service.AuxiliarServices;

import com.homies.app.domain.Group;
import com.homies.app.domain.User;
import com.homies.app.domain.UserData;
import com.homies.app.service.GroupQueryService;
import com.homies.app.service.GroupService;
import com.homies.app.service.UserDataService;
import com.homies.app.service.UserService;
import com.homies.app.web.rest.vm.AddUserToGroupVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ManageUserOfGroupAuxServiceCopy {

    private final Logger log = LoggerFactory.getLogger(ManageUserOfGroupAuxServiceCopy.class);

    private final UserService userService;

    private final UserDataService userDataService;

    private final GroupService groupService;

    private final GroupQueryService groupQueryService;

    public ManageUserOfGroupAuxServiceCopy(UserService userService,
                                           GroupService groupService,
                                           UserDataService userDataService,
                                           GroupQueryService groupQueryService) {
        this.userService = userService;
        this.groupService = groupService;
        this.userDataService = userDataService;
        this.groupQueryService = groupQueryService;
    }

    private Optional<Group> group;
    private Optional<UserData> userData;

    public Optional<Group> addUserToGroup(AddUserToGroupVM addUser) {
        if (manageUserOfTheGroup(addUser).isPresent()) {
            //in MtM relationships, the relationship with each other must be maintained in both entities.
            userData.get().addGroup(group.get());
            userDataService.save(userData.get());
            group.get().addUserData(userData.get());
            groupService.save(group.get());
            return group;
        }
        return Optional.empty();
    }

    public Optional<Group> deleteUserToTheGroup(AddUserToGroupVM addUser) {
        if (manageUserOfTheGroup(addUser).isPresent()) {
            deleteUserAdmin(group.get());

/*            userData.get().removeGroup(group.get());
            if (userData.get().getId().longValue() == group.get().getUserAdmin().getId().longValue()) {
                Set<UserData> userDataSet = group.get().getUserData();
                if (userDataSet.size() > 0) {
                    group.get().setUserAdmin(userDataSet.iterator().next());
                    groupService.save(group.get());
                }
            }*/

            userDataService.save(userData.get());
            return group;
        }
        return Optional.empty();
    }
    private void deleteUserAdmin(Group group) {
        userData.get().removeGroup(group);
        if (userData.get().getId().longValue() == group.getUserAdmin().getId().longValue()) {
            Set<UserData> userDataSet = group.getUserData();
            if (userDataSet.size() > 0) {
                group.setUserAdmin(userDataSet.iterator().next());
                groupService.save(group);
            } else {
                group.setUserAdmin(null);
                groupService.delete(group.getId()); //Revisar, hace falta eliminar las listas? se puede hacer con cascade.
            }
        } else {
            groupService.save(group);
        }
    }

    public boolean deleteUserAllGroups(Long id) {
        try {
            userData = findUserGroupById(id);
            //Recuperar en que grupos está el usuario
            List<Group> userGroups = groupQueryService
                .getAllGroupsUserId(userData.get().getId(),
                    userData.get().getId());

            //Iterar sobre cada grupo para eliminarlo
            for (Group group: userGroups) {
                deleteUserAdmin(group);
            }

            //eliminar datos del usuario sobre grupos y propiedad de grupos
            userData.get().setGroups(null);
            userData.get().setAdminGroups(null);

            //eliminar usuario
            userDataService.delete(id);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Optional<Group> changeUserAdminOfTheGroup(AddUserToGroupVM addUser){
        if (manageUserOfTheGroup(addUser).isPresent()) {
            group.get().setUserAdmin(userData.get());
            groupService.save(group.get());
            return group;
        }
        return Optional.empty();
    }

    private Optional<Group> manageUserOfTheGroup(AddUserToGroupVM addUser) {
        Optional<UserData> userAdmin = userDataService.findOne(addUser.getIdAdminGroup());
        group = groupService.findOne(addUser.getIdGroup());
        Optional<User> user = userService.getUser(addUser.getLogin());
        userData = userDataService.findOne(user.get().getId());

        /*//Optional<UserData> userAdmin = findUserAdmin(addUser.getIdAdminGroup());
        group = findGroup(addUser.getIdGroup());
        userData = findUserGroupByLogin(addUser.getLogin());*/

        if (userAdmin.isEmpty())
            return Optional.empty(); //UserAdmin not exist

        if (group.isEmpty())
            return Optional.empty(); //Group not exist

        if (group.get().getUserAdmin() == null)
            return Optional.empty(); //Group's UserAdmin not exist

        if (userData.isEmpty())
            return Optional.empty(); //UserData not exist

        if (userAdmin.get().getId().longValue() != group.get().getUserAdmin().getId().longValue())
            return Optional.empty(); //UserAdmin not is group's userAdmin.

        return group;

    }

/*    private Optional<UserData> findUserAdmin(Long id) {
        return userDataService.findOne(id);
    }

    private Optional<Group> findGroup(Long id) {
        return groupService.findOne(id);
    }

    private Optional<UserData> findUserGroupByLogin(String login) {
        *//*        Optional<User> user = userService.getUser(addUser.getLogin());
        userData = userDataService.findOne(user.get().getId());*//*
        Optional<User> user = userService.getUser(login);
        return userDataService.findOne(user.get().getId());
    }*/

    private Optional<UserData> findUserGroupById(Long id) {
        return userDataService.findOne(id);
    }

}
