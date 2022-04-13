package com.homies.app.service.AuxiliarServices;

import com.homies.app.domain.Task;
import com.homies.app.domain.TaskList;
import com.homies.app.domain.User;
import com.homies.app.domain.UserData;
import com.homies.app.service.*;
import com.homies.app.web.rest.vm.AddUserToTaskVM;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ManageTaskAuxService {

    private TaskService taskService;
    private UserDataQueryService userDataQueryService;
    private UserDataService userDataService;
    private TaskListService taskListService;
    private UserService userService;

    public ManageTaskAuxService(TaskService taskService,
                                UserDataQueryService userDataQueryService,
                                UserDataService userDataService,
                                TaskListService taskListService,
                                UserService userService) {
        this.taskService = taskService;
        this.userDataQueryService = userDataQueryService;
        this.userDataService = userDataService;
        this.taskListService = taskListService;
        this.userService = userService;
    }

    private Optional<UserData> userData;
    private Optional<Task> task;
    private Optional<TaskList> taskList;

    public Optional<Task> addUserToTask(AddUserToTaskVM addUserToTaskVM){
        if (managerUserOfTheGroup(addUserToTaskVM).isPresent()){
            userData.get().addTaskAsigned(task.get());
            userDataService.save(userData.get());
            task.get().addUserAssigned(userData.get());
            taskService.save(task.get());
            return taskService.findOne(task.get().getId());
        }
        return Optional.empty();
    }

    public Optional<Task> deleteUserToTask(AddUserToTaskVM addUserToTaskVM){
        if(managerUserOfTheGroup(addUserToTaskVM).isPresent()){
            deleteUser();
            return taskService.findOne(task.get().getId());
        }
        return Optional.empty();
    }

    private void deleteUser(){

        userData.get().removeTaskAsigned(task.get());
        userDataService.save(userData.get());

        task.get().removeUserAssigned(userData.get());
        taskService.save(task.get());

    }

    private Optional<Task> managerUserOfTheGroup(AddUserToTaskVM addUserToTaskVM){
        task = taskService.findOne(addUserToTaskVM.getIdTask());
        taskList = taskListService.findOne(addUserToTaskVM.getIdList());
        Optional<User> user = userService.getUser(addUserToTaskVM.getLogin());
        userData = userDataService.findOne(user.get().getId());

        if (userData.isEmpty() || task.isEmpty() || taskList.isEmpty()){
            return Optional.empty();
        }
        return task;
    }

}
