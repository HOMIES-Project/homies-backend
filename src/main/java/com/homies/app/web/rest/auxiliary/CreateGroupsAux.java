package com.homies.app.web.rest.auxiliary;

import com.homies.app.domain.Group;
import com.homies.app.domain.TaskList;
import com.homies.app.domain.User;
import com.homies.app.service.*;

import java.time.LocalDate;
import java.util.Optional;

public class CreateGroupsAux {

    private final GroupQueryService groupQueryService;

    private final TaskListService taskListService;

    private final UserService userService;

    public CreateGroupsAux(GroupQueryService groupQueryService, TaskListService taskListService, UserService userService) {
        this.groupQueryService = groupQueryService;
        this.taskListService = taskListService;
        this.userService = userService;
    }

    private User userExist(Long id) {
        Optional<User> user = userService.findUserById(id);
        return user.orElse(null);
    }

    private boolean groupExist(String name) {
        return  groupQueryService.findOneByName(name);
    }

    private TaskList createTaskList(Group group, String name) {
        TaskList newTaskList = new TaskList();
        newTaskList.setNameList(name);
        newTaskList.setGroup(group);
        taskListService.save(newTaskList);
        return newTaskList;
    }

    public Group createNewGroup(Long id, String name, String relationName) {
        Group newGroup = new Group();
        newGroup.setGroupKey("probando" + LocalDate.now());
        newGroup.setGroupName(name);
        newGroup.setGroupRelationName(relationName);
        newGroup.setTaskList(this.createTaskList(newGroup, name));
        //newGroup.setUserAdmin(this.userExist(id));
        return newGroup;
    }

}
