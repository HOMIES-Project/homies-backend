package com.homies.app.service.AuxiliarServices;

import com.homies.app.domain.Group;
import com.homies.app.domain.User;
import com.homies.app.domain.UserData;
import com.homies.app.service.*;
import com.homies.app.web.rest.vm.AddUserToGroupVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

    public Optional<Group> addUserToGroup(AddUserToGroupVM addUser) {
        if (manageUserOfTheGroup(addUser).isPresent()) {
            //in MtM relationships, the relationship with each other must be maintained in both entities.
            userData.get().addGroup(group.get());
            userDataService.save(userData.get());
            group.get().addUserData(userData.get());
            groupService.save(group.get());
            return groupService.findOne(group.get().getId());
        }
        return Optional.empty();
    }

    public Optional<Group> deleteUserToTheGroup(AddUserToGroupVM addUser) {
        if (manageUserOfTheGroup(addUser).isPresent()) {
            deleteUser();
            return groupService.findOne(group.get().getId());
        }
        return Optional.empty();
    }
    private void deleteUser() {

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
            userDataService.save(userData.get());

            //se carga el set de usuarios del grupo
            Set<UserData> userDataSet = group.get().getUserData();

            //Si hay más usuarios en el grupo
            if (userDataSet.size() > 0) {

                //Se caga el primer user que haya en la lista
                userData = userDataService.findOne(userDataSet.iterator().next().getId());

                //Se guarda en el grupo al nuevo userAdmin
                group.get().setUserAdmin(userData.get());
                groupService.save(group.get());

                //Se le carga el grupo que va a administrar y se guarda
                userData.get().addAdminGroups(group.get());
                userDataService.save(userData.get());

            } else {
                log.warn("########=> Se debe eliminar al grupo");
                //Se elimina el grupo
                //Cascade.all active = delete all group lists
                groupService.delete(group.get().getId());
            }

        }
    }

    public boolean deleteUserAllGroups(Long id) {
        try {
            userData = userDataService.findOne(id);
            //Recuperar en que grupos está el usuario
            List<Group> userGroups = groupQueryService
                .getAllGroupsUserId(userData.get().getId(),
                    userData.get().getId());

/*            //eliminar datos del usuario sobre grupos y propiedad de grupos
            userData.get().setGroups(null);
            userData.get().setAdminGroups(null);*/

            //Iterar sobre cada grupo para eliminarlo
/*            for (Group group: userGroups) {
                this.group = Optional.ofNullable(group);
                userGroups.remove(0);
                deleteUser();
            }*/

            for (int i = 0; i <= userGroups.size() ; i += 1) {
                this.group = Optional.ofNullable(userGroups.get(i));
                deleteUser();
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Optional<Group> changeUserAdminOfTheGroup(AddUserToGroupVM addUser){
        if (manageUserOfTheGroup(addUser).isPresent()) {

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

    private Optional<Group> manageUserOfTheGroup(AddUserToGroupVM addUser) {
        userAdmin = userDataService.findOne(addUser.getIdAdminGroup());
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

}