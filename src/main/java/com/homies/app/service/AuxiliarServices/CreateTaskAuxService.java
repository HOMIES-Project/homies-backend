package com.homies.app.service.AuxiliarServices;

import com.homies.app.domain.*;
import com.homies.app.repository.UserDataRepository;
import com.homies.app.service.*;
import com.homies.app.web.rest.GroupResource;
import com.homies.app.web.rest.errors.Group.GroupNotExistException;
import com.homies.app.web.rest.errors.Task.TaskAlreadyUsedException;
import com.homies.app.web.rest.errors.Task.TaskUserDoesNotExist;
import com.homies.app.web.rest.errors.User.UserDoesNotExistInGroup;
import com.homies.app.web.rest.vm.AddUserToTaskVM;
import com.homies.app.web.rest.vm.CreateTaskVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Service
public class CreateTaskAuxService {

    @Autowired
    private final TaskService taskService;
    @Autowired
    private final GroupService groupService;
    @Autowired
    private final TaskListService taskListService;
    @Autowired
    private final TaskListQueryService taskListQueryService;
    @Autowired
    private final UserDataService userDataService;
    @Autowired
    private final UserDataQueryService userDataQueryService;
    @Autowired
    private final TaskQueryService taskQueryService;

    private final Logger log = LoggerFactory.getLogger(GroupResource.class);

    public CreateTaskAuxService(TaskService taskService,
                                GroupService groupService,
                                TaskListService taskListService,
                                TaskListQueryService taskListQueryService,
                                UserDataService userDataService,
                                UserDataQueryService userDataQueryService,
                                TaskQueryService taskQueryService) {

        this.taskService = taskService;
        this.groupService = groupService;
        this.taskListService = taskListService;
        this.taskListQueryService = taskListQueryService;
        this.userDataService = userDataService;
        this.userDataQueryService = userDataQueryService;
        this.taskQueryService = taskQueryService;
    }

    private Optional<Group> group;

    public Task createNewTask(CreateTaskVM createTaskVM) {
        if (userExist(createTaskVM.getUser()) == null)
            throw new TaskUserDoesNotExist();

        if (groupExist(createTaskVM.getIdGroup()) == null)
            throw new GroupNotExistException();

        if(taskListExist(createTaskVM.getIdGroup()) == null)
            throw new TaskUserDoesNotExist();

        if (taskExist(createTaskVM.getIdGroup(), createTaskVM.getTaskName()).isPresent())
            throw new TaskAlreadyUsedException();

        log.warn("creating task");
        Task newTask = new Task();
        newTask.setTaskName(createTaskVM.getTaskName()); //name
        newTask.setDescription(createTaskVM.getDescription()); //description
        newTask.setDataCreate(LocalDate.now()); //Date of creation

        //New task created
        log.warn("Created Task: " + newTask);
        taskService.save(newTask);

        //User Assigned
        UserData userData = userDataQueryService.getByUser_Login(createTaskVM.getLogin()).get();
        userData.addTaskAsigned(newTask);
        newTask.addUserAssigned(userData);
        userDataService.save(userData);

        //Task add taskList
        TaskList taskList = taskListExist(createTaskVM.getIdGroup()); //Este m??todo lo llamas dos veces para lo mismo
        taskList.addTask(newTask);
        newTask.setTaskList(taskList);
        taskListService.save(taskList);

        taskService.save(newTask);

        refreshEntities();
        return taskService.findOne(newTask.getId()).get();
    }

    @Transactional(readOnly = true)
    private UserData userExist(Long id) {
        Optional<UserData> user = userDataService.findOne(id);
        log.warn(user.toString());
        return user.orElse(null);
    }

    @Transactional(readOnly = true)
    private Group groupExist(Long id) {
        Optional<Group> group = groupService.findOne(id);
        log.warn("" + id);
        return group.orElse(null);
    }

    @Transactional(readOnly = true)
    private TaskList taskListExist(Long id) {
        Optional<TaskList> taskList = taskListService.findOne(id);
        log.warn(taskList.toString());
        return taskList.orElse(null);
    }

    @Transactional(readOnly = true)
    private Optional<TaskList> taskExist(Long id, String taskName) {
        log.warn("" + id + " " + taskName );
        return taskListQueryService.findByIdAndTasks_TaskName(id, taskName);
    }

    private void refreshEntities() {
        taskQueryService.refreshTaskEntity();
        taskListQueryService.refreshTaskListEntity();
        userDataQueryService.refreshUserDataEntity();
    }
}
