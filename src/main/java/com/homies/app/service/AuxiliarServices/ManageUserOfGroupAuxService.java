package com.homies.app.service.AuxiliarServices;

import com.homies.app.domain.Group;
import com.homies.app.domain.Task;
import com.homies.app.domain.TaskList;
import com.homies.app.domain.UserData;

import com.homies.app.security.SecurityUtils;

import com.homies.app.service.*;

import com.homies.app.web.rest.errors.Group.GroupNotExistException;
import com.homies.app.web.rest.errors.User.UserDoesNotExist;
import com.homies.app.web.rest.vm.ManageGroupVM;
import com.homies.app.web.rest.vm.UpdateGroupVM;

import org.jetbrains.annotations.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;

import java.util.*;

@Service
public class ManageUserOfGroupAuxService {

    private final Logger log = LoggerFactory.getLogger(ManageUserOfGroupAuxService.class);
    @Autowired
    private final UserDataService userDataService;
    @Autowired
    private final UserDataQueryService userDataQueryService;
    @Autowired
    private final GroupService groupService;
    @Autowired
    private final GroupQueryService groupQueryService;
    @Autowired
    private final TaskService taskService;
    @Autowired
    private final TaskQueryService taskQueryService;
    @Autowired
    private final TaskListService taskListService;
    @Autowired
    private final TaskListQueryService taskListQueryService;
    @Autowired
    private final DeleteEntitiesManagerService deleteEntitiesManager;

    public ManageUserOfGroupAuxService(
        UserDataService userDataService,
        UserDataQueryService userDataQueryService,
        GroupService groupService,
        GroupQueryService groupQueryService,
        TaskService taskService,
        TaskQueryService taskQueryService,
        TaskListService taskListService,
        TaskListQueryService taskListQueryService,
        DeleteEntitiesManagerService deleteEntitiesManager
    ) {
        this.userDataService = userDataService;
        this.userDataQueryService = userDataQueryService;
        this.groupService = groupService;
        this.groupQueryService = groupQueryService;
        this.taskService = taskService;
        this.taskListService = taskListService;
        this.taskListQueryService = taskListQueryService;
        this.taskQueryService = taskQueryService;
        this.deleteEntitiesManager = deleteEntitiesManager;
    }

    private Optional<UserData> userAdmin;
    private Optional<Group> group;
    private Optional<UserData> userData;

    //Funciona
    public Optional<Group> addUserToGroup(
        ManageGroupVM manageGroupVM
    ) throws UserPrincipalNotFoundException {
        if (manageUserOfTheGroup(manageGroupVM, false, false,false).isPresent()) {

            if (groupQueryService.findGroupByIdAndUserDataUserLogin(
                manageGroupVM.getIdGroup(),
                manageGroupVM.getLogin()).isPresent())
                throw new UsernameAlreadyUsedException();

            //group.get().addUserData(userData.get());

            //Add newGroup in UserData
            userData.get().addGroup(group.get());
            userDataService.save(userData.get());
            group.get().addUserData(userData.get());
            groupService.save(group.get());
            refreshEntities();
            /*UserData oldUser = group.get().getUserData().stream().filter(userData1 -> userData1.equals(userData)).findAny().orElse(null);

            if (oldUser == null) {

                groupService.save(group.get());
            }*/



            return groupService.findOne(group.get().getId());
        }
        return Optional.empty();

    }

    public Group updateGroup(UpdateGroupVM updateGroupVM) {
        ManageGroupVM manageGroupVM = new ManageGroupVM();

        //Adapting data
        manageGroupVM.setLogin(SecurityUtils.getCurrentUserLogin().get());
        manageGroupVM.setIdGroup(updateGroupVM.getIdGroup());

        try {
            if (manageUserOfTheGroup(manageGroupVM, false, true,false).isPresent()) {
                group.get().setGroupName(updateGroupVM.getGroupName());
                group.get().setGroupRelationName(updateGroupVM.getGroupRelation());
                groupService.save(group.get());
            }

        } catch (UserPrincipalNotFoundException e) {
            e.printStackTrace();

        }

        return group.get();

    }

