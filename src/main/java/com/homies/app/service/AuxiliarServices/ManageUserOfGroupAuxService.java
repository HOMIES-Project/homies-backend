package com.homies.app.service.AuxiliarServices;

import com.homies.app.domain.Group;
import com.homies.app.domain.UserData;
import com.homies.app.service.*;
import com.homies.app.web.rest.errors.Group.GroupNotExistException;
import com.homies.app.web.rest.errors.User.UserDoesNotExist;
import com.homies.app.web.rest.vm.AddUserToGroupVM;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.*;

@Service
public class ManageUserOfGroupAuxService {

    private final Logger log = LoggerFactory.getLogger(ManageUserOfGroupAuxService.class);

    private final UserDataService userDataService;

    private final UserDataQueryService userDataQueryService;

    private final GroupService groupService;

    private final GroupQueryService groupQueryService;

    public ManageUserOfGroupAuxService(
        UserDataService userDataService,
        UserDataQueryService userDataQueryService,
        GroupService groupService,
        GroupQueryService groupQueryService
    ) {
        this.userDataService = userDataService;
        this.userDataQueryService = userDataQueryService;
        this.groupService = groupService;
        this.groupQueryService = groupQueryService;
    }

    private Optional<UserData> userAdmin;
    private Optional<Group> group;
    private Optional<UserData> userData;

    public Optional<Group> addUserToGroup(AddUserToGroupVM addUser) throws UserPrincipalNotFoundException {
        if (manageUserOfTheGroup(addUser, false).isPresent()) {

            if (groupQueryService.findGroupByIdAndUserDataUserLogin(
                addUser.getIdGroup(),
                addUser.getLogin()).isPresent())
                throw new UsernameAlreadyUsedException();

            group.get().addUserData(userData.get());
            groupService.save(group.get());
            refreshEntities();
            return groupService.findOne(group.get().getId());
        }
        return Optional.empty();

    }

    public Optional<Group> deleteUserToTheGroup(AddUserToGroupVM addUser) throws UserPrincipalNotFoundException {

        boolean userOrAdmin = addUser.getIdAdminGroup() == null;
        if (manageUserOfTheGroup(addUser, userOrAdmin).isPresent()) {
            if (groupQueryService.findGroupByIdAndUserDataUserLogin(
                                    addUser.getIdGroup(),
                                    addUser.getLogin())
                                    .isEmpty())
                throw new UsernameNotFoundException("No existe el usuario en este equipo");

            deleteUser();
            return groupService.findOne(group.get().getId());

        }
        return Optional.empty();

    }

    public void deleteUserAllGroups(Long id) {
        userData = userDataService.findOne(id);
        if (userData.isEmpty())
            throw new UserDoesNotExist();

        //Detach user of her groups
        List<Group> useGroups = groupQueryService.getUseGroupsByUserDataId(userData.get().getId());

        userData.get().setGroups(new HashSet<>());
        userDataService.save(userData.get());

        useGroups.forEach(useGroup -> {
            useGroup.removeUserData(userData.get());
            groupService.save(useGroup);
        });
        refreshEntities();

        //Detach user of her admin groups
        List<Group> adminGroups = groupQueryService.getAdminGroupsByUserDataId(userData.get().getId());

        userData.get().setAdminGroups(new HashSet<>());
        userDataService.save(userData.get());

        adminGroups.forEach(adminGroup -> {
            if (adminGroup.getUserData().size() > 0) {
                adminGroup.setUserAdmin(adminGroup.getUserData().iterator().next());
                groupService.save(adminGroup);
            } else {
               adminGroup.setUserAdmin(null);
               groupService.save(adminGroup);
               groupService.delete(adminGroup.getId());
            }
        });
        refreshEntities();

    }

    public Optional<Group> changeUserAdminOfTheGroup(AddUserToGroupVM addUser) throws UserPrincipalNotFoundException {
        if (manageUserOfTheGroup(addUser, false).isPresent()) {

            if (groupQueryService.findGroupByIdAndUserDataUserLogin(
                addUser.getIdGroup(),
                addUser.getLogin()).isEmpty())
                throw new UserDoesNotExist();

            group.get().setUserAdmin(userData.get());
            groupService.save(group.get());

            userAdmin.get().removeAdminGroups(group.get());
            userDataService.save(userAdmin.get());

            userData.get().addAdminGroups(group.get());
            userDataService.save(userData.get());

            return groupService.findOne(group.get().getId());
        }
        return Optional.empty();
    }

    private Optional<Group> manageUserOfTheGroup(@NotNull AddUserToGroupVM addUser, boolean userExit) throws UserPrincipalNotFoundException {
        userAdmin = userDataService.findOne(addUser.getIdAdminGroup());
        group = groupService.findOne(addUser.getIdGroup());
        userData = userDataQueryService.getByUser_Login(addUser.getLogin());

        if (!userExit) {
            if (userAdmin.isEmpty())
                throw new UserPrincipalNotFoundException("Don't exist this admin"); //UserAdmin not exist

            if (userAdmin.get().getId().longValue() != group.get().getUserAdmin().getId().longValue())
                throw new UserPrincipalNotFoundException("UserAdmin isn't admin of this group");  //UserAdmin not is group's userAdmin.
        }

        if (userData.isEmpty())
            throw new UserDoesNotExist(); //UserData not exist

        if (group.isEmpty())
            throw new GroupNotExistException(); //Group not exist

        if (group.get().getUserAdmin() == null)
            throw new UserPrincipalNotFoundException(addUser.getIdAdminGroup().toString()); //Group's UserAdmin not exist

        return group;

    }

    private void deleteUser() {
        try {
            userData.get().removeGroup(group.get());
            userDataService.save(userData.get());

            group.get().removeUserData(userData.get());
            groupService.save(group.get());

            if (userData.get().getId().longValue() == group.get().getUserAdmin().getId().longValue()) {

                group.get().setUserAdmin(null);
                groupService.save(group.get());

                userData.get().removeAdminGroups(group.get());
                userDataService.partialUpdate(userData.get());

                Set<UserData> userDataSet = group.get().getUserData();

                if (userDataSet.size() > 1) {

                    userData = userDataService.findOne(userDataSet.iterator().next().getId());

                    group.get().setUserAdmin(userData.get());
                    groupService.save(group.get());

                    userData.get().addAdminGroups(group.get());
                    userDataService.save(userData.get());

                } else {
                    groupService.delete(group.get().getId());

                }
            }
        } catch (Exception e){
            e.printStackTrace();

        } finally {
            refreshEntities();

        }

    }


    private void refreshEntities() {
        groupQueryService.refreshGroupEntity();
        userDataQueryService.refreshUserDataEntity();
    }

}
