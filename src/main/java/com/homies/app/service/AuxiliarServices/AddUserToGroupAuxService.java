package com.homies.app.service.AuxiliarServices;

import com.homies.app.domain.Group;
import com.homies.app.domain.User;
import com.homies.app.domain.UserData;
import com.homies.app.service.GroupQueryService;
import com.homies.app.service.GroupService;
import com.homies.app.service.UserDataService;
import com.homies.app.service.UserService;
import com.homies.app.service.impl.UserDataServiceImpl;
import com.homies.app.web.rest.vm.AddUserToGroupVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.xml.crypto.URIReferenceException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

@Service
public class AddUserToGroupAuxService {

    private final Logger log = LoggerFactory.getLogger(AddUserToGroupAuxService.class);

    private final UserService userService;

    private final UserDataService userDataService;

    private final GroupService groupService;

    private final GroupQueryService groupQueryService;

    public AddUserToGroupAuxService(UserService userService,
                                    GroupService groupService,
                                    GroupQueryService groupQueryService,
                                    UserDataService userDataService) {
        this.userService = userService;
        this.groupService = groupService;
        this.groupQueryService = groupQueryService;
        this.userDataService = userDataService;
    }

    public Optional<Group> addUserToGroup(AddUserToGroupVM addUser) {
        Optional<UserData> userAdmin = userDataService.findOne(addUser.getIdAdminGroup());
        Optional<Group> group = groupService.findOne(addUser.getIdGroup());
        Optional<User> user = userService.getUser(addUser.getLogin());
        Optional<UserData> userData = userDataService.findOne(user.get().getId());

        if (userAdmin.isEmpty())
            return Optional.empty(); //UserAdmin not exist
        log.warn("##### => " + userAdmin);

        if (group.isEmpty())
            return Optional.empty(); //Group not exist

        if (group.get().getUserAdmin() == null)
            return Optional.empty(); //Group's UserAdmin not exist
        log.warn("##### => " + group);

        if (userData.isEmpty())
            return Optional.empty(); //UserData not exist
        log.warn("##### => " + userData);

        if (userAdmin.get().getId().longValue() != group.get().getUserAdmin().getId().longValue())
            return Optional.empty(); //UserAdmin not is group's userAdmin.
        log.warn("##### => " + true);

        userData.get().addGroup(group.get());
        userDataService.save(userData.get());
        
        return group;

    }

}
