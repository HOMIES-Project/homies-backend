package com.homies.app.service.AuxiliarServices;

import com.homies.app.domain.*;
import com.homies.app.repository.*;
import com.homies.app.security.SecurityUtils;
import com.homies.app.service.*;
import com.homies.app.web.rest.SpendingResource;
import com.homies.app.web.rest.errors.Group.GroupNotExistException;
import com.homies.app.web.rest.errors.Group.GroupUserLoginNotAdmin;
import com.homies.app.web.rest.errors.GroupAlreadyUsedException;
import com.homies.app.web.rest.errors.User.UserDoesNotExist;
import com.homies.app.web.rest.vm.ManageGroupVM;
import com.homies.app.web.rest.vm.UpdateGroupVM;
import liquibase.exception.DatabaseException;
import org.aspectj.weaver.patterns.ExactAnnotationFieldTypePattern;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.*;

@Service
public class ManageUserOfGroupServiceV2 {

    private final Logger log = LoggerFactory.getLogger(ManageUserOfGroupServiceV2.class);
    @Autowired
    private final UserDataRepository userDataRepository;
    @Autowired
    private final GroupRepository groupRepository;
    @Autowired
    private final TaskRepository taskRepository;
    @Autowired
    private final TaskListRepository taskListRepository;
    @Autowired
    private final ProductsRepository productsRepository;
    @Autowired
    private final ShoppingListRepository shoppingListRepository;
    @Autowired
    private final SpendingResource spendingResource;
    @Autowired
    private final SpendingListRepository spendingListRepository;
    @Autowired
    private final UserPendingRepository userPendingRepository;

    public ManageUserOfGroupServiceV2(
        UserDataRepository userDataRepository,
        GroupRepository groupRepository,
        TaskRepository taskRepository,
        TaskListRepository taskListRepository,
        ProductsRepository productsRepository,
        ShoppingListRepository shoppingListRepository,
        SpendingResource spendingResource,
        SpendingListRepository spendingListRepository,
        UserPendingRepository userPendingRepository
    ) {
        this.userDataRepository = userDataRepository;
        this.groupRepository = groupRepository;
        this.taskRepository = taskRepository;
        this.taskListRepository = taskListRepository;
        this.productsRepository = productsRepository;
        this.shoppingListRepository = shoppingListRepository;
        this.spendingResource = spendingResource;
        this.spendingListRepository = spendingListRepository;
        this.userPendingRepository = userPendingRepository;
    }

    Optional<UserData> userData = Optional.empty();
    Optional<Group> group = Optional.empty();
    Optional<UserData> userAdmin = Optional.empty();

/*    Optional<Task> task = Optional.empty();
    Optional<TaskList> taskList = Optional.empty();
    Optional<Products> products = Optional.empty();
    Optional<ShoppingList> shoppingList = Optional.empty();
    Optional<Spending> spending = Optional.empty();
    Optional<SpendingList> spendingList = Optional.empty();
    Optional<UserPending> userPending = Optional.empty();*/

    public Optional<Group> addUserToGroup(@NotNull ManageGroupVM manageGroupVM) {
        try {
            if (userIsAuthenticated(manageGroupVM, MethodName.ADD_USER_TO_GROUP)) {

                userData.get().addGroup(group.get());
                userDataRepository.save(userData.get());

                return groupRepository.findById(group.get().getId());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Optional<Group> removeUserFromGroup(@NotNull ManageGroupVM manageGroupVM) {
        try {
            if (userIsAuthenticated(manageGroupVM, MethodName.REMOVE_USER_FROM_GROUP)) {

                userData.get().removeGroup(group.get());
                userDataRepository.saveAndFlush(userData.get());

                updateEntities(manageGroupVM);

                if (Objects.equals(userAdmin.get().getId(), group.get().getUserAdmin().getId())) {
                    if (group.get().getUserData().size() > 0) {
                        manageGroupVM.setLogin(
                            group.get().getUserData().stream().findFirst()
                                .get().getUser().getLogin()
                        );
                        changeAdminOfGroup(manageGroupVM);
                    } else {
                        deleteGroup(manageGroupVM);
                    }
                }

                return groupRepository.findById(group.get().getId());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Optional<Group> changeAdminOfGroup(@NotNull ManageGroupVM manageGroupVM) {
        try {
            if (userIsAuthenticated(manageGroupVM, MethodName.CHANGE_ADMIN_OF_GROUP)) {

                groupRepository.updateUserAdmin(
                    userAdmin.get(),
                    group.get().getId(),
                    userData.get()
                );

                return groupRepository.findById(group.get().getId());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Optional<Group> updateGroup(@NotNull ManageGroupVM manageGroupVM,
                                       @NotNull UpdateGroupVM updateGroupVM) {
        try {
            if (userIsAuthenticated(manageGroupVM, MethodName.UPDATE_GROUP)) {

                group.get().setGroupName(updateGroupVM.getGroupName());
                group.get().setGroupRelationName(updateGroupVM.getGroupRelation());
                groupRepository.save(group.get());

                return groupRepository.findById(group.get().getId());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Optional<Group> deleteGroup(@NotNull ManageGroupVM manageGroupVM) {
        try {
            if (userIsAuthenticated(manageGroupVM, MethodName.DELETE_GROUP)) {

                groupRepository.deleteByIdAndUserAdmin(
                    manageGroupVM.getIdGroup(),
                    userAdmin.get()
                );

                return groupRepository.findById(group.get().getId());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
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
            case DELETE_GROUP:
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
            userData = userDataRepository.getByUser_Login(manageGroupVM.getLogin());
            group = groupRepository.findById(manageGroupVM.getIdGroup());
            userAdmin = userDataRepository.getByUser_Login(SecurityUtils.getCurrentUserLogin().get());
        } catch (UsernameNotFoundException e) {
            throw new DatabaseException("Invalid or non-existent data provided");
        }
    }

    private boolean isAdmin() {
        if (Objects.equals(userAdmin.get(), group.get().getUserAdmin()))
            return true;
        throw new GroupUserLoginNotAdmin();
    }

    private boolean isUserInGroup() {
        return group.get().getUserData().contains(userData.get());
    }

    /**
     * Función demostrativa para eliminar
     */
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

enum MethodName {
    ADD_USER_TO_GROUP,
    REMOVE_USER_FROM_GROUP,
    REMOVE_ADMIN_FROM_GROUP,
    CHANGE_ADMIN_OF_GROUP,
    UPDATE_GROUP,
    DELETE_GROUP
}
