package com.homies.app.web.rest.auxiliary;

import com.homies.app.domain.Group;
import com.homies.app.domain.TaskList;
import com.homies.app.domain.User;
import com.homies.app.domain.UserData;
import com.homies.app.service.*;
import com.homies.app.web.rest.GroupResource;
import com.homies.app.web.rest.vm.CreateGroupVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.security.RandomUtil;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class CreateGroupsAux {

    private final GroupQueryService groupQueryService;

    private final GroupService groupService;

    private final TaskListService taskListService;

    private final UserDataService userDataService;

    private final Logger log = LoggerFactory.getLogger(GroupResource.class);

    public CreateGroupsAux(GroupQueryService groupQueryService,
                           GroupService groupService,
                           TaskListService taskListService,
                           UserDataService userDataService) {
        this.groupQueryService = groupQueryService;
        this.groupService = groupService;
        this.taskListService = taskListService;
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

        //First user add (The same user as the administrator)
        HashSet userSet = new HashSet<UserData>();
        userSet.add(userData);
        newGroup.setUserData(userSet);

        //modelo de inserción de usuarios ***********eliminar esto
        /*userData = userExist(2L);
        userSet.add(userData);*/

        newGroup.setAddGroupDate(LocalDate.now()); //Date of creation

        TaskList taskList = createTaskList(groupVM.getGroupName());
        newGroup.setTaskList(taskList); //TaskList add

        //New Group created
        log.warn("Created group: " + newGroup);
        groupService.save(newGroup);

        //Update taskList
        //newGroup = groupService.findOne(newGroup.getGroupName()).get();
        updateTaskList(newGroup.getId(), newGroup);

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
        newTaskList.setNameList("Lista" + name);
        taskListService.save(newTaskList);
        log.warn(newTaskList.toString());
        return newTaskList;
    }

    private void updateTaskList(Long id, Group group) {
        TaskList taskList = taskListService.findOne(id).get();
        taskList.group(group);
        taskListService.partialUpdate(taskList);
    }



}
