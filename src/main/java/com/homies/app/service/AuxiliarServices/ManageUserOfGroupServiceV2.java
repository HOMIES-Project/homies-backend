package com.homies.app.service.AuxiliarServices;

import com.homies.app.domain.*;
import com.homies.app.repository.*;
import com.homies.app.security.SecurityUtils;
import com.homies.app.service.*;
import com.homies.app.web.rest.SpendingResource;
import com.homies.app.web.rest.errors.Group.GroupNotExistException;
import com.homies.app.web.rest.errors.Group.GroupUserLoginNotAdmin;
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


    



    @Transactional(readOnly = true)
    private boolean userIsAuthenticated(ManageGroupVM mg, MethodName methodName) throws Exception {
        if (SecurityUtils.isAuthenticated())
            throw new Exception("User Is Not Authenticated");

        try {
            userData = userDataRepository.getByUser_Login(SecurityUtils.getCurrentUserLogin().get());
            group = groupRepository.findById(mg.getIdGroup());
            userAdmin = userDataRepository.findById(mg.getIdAdminGroup());
        } catch (UsernameNotFoundException e) {
            throw new DatabaseException("Invalid or non-existent data provided");
        }

        switch (methodName) {
            case ADD_USER_TO_GROUP:
                if (isAdmin())
                    if (!isUserInGroup())
                        return true;
                break;
            case REMOVE_USER_FROM_GROUP:
            case CHANGE_ADMIN:
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

    @Transactional(readOnly = true)
    private boolean isAdmin(){
        if (Objects.equals(userAdmin.get(), group.get().getUserAdmin()))
            throw new GroupUserLoginNotAdmin();
        return true;
    }

    @Transactional(readOnly = true)
    private boolean isUserInGroup(){
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
    UPDATE_GROUP,
    CHANGE_ADMIN,
    DELETE_GROUP
}
