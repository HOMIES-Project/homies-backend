package com.homies.app.service.AuxiliarServices;

import com.homies.app.domain.Task;
import com.homies.app.domain.TaskList;
import com.homies.app.domain.User;
import com.homies.app.domain.UserData;
import com.homies.app.service.*;
import com.homies.app.web.rest.errors.General.IncorrectParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ManageListTaskAuxService {
    @Autowired
    private TaskListService taskListService;
    @Autowired
    private UserDataService userDataService;
    @Autowired
    private UserService userService;
    @Autowired
    private TaskQueryService taskQueryService;

    public ManageListTaskAuxService(TaskListService taskListService,
                                    UserDataService userDataService,
                                    UserService userService,
                                    TaskQueryService taskQueryService) {
        this.taskListService = taskListService;
        this.userDataService = userDataService;
        this.userService = userService;
        this.taskQueryService =taskQueryService;
    }

    private Optional<TaskList> taskList;
    private Optional<UserData> userData;

    public List<Task> getTaskUserTaskList(Long id, String login){
        if (managerUserOfTheTask(id, login).isPresent()){
            taskList = taskListService.findOne(id);
            Optional<User> user = userService.getUser(login);
            userData = userDataService.findOne(user.get().getId());
            taskList = taskListService.findOne(id);
            List<Task> list = new ArrayList<>();

            taskList.get().getTasks().forEach(task ->{
                task.getUserAssigneds().forEach(userAssigned -> {
                    if (userData.get().getId() == userAssigned.getId()){
                        list.add(task);
                        return;
                    }
                });
            });
            return list;
        }
        throw new IncorrectParameters();
    }

    private Optional<TaskList> managerUserOfTheTask(Long id, String login){
        taskList = taskListService.findOne(id);
        Optional<User> user = userService.getUser(login);
        userData = userDataService.findOne(user.get().getId());

        if (userData.isEmpty() || taskList.isEmpty()){
            return Optional.empty();
        }
        return taskList;
    }
}

/*public List<Task> getTaskUserTaskList(GetGroupTaskListVM getGroupTaskListVM){
        if (managerUserOfTheTask(getGroupTaskListVM).isPresent()){
            taskList = taskListService.findOne(getGroupTaskListVM.getIdGroup());
            Optional<User> user = userService.getUser(getGroupTaskListVM.getLogin());
            userData = userDataService.findOne(user.get().getId());
            taskList = taskListService.findOne(getGroupTaskListVM.getIdGroup());
            List<Task> list = new ArrayList<>();

            taskList.get().getTasks().forEach(task ->{
                task.getUserAssigneds().forEach(userAssigned -> {
                    if (userData.get().getId() == userAssigned.getId()){
                        list.add(task);
                        return;
                    }
                });
            });
            return list;
        }
        throw new TaskWasNotSpecifyIdTask();
    }*/

/*private Optional<TaskList> managerUserOfTheTask(GetGroupTaskListVM getGroupTaskListVM){
        taskList = taskListService.findOne(getGroupTaskListVM.getIdGroup());
        Optional<User> user = userService.getUser(getGroupTaskListVM.getLogin());
        userData = userDataService.findOne(user.get().getId());

        if (userData.isEmpty() || taskList.isEmpty()){
            return Optional.empty();
        }
        return taskList;
    }*/
