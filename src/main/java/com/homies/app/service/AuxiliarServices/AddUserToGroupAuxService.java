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

import java.util.Optional;

@Service
public class AddUserToGroupAuxService {

    private final Logger log = LoggerFactory.getLogger(AddUserToGroupAuxService.class);

    private final UserService userService;

    private final UserDataService userDataService;

    private final GroupService groupService;

    public AddUserToGroupAuxService(UserService userService,
                                    GroupService groupService,
                                    UserDataService userDataService) {
        this.userService = userService;
        this.groupService = groupService;
        this.userDataService = userDataService;
    }

    private Optional<UserData> userAdmin;
    private Optional<Group> group;
    private Optional<UserData> userData;

    public Optional<Group> addUserToGroup(AddUserToGroupVM addUser) {
        if (manageUserOfTheGroup(addUser).isPresent()) {
            userData.get().addGroup(group.get());
            userDataService.save(userData.get());
            return group;
        }
        return Optional.empty();
    }

    public Optional<Group> deleteUserToTheGroup(AddUserToGroupVM addUser) {
        if (manageUserOfTheGroup(addUser).isPresent()) {
            userData.get().removeGroup(group.get());
            userDataService.save(userData.get());
            return group;
        }
        return Optional.empty();
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
        userAdmin = userDataService.findOne(addUser.getIdAdminGroup());
        group = groupService.findOne(addUser.getIdGroup());
        Optional<User> user = userService.getUser(addUser.getLogin());
        userData = userDataService.findOne(user.get().getId());

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
    
}
