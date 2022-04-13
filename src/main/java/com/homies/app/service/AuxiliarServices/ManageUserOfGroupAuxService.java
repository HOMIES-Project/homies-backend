package com.homies.app.service.AuxiliarServices;

import com.homies.app.domain.Group;
import com.homies.app.domain.User;
import com.homies.app.domain.UserData;
import com.homies.app.service.*;
import com.homies.app.web.rest.errors.BadRequestAlertException;
import com.homies.app.web.rest.errors.Group.GroupNotExistException;
import com.homies.app.web.rest.errors.User.UserDoesNotExist;
import com.homies.app.web.rest.vm.AddUserToGroupVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ManageUserOfGroupAuxService {

    private final Logger log = LoggerFactory.getLogger(ManageUserOfGroupAuxService.class);

    private final UserService userService;

    private final UserDataService userDataService;

    private final GroupService groupService;

    private final GroupQueryService groupQueryService;

    public ManageUserOfGroupAuxService(UserService userService,
                                       GroupService groupService,
                                       UserDataService userDataService,
                                       GroupQueryService groupQueryService) {
        this.userService = userService;
        this.groupService = groupService;
        this.userDataService = userDataService;
        this.groupQueryService = groupQueryService;
    }
    private Optional<UserData> userAdmin;
    private Optional<Group> group;
    private Optional<UserData> userData;

    public Optional<Group> addUserToGroup(AddUserToGroupVM addUser) throws UserPrincipalNotFoundException {
        if (manageUserOfTheGroup(addUser).isPresent()) {

            if (groupQueryService.findByIdAndUserDataUserLogin(
                addUser.getIdGroup(),
                addUser.getLogin()).isPresent())
                throw new UsernameAlreadyUsedException();

            //in MtM relationships, the relationship with each other must be maintained in both entities.
            userData.get().addGroup(group.get());
            userDataService.save(userData.get());
            group.get().addUserData(userData.get());
            groupService.save(group.get());
            return groupService.findOne(group.get().getId());
        }
        return Optional.empty();
    }

    public Optional<Group> deleteUserToTheGroup(AddUserToGroupVM addUser) throws UserPrincipalNotFoundException {
        if (manageUserOfTheGroup(addUser).isPresent()) {
            if (groupQueryService.findByIdAndUserDataUserLogin(
                addUser.getIdGroup(),
                addUser.getLogin()).isEmpty())
                throw new UserDoesNotExist();
            deleteUser();
            return groupService.findOne(group.get().getId());
        }
        return Optional.empty();
    }

    private void deleteUser() {

        //############### Esto hay que mejorarlo

        //Se elimina el grupo de la relación en userData
        userData.get().removeGroup(group.get());
        userDataService.save(userData.get());

        //Se elimina al usuario de la reación de group
        group.get().removeUserData(userData.get());
        groupService.save(group.get());

        //Si el usuario que se ha eliminado es el mismo que el admin del grupo
        if (userData.get().getId().longValue() == group.get().getUserAdmin().getId().longValue()) {

            //Se elimina al admin de la relación de group
            group.get().setUserAdmin(null);
            groupService.save(group.get());

            //Se elimina al grupo de la relación de userAdmin
            userData.get().removeAdminGroups(group.get());
            userDataService.partialUpdate(userData.get());

            //se carga el set de usuarios del grupo
            Set<UserData> userDataSet = group.get().getUserData();

            //Si hay más usuarios en el grupo
            if (userDataSet.size() > 1) {

                //Se caga el primer user que haya en la lista
                userData = userDataService.findOne(userDataSet.iterator().next().getId());

                //Se guarda en el grupo al nuevo userAdmin
                group.get().setUserAdmin(userData.get());
                groupService.save(group.get());

                //Se le carga el grupo que va a administrar y se guarda
                userData.get().addAdminGroups(group.get());
                userDataService.save(userData.get());

            } else {
                //Se elimina el grupo
                groupService.delete(group.get().getId());

            }
        }
    }

    public void deleteUserAllGroups(Long id) throws Exception {
        if (userDataService.findOne(id).isEmpty())
            throw new UserDoesNotExist();

        try {
            userData = userDataService.findOne(id);

            //Request the groups the user is in.
            List<Group> userGroups = groupQueryService
                .getAllGroupsUserId(userData.get().getId(),
                    userData.get().getId());

            //Delete relationships of user
            userData.get().setGroups(new HashSet<>());
            userData.get().setAdminGroups(new HashSet<>());
            userDataService.save(userData.get());

            for (Group userGroup : userGroups) {

                if (userData.get().getId() == userGroup.getUserAdmin().getId()) {

                    //Delete user of group
                    userGroup.removeUserData(userData.get());
                    groupService.save(userGroup);

                    //Request users
                    Set<UserData> userDataSet = userGroup.getUserData();

                    if (userDataSet.size() > 0) {

                        //New group in set adminGroups
                        Optional<UserData> newAdmin = userDataService.
                            findOne(userDataSet.iterator().next().getId());
                        newAdmin.get().addAdminGroups(userGroup);
                        userDataService.save(newAdmin.get());

                        //New user admin for group
                        userGroup.setUserAdmin(newAdmin.get());
                        groupService.save(userGroup);

                    } else {
                        groupService.delete(userGroup.getId());

                    }
                }
            }

        } catch (Exception e) {
            throw new Exception();

        }
    }

    public Optional<Group> changeUserAdminOfTheGroup(AddUserToGroupVM addUser) throws UserPrincipalNotFoundException {
        if (manageUserOfTheGroup(addUser).isPresent()) {

            if (groupQueryService.findByIdAndUserDataUserLogin(
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

    private Optional<Group> manageUserOfTheGroup(AddUserToGroupVM addUser) throws UserPrincipalNotFoundException {
        userAdmin = userDataService.findOne(addUser.getIdAdminGroup());
        group = groupService.findOne(addUser.getIdGroup());
        Optional<User> user = userService.getUser(addUser.getLogin());
        userData = userDataService.findOne(user.get().getId());

        if (userAdmin.isEmpty())
            throw new UserPrincipalNotFoundException("Don't exist this admin"); //UserAdmin not exist

        if (userData.isEmpty())
            throw new UserDoesNotExist(); //UserData not exist

        if (userAdmin.get().getId().longValue() != group.get().getUserAdmin().getId().longValue())
            throw new UserPrincipalNotFoundException("UserAdmin isn't admin of this group");  //UserAdmin not is group's userAdmin.

        if (group.isEmpty())
            throw new GroupNotExistException(); //Group not exist

        if (group.get().getUserAdmin() == null)
            throw new UserPrincipalNotFoundException(addUser.getIdAdminGroup().toString()); //Group's UserAdmin not exist

        return group;

    }

}
