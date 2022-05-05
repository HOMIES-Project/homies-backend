package com.homies.app.service;

import com.homies.app.domain.*;
import com.homies.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;

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

    private void detachUser(UserData user){}
    private void detachGroup(Group group){}
    private void detachTask(Task task){}
    private void detachProduct(Products products){}
    private void detachSpending(Spending spending){}
    private void detachUserPending(UserPending userPending){}

    private void detachTasksList(TaskList taskList){}
    private void detachShoppingList(ShoppingList shoppingList){}
    private void detachSpendingList(SpendingList spendingList){}

}
