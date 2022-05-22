package com.homies.app.service.AuxiliarServices;

import com.homies.app.domain.Group;
import com.homies.app.domain.Task;
import com.homies.app.domain.TaskList;
import com.homies.app.domain.UserData;

import com.homies.app.security.SecurityUtils;

import com.homies.app.service.*;

import com.homies.app.web.rest.errors.Group.GroupUserLoginNotAdmin;
import com.homies.app.web.rest.errors.User.UserDoesNotExist;
import com.homies.app.web.rest.vm.ManageGroupVM;
import com.homies.app.web.rest.vm.UpdateGroupVM;

import liquibase.exception.DatabaseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;

import java.util.*;

@Service
public class ManageUserAndGroupsAuxService {

    private final Logger log = LoggerFactory.getLogger(ManageUserAndGroupsAuxService.class);
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
    private final ManageTaskAuxService manageTaskAuxService;


    public ManageUserAndGroupsAuxService(
        UserDataService userDataService,
        UserDataQueryService userDataQueryService,
        GroupService groupService,
        GroupQueryService groupQueryService,
        TaskService taskService,
        TaskQueryService taskQueryService,
        TaskListService taskListService,
        ManageTaskAuxService manageTaskAuxService
    ) {
        this.userDataService = userDataService;
        this.userDataQueryService = userDataQueryService;
        this.groupService = groupService;
        this.groupQueryService = groupQueryService;
        this.taskService = taskService;
        this.taskListService = taskListService;
        this.taskQueryService = taskQueryService;
        this.manageTaskAuxService = manageTaskAuxService;
    }

    private Optional<UserData> userAdmin;
    private Optional<Group> group;
    private Optional<UserData> userData;