    public Optional<Group> deleteUserToTheGroup(
        ManageGroupVM addUser
    ) throws UserPrincipalNotFoundException {

        boolean userOrAdmin = addUser.getIdAdminGroup() == null;
        if (manageUserOfTheGroup(addUser, userOrAdmin, false,false).isPresent()) {
            if (groupQueryService.findGroupByIdAndUserDataUserLogin(
                                    addUser.getIdGroup(),
                                    addUser.getLogin())
                                    .isEmpty())
                throw new UsernameNotFoundException("No existe el usuario en este equipo");

            removeUserFromTheGroup();
            return groupService.findOne(group.get().getId());

        }
        return Optional.empty();

    }

    public void deleteUserAndRelationships(Long id) {
        userData = userDataService.findOne(id);
        if (userData.isEmpty())
            throw new UserDoesNotExist();

        //Detach Task
        List<Task> tasks = taskQueryService.getByUserAssigneds_Id(userData.get().getId());
        tasks.forEach(task -> {
            task.removeUserAssigned(userData.get());
            userData.get().getTaskAsigneds().remove(task);
            taskService.save(task);
        });
        userData.get().setTaskAsigneds(new HashSet<>());
        userDataService.save(userData.get());
        refreshEntities();

        //Detach user of her groups
        List<Group> useGroups = groupQueryService.getUseGroupsByUserDataId(userData.get().getId());
        useGroups.forEach(useGroup -> {
            useGroup.removeUserData(userData.get());
            groupService.save(useGroup);
        });
        userData.get().setGroups(new HashSet<>());
        userDataService.save(userData.get());
        refreshEntities();

        //Detach user of her admin groups
        List<Group> adminGroups = groupQueryService.getAdminGroupsByUserDataId(userData.get().getId());
        userData.get().setAdminGroups(new HashSet<>());
        userDataService.save(userData.get());
        adminGroups.forEach(adminGroup -> {

            if (adminGroup.getUserData().size() > 1) {
                adminGroup.setUserAdmin(null);
                adminGroup.setUserAdmin(adminGroup.getUserData().iterator().next());
                userDataService.save(userData.get());
                groupService.save(adminGroup);
                //userDataService.save(userData.get());
            } else {
                //adminGroup.setUserAdmin(null);
                //groupService.save(adminGroup);
                ManageGroupVM mgv = new ManageGroupVM();
                mgv.setLogin(SecurityUtils.getCurrentUserLogin().get());
                mgv.setIdAdminGroup(id);
                mgv.setIdGroup(adminGroup.getId());
                deleteGroup(mgv); //Jorge del mañana, esto no se pude hacer, hya que pasar los datos.
                //groupService.save(adminGroup);
                //groupService.delete(adminGroup.getId());
            }
        });

        refreshEntities();

        userDataService.delete(id);

    }

