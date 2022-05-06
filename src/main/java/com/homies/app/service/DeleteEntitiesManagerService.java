package com.homies.app.service;

import com.homies.app.domain.*;
import com.homies.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class DeleteEntitiesManagerService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final UserDataRepository userDataRepository;

    @Autowired
    private final GroupRepository groupRepository;

    @Autowired
    private final TaskListRepository taskListRepository;

    @Autowired
    private final TaskRepository taskRepository;

    @Autowired
    private final ShoppingListRepository shoppingListRepository;

    @Autowired
    private final ProductsRepository productsRepository;

    @Autowired
    private final SpendingListRepository spendingListRepository;

    @Autowired
    private final SpendingRepository spendingRepository;

    @Autowired
    private final UserPendingRepository userPendingRepository;

    public DeleteEntitiesManagerService(
        UserRepository userRepository,
        UserDataRepository userDataRepository,
        GroupRepository groupRepository,
        TaskListRepository taskListRepository,
        TaskRepository taskRepository,
        ShoppingListRepository shoppingListRepository,
        ProductsRepository productsRepository,
        SpendingListRepository spendingListRepository,
        SpendingRepository spendingRepository,
        UserPendingRepository userPendingRepository
    ) {
        this.userRepository = userRepository;
        this.userDataRepository = userDataRepository;
        this.groupRepository = groupRepository;
        this.taskListRepository = taskListRepository;
        this.taskRepository = taskRepository;
        this.shoppingListRepository = shoppingListRepository;
        this.productsRepository = productsRepository;
        this.spendingListRepository = spendingListRepository;
        this.spendingRepository = spendingRepository;
        this.userPendingRepository = userPendingRepository;
    }

    private UserData user;
    private Group group;
    private Task task;
    private Products products;
    private Spending spending;
    private UserPending userPending;
    private TaskList taskList;
    private ShoppingList shoppingList;
    private SpendingList spendingList;

    public void deleteUser(Long id){
        UserData user = userDataRepository.findById(id).get();
        /* Delete of groups os user*/
        Set<Group> groups = user.getGroups();
        groups.forEach(group ->{
            group.getUserData().remove(user);
            groupRepository.saveAndFlush(group);

            taskListRepository.findById(group.getId()).get().getTasks().remove(user.getTaskAsigneds());
            taskList = taskListRepository.findById(group.getId()).get();
            taskListRepository.saveAndFlush(taskList);

        });
        user.setGroups(new HashSet<>());

        /* Delete of groups of admin */
        Set<Group> groupsAdmin = user.getAdminGroups();
        groupsAdmin.forEach(group ->{
            UserData admin = null;
            if (group.getUserData().size() > 0) {
                if (user != group.getUserData().iterator().next()) {
                    admin = group.getUserData().iterator().next();
                }
            }
            group.setUserAdmin(
                admin
            );
            groupRepository.saveAndFlush(group);
        });
        user.setAdminGroups(new HashSet<>());

        /* Delete of task assigned */
        Set<Task> tasks = user.getTaskAsigneds();
        tasks.forEach(taskRepository::delete);
        user.setTaskAsigneds(new HashSet<>());

        /* Delete of products */
        Set<Products> allProducts = user.getProductCreateds();
        allProducts.forEach(product -> {
            product.setUserCreator(null);
            productsRepository.saveAndFlush(product);
        });
        user.setProductCreateds(new HashSet<>());

        userDataRepository.saveAndFlush(user);


    }

    private void deleteGroup(Group group){

    }

    private void detachTask(Task task){}
    private void detachProduct(Products products){}
    private void detachSpending(Spending spending){}
    private void detachUserPending(UserPending userPending){}

    private void detachTasksList(TaskList taskList){}
    private void detachShoppingList(ShoppingList shoppingList){}
    private void detachSpendingList(SpendingList spendingList){}

}
