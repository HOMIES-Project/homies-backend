package com.homies.app.web.rest.auxiliary;

import com.homies.app.domain.*;
import com.homies.app.service.*;
import com.homies.app.web.rest.GroupResource;
import com.homies.app.web.rest.vm.CreateGroupVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.security.RandomUtil;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class CreateGroupsAux {

    private final GroupQueryService groupQueryService;

    private final GroupService groupService;

    private final TaskListService taskListService;

    private final SpendingListService spendingListService;

    private final ShoppingListService shoppingListService;

    private final SettingsListService settingsListService;

    private final UserDataService userDataService;

    private final Logger log = LoggerFactory.getLogger(GroupResource.class);

    public CreateGroupsAux(GroupQueryService groupQueryService,
                           GroupService groupService,
                           TaskListService taskListService,
                           SpendingListService spendingListService,
                           ShoppingListService shoppingListService,
                           SettingsListService settingsListService,
                           UserDataService userDataService) {
        this.groupQueryService = groupQueryService;
        this.groupService = groupService;
        this.taskListService = taskListService;
        this.spendingListService = spendingListService;
        this.shoppingListService = shoppingListService;
        this.settingsListService = settingsListService;
        this.userDataService = userDataService;
    }

    public Group createNewGroup(CreateGroupVM groupVM) {
        if (userExist(groupVM.getUser()) == null)
            return null;

        if (groupExist(groupVM.getGroupName()))
            return null;

        log.warn("creating group");
        Group newGroup = new Group();
        newGroup.setGroupKey(RandomUtil.generateActivationKey()); //key
        newGroup.setGroupName(groupVM.getGroupName()); //name
        newGroup.setGroupRelationName(groupVM.getGroupRelation()); //RelationName

        //Administrator user add
        UserData userData = userExist(groupVM.getUser());
        newGroup.setUserAdmin(userData); //add user (group creator user)

        newGroup.setAddGroupDate(LocalDate.now()); //Date of creation

        TaskList taskList = createTaskList(groupVM.getGroupName());
        newGroup.setTaskList(taskList); //TaskList add

        //New Group created
        log.warn("Created group: " + newGroup);
        groupService.save(newGroup);

        //Add newGroup in UserData
        userData.addGroup(newGroup);
        userDataService.partialUpdate(userData);

        //New SpendingList
        SpendingList spendingList = createSpendingList(newGroup.getGroupName(), newGroup);
        newGroup.setSpendingList(spendingList);

        //New ShoppingList
        ShoppingList shoppingList = createShoppingList(newGroup.getGroupName(), newGroup);
        newGroup.setShoppingList(shoppingList);

        //New SettingsList
        SettingsList settingsList = createSettingsList(newGroup);
        newGroup.setSettingsList(settingsList);

        //Update taskList
        newGroup = groupService.findOne(newGroup.getGroupName()).get();
        updateTaskList(newGroup.getId(), newGroup);

        //Add userData in newGroup
        newGroup.addUserData(userData);

        //Group update with new taskList
        groupService.partialUpdate(newGroup);

        return newGroup;
    }

    @Transactional(readOnly = true)
    private UserData userExist(Long id) {
        Optional<UserData> user = userDataService.findOne(id);
        log.warn(user.toString());
        return user.orElse(null);
    }

    @Transactional(readOnly = true)
    private boolean groupExist(String name) {
        log.warn(name);
        return  false;//groupQueryService.findOneByName(name);
    }

    private TaskList createTaskList(String name) {
        TaskList newTaskList = new TaskList();
        newTaskList.setNameList("TKL" + name);
        taskListService.save(newTaskList);
        log.warn(newTaskList.toString());
        return newTaskList;
    }

    private void updateTaskList(Long id, Group group) {
        TaskList taskList = taskListService.findOne(id).get();
        taskList.group(group);
        taskListService.partialUpdate(taskList);
    }

    private SpendingList createSpendingList(String name, Group group) {
        SpendingList spendingList = new SpendingList();
        spendingList.setTotal(0f);
        spendingList.setNameSpendList("SPL_" + name);
        spendingList.setGroup(group);
        spendingListService.save(spendingList);
        return spendingList;
    }

    private ShoppingList createShoppingList(String name, Group group) {
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setTotal(0f);
        shoppingList.setNameShopList("SHL" + name);
        shoppingList.setGroup(group);
        shoppingListService.save(shoppingList);
        return shoppingList;
    }

    private SettingsList createSettingsList(Group group) {
        SettingsList settingsList = new SettingsList();
        settingsList.setGroup(group);
        settingsList.settingOne(false);
        settingsList.settingTwo(false);
        settingsList.settingThree(false);
        settingsList.settingFour(false);
        settingsList.settingFive(false);
        settingsList.settingSix(false);
        settingsList.settingSeven(false);
        settingsListService.save(settingsList);
        return settingsList;
    }

}
