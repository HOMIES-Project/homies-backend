package com.homies.app.service.AuxiliarServices;

import com.homies.app.domain.Task;
import com.homies.app.domain.TaskList;
import com.homies.app.domain.User;
import com.homies.app.domain.UserData;
import com.homies.app.service.TaskListQueryService;
import com.homies.app.service.TaskListService;
import com.homies.app.service.UserDataService;
import com.homies.app.service.UserService;
import com.homies.app.web.rest.vm.GetGroupTaskListVM;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ManageListTaskAuxService {

    private TaskListService taskListService;
    private UserDataService userDataService;
    private UserService userService;
    private TaskListQueryService taskListQueryService;

    public ManageListTaskAuxService(TaskListService taskListService,
                                    UserDataService userDataService,
                                    UserService userService,
                                    TaskListQueryService taskListQueryService) {
        this.taskListService = taskListService;
        this.userDataService = userDataService;
        this.userService = userService;
        this.taskListQueryService =taskListQueryService;
    }

    private Optional<TaskList> taskList;
    private Optional<UserData> userData;

    public Optional<TaskList> getTaskUserTaskList(GetGroupTaskListVM getGroupTaskListVM){
        if (managerUserOfTheTask(getGroupTaskListVM).isPresent()){
            Optional<User> user = userService.getUser(getGroupTaskListVM.getLogin());
            userData = userDataService.findOne(user.get().getId());
            taskList = taskListQueryService.findByTasks_UserAssigneds_Id(userData.get().getId());
            return taskList;
        }
        return Optional.empty();
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