    //Funciona. ---Trasladar a DeleteEntitiesManagerService
    public Optional<Group> deleteGroup(
        ManageGroupVM manageGroupVM
    ) {
        try {
            if (manageUserOfTheGroup(manageGroupVM, false, false, true).isPresent()) {

                List<UserData> users = new ArrayList<>(group.get().getUserData());
                users.forEach(user -> {
                    user.removeGroup(group.get());
                    userDataService.save(user);
                });

                userAdmin.get().removeAdminGroups(group.get());
                userDataService.save(userAdmin.get());

                Optional<TaskList> taskList = taskListService.findOne(group.get().getId());
                List<Task> tasks = new ArrayList<>(taskList.get().getTasks());

                tasks.forEach(task -> {
                    if (task.getUserAssigneds().size() > 0){
                        userData = Optional.ofNullable(task.getUserAssigneds().iterator().next());
                        userData.get().getTaskAsigneds().remove(task);
                        userDataService.save(userData.get());
                    }

                    task.setTaskList(null);
                    taskService.save(task);
                });

                taskList.get().setTasks(new HashSet<>());
                taskListService.save(taskList.get());

                tasks.forEach(task -> {
                    taskService.delete(task.getId());
                });

                group.get().setUserAdmin(null);
                groupService.save(group.get());
                groupService.delete(group.get().getId());
                refreshEntities();

            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        return Optional.empty();
    }

    public Optional<Group> changeUserAdminOfTheGroup(
        ManageGroupVM manageGroupVM
    ) throws UserPrincipalNotFoundException {
        if (manageUserOfTheGroup(manageGroupVM, false, false,false).isPresent()) {

            if (groupQueryService.findGroupByIdAndUserDataUserLogin(
                manageGroupVM.getIdGroup(),
                manageGroupVM.getLogin()).isEmpty())
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


    //Funciona, pero hay que hacer ajustes
    private Optional<Group> manageUserOfTheGroup(
        @NotNull ManageGroupVM manageGroupVM,
        boolean userExit,
        boolean editGroup,
        boolean deleteGroup
    ) throws UserPrincipalNotFoundException {

        userAdmin = userDataService.findOne(manageGroupVM.getIdAdminGroup());
        group = groupService.findOne(manageGroupVM.getIdGroup());
        userData = userDataQueryService.getByUser_Login(manageGroupVM.getLogin());

        if (!userExit) {
            if (deleteGroup || editGroup) userAdmin = userData;
            if (userAdmin.isEmpty())
                throw new UserPrincipalNotFoundException("Don't exist this admin"); //UserAdmin not exist

            if (group.get().getUserAdmin() != null)
                if (!Objects.equals(userAdmin.get().getId(), group.get().getUserAdmin().getId()))
                    throw new UserPrincipalNotFoundException("UserAdmin isn't admin of this group");  //UserAdmin not is group's userAdmin.

        }

        if (!deleteGroup & !editGroup) {
            if (userData.isEmpty())
                throw new UserDoesNotExist(); //UserData not exist
        }

        if (group.isEmpty())
            throw new GroupNotExistException(); //Group not exist

       /* if (group.get().getUserAdmin() == null)
            throw new UserPrincipalNotFoundException(manageGroupVM.getIdAdminGroup().toString()); //Group's UserAdmin not exist
*/
        return group;

    }

    private void removeUserFromTheGroup() {
        try {
            userData.get().removeGroup(group.get());
            userDataService.save(userData.get());

            //detachedTasks(); //Falta probar esto.

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
                    refreshEntities();
                } else {
                    groupService.delete(group.get().getId());
                    refreshEntities();
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
        taskQueryService.refreshTaskEntity();
        taskListQueryService.refreshTaskListEntity();

    }

    /** Función demostrativa para eliminar */
    private void pruebaSeguridad() {
        //Prueba de seguridades
        Optional<String> uno = SecurityUtils.getCurrentUserLogin();
        Optional<String> dos = SecurityUtils.getCurrentUserJWT();
        boolean tres = SecurityUtils.hasCurrentUserAnyOfAuthorities(); //tiene el usuario actual cualquiera de las autoridades??
        boolean cuatro = SecurityUtils.hasCurrentUserNoneOfAuthorities(); //tiene Usuario actual Ninguna de las autoridades??
        boolean cinco = SecurityUtils.hasCurrentUserThisAuthority(dos.get()); //tiene el usuario actual esta autoridad
        boolean seis = SecurityUtils.isAuthenticated();

        log.warn("Uno : " + uno.get());
        log.warn("Dos : " + dos.get());
        log.warn("tres : " + tres);
        log.warn("cuatro : " + cuatro);
        log.warn("Cinco : " + cinco);
        log.warn("Seis : " + seis);
    }
}