    public Optional<Group> addUserToGroup(
        ManageGroupVM manageGroupVM
    ) throws UserPrincipalNotFoundException {
        try {
            if (userIsAuthenticated(manageGroupVM, MethodName.ADD_USER_TO_GROUP)) {
                if (groupQueryService.findGroupByIdAndUserDataUserLogin(
                    manageGroupVM.getIdGroup(),
                    manageGroupVM.getLogin()).isPresent())
                    throw new UsernameAlreadyUsedException();

                //Add newGroup in UserData
                userData.get().addGroup(group.get());
                userDataService.save(userData.get());
                group.get().addUserData(userData.get());
                groupService.save(group.get());

                log.warn("@@@@ Homies::User {} added to group {}", manageGroupVM.getLogin(), manageGroupVM.getIdGroup());

                return groupService.findOne(group.get().getId());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Optional<Group> updateGroup(UpdateGroupVM updateGroupVM) {
        ManageGroupVM manageGroupVM = new ManageGroupVM();
        manageGroupVM.setLogin(SecurityUtils.getCurrentUserLogin().get());
        manageGroupVM.setIdGroup(updateGroupVM.getIdGroup());

        try {
            if (userIsAuthenticated(manageGroupVM, MethodName.UPDATE_GROUP)) {
                group.get().setGroupName(updateGroupVM.getGroupName());
                group.get().setGroupRelationName(updateGroupVM.getGroupRelation());
                groupService.save(group.get());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        log.warn("@@@@ Homies::Group {} updated", updateGroupVM.getIdGroup());
        return group;
    }

    public Optional<Group> deleteUserToTheGroup(
        ManageGroupVM manageGroupVM
    ) {
        try {
            if (userIsAuthenticated(manageGroupVM, MethodName.REMOVE_USER_FROM_GROUP)) {
                if (groupQueryService.findGroupByIdAndUserDataUserLogin(
                        manageGroupVM.getIdGroup(),
                        manageGroupVM.getLogin())
                    .isEmpty())
                    throw new UsernameNotFoundException("No existe el usuario en este equipo");

                removeUserFromTheGroup();
                return groupService.findOne(group.get().getId());

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        log.warn("@@@@ Homies::User {} removed from group {}", manageGroupVM.getLogin(), manageGroupVM.getIdGroup());
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

        //Detach user of her groups
        List<Group> useGroups = groupQueryService.getUseGroupsByUserDataId(userData.get().getId());
        useGroups.forEach(useGroup -> {
            useGroup.removeUserData(userData.get());
            groupService.save(useGroup);
        });
        userData.get().setGroups(new HashSet<>());
        userDataService.save(userData.get());

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
            } else {
                deletingGroup();
            }
        });

        userDataService.delete(id);

        log.warn("@@@@ Homies::User {} deleted", id);
    }

    public Optional<Group> deleteGroup(
        ManageGroupVM manageGroupVM
    ) {
        try {
            if (userIsAuthenticated(manageGroupVM, MethodName.DELETE_GROUP)) {

                deletingGroup();

                log.warn("@@@@ Homies::Group {} deleted", manageGroupVM.getIdGroup());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private void deletingGroup() {
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
            if (task.getUserAssigneds().size() > 0) {
                userData = Optional.ofNullable(task.getUserAssigneds().iterator().next());
                userData.get().getTaskAsigneds().remove(task);
                userDataService.save(userData.get());
            }

            task.setTaskList(null);
            taskService.save(task);
        });

        taskList.get().setTasks(new HashSet<>());
        taskListService.save(taskList.get());

        tasks.forEach(task -> taskService.delete(task.getId()));

        group.get().setUserAdmin(null);
        groupService.save(group.get());
        groupService.delete(group.get().getId());
    }

    public Optional<Group> changeUserAdminOfTheGroup(
        ManageGroupVM manageGroupVM
    ) {
        try {
            if (userIsAuthenticated(manageGroupVM, MethodName.CHANGE_ADMIN_OF_GROUP)) {
                changingAdmin();

                log.warn("@@@@ Homies::User {} is now admin of group {}", manageGroupVM.getLogin(), manageGroupVM.getIdGroup());

                return groupService.findOne(group.get().getId());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    private void changingAdmin() {
        groupQueryService.updateUserAdmin(
            userAdmin.get(),
            group.get().getId(),
            userData.get()
        );
    }

    private void removeUserFromTheGroup() {
        try {
            group.get().getTaskList().getTasks().stream().peek(
                task -> {
                    try {
                        manageTaskAuxService.deleteTask(task.getId());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            );

            userData.get().removeGroup(group.get());
            userDataService.save(userData.get());

            group.get().removeUserData(userData.get());
            groupService.save(group.get());

            if (userData.get().getId().longValue() == group.get().getUserAdmin().getId().longValue()) {

                userData.get().removeAdminGroups(group.get());
                userDataService.partialUpdate(userData.get());

                Set<UserData> userDataSet = group.get().getUserData();
                boolean changeAdmin = false;
                if (userDataSet.size() > 0) {
                    userData = userDataService.findOne(userDataSet.iterator().next().getId());
                    if (!Objects.equals(userData.get().getId(), userAdmin.get().getId()))
                        changeAdmin = true;
                }

                if (changeAdmin) {
                    changingAdmin();
                } else {
                    deletingGroup();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean userIsAuthenticated(ManageGroupVM manageGroupVM, MethodName methodName) throws Exception {
        if (!SecurityUtils.isAuthenticated())
            throw new Exception("User Is Not Authenticated");

        updateEntities(manageGroupVM);

        switch (methodName) {
            case ADD_USER_TO_GROUP:
                if (isAdmin())
                    if (!isUserInGroup())
                        return true;
                break;
            case REMOVE_USER_FROM_GROUP:
            case CHANGE_ADMIN_OF_GROUP:
                if (isAdmin())
                    if (isUserInGroup())
                        return true;
                break;
            case UPDATE_GROUP:
                if (isAdmin())
                    return true;
                break;
            case DELETE_GROUP:
                if (isNullAdmin())
                    return true;
                if (isAdmin())
                    return true;
                break;
            default:
                throw new Exception("Invalid method name");
        }

        return false;
    }

    private void updateEntities(ManageGroupVM manageGroupVM) throws DatabaseException {
        try {
            userData = userDataQueryService.getByUser_Login(manageGroupVM.getLogin());
            group = groupService.findOne(manageGroupVM.getIdGroup());
            userAdmin = userDataQueryService.getByUser_Login(SecurityUtils.getCurrentUserLogin().get());
        } catch (UsernameNotFoundException e) {
            throw new DatabaseException("Invalid or non-existent data provided");
        }
    }

    private boolean isAdmin() {
        if (Objects.equals(userAdmin.get(), group.get().getUserAdmin()))
            return true;
        throw new GroupUserLoginNotAdmin();
    }

    private boolean isNullAdmin() {
        if (group.get().getUserAdmin() == null)
            return true;
        throw new GroupUserLoginNotAdmin();
    }

    private boolean isUserInGroup() {
        return group.get().getUserData().contains(userData.get());
    }

}

enum MethodName {
    ADD_USER_TO_GROUP,
    REMOVE_USER_FROM_GROUP,
    CHANGE_ADMIN_OF_GROUP,
    UPDATE_GROUP,
    DELETE_GROUP
}
