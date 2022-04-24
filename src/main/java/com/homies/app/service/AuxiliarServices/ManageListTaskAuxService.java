package com.homies.app.service.AuxiliarServices;

import com.homies.app.domain.Task;
import com.homies.app.domain.TaskList;
import com.homies.app.domain.User;
import com.homies.app.domain.UserData;
import com.homies.app.service.*;
import com.homies.app.web.rest.errors.Task.TaskWasNotSpecifyIdTask;
import com.homies.app.web.rest.vm.GetGroupTaskListVM;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ManageListTaskAuxService {

    private TaskListService taskListService;
    private UserDataService userDataService;
    private UserService userService;
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

    public List<Task> getTaskUserTaskList(GetGroupTaskListVM getGroupTaskListVM){
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
    }

    private Optional<TaskList> managerUserOfTheTask(GetGroupTaskListVM getGroupTaskListVM){
        taskList = taskListService.findOne(getGroupTaskListVM.getIdGroup());
        Optional<User> user = userService.getUser(getGroupTaskListVM.getLogin());
        userData = userDataService.findOne(user.get().getId());

        if (userData.isEmpty() || taskList.isEmpty()){
            return Optional.empty();
        }
        return taskList;
    }
}
