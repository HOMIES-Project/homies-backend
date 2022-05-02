package com.homies.app.service.AuxiliarServices;

import com.homies.app.domain.*;
import com.homies.app.repository.TaskRepository;
import com.homies.app.service.*;
import com.homies.app.web.rest.TaskResource;
import com.homies.app.web.rest.errors.General.IncorrectParameters;
import com.homies.app.web.rest.errors.Task.TaskAlreadyUsedException;
import com.homies.app.web.rest.errors.Task.TaskDoesNotExist;
import com.homies.app.web.rest.errors.User.UserDoesNotExist;
import com.homies.app.web.rest.errors.User.UserDoesNotExistInGroup;
import com.homies.app.web.rest.vm.AddUserToTaskVM;
import com.homies.app.web.rest.vm.UpdateTaskVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ManageTaskAuxService {

    private final Logger log = LoggerFactory.getLogger(TaskResource.class);

    private TaskService taskService;
    private TaskQueryService taskQueryService;
    private UserDataQueryService userDataQueryService;
    private UserDataService userDataService;
    private TaskListService taskListService;
    private UserService userService;
    private GroupService groupService;
    private TaskRepository taskRepository;


    public ManageTaskAuxService(TaskService taskService,
                                TaskQueryService taskQueryService,
                                UserDataQueryService userDataQueryService,
                                UserDataService userDataService,
                                TaskListService taskListService,
                                UserService userService,
                                GroupService groupService,
                                TaskRepository taskRepository) {
        this.taskService = taskService;
        this.taskQueryService = taskQueryService;
        this.userDataQueryService = userDataQueryService;
        this.userDataService = userDataService;
        this.taskListService = taskListService;
        this.userService = userService;
        this.groupService = groupService;
        this.taskRepository = taskRepository;
    }

    private Optional<UserData> userData;
    private Optional<Task> task;
    private Optional<TaskList> taskList;
    private Optional<Group> group;

    public Optional<Task> addUserToTask(AddUserToTaskVM addUserToTaskVM){
        if (managerUserOfTheTask(addUserToTaskVM).isPresent()){
            if (taskQueryService.findByIdAndUserAssignedsUserLogin(
                addUserToTaskVM.getIdTask(),
                addUserToTaskVM.getLogin()).isPresent())
                throw new UsernameAlreadyUsedException();

            userData.get().addTaskAsigned(task.get());
            userDataService.save(userData.get());
            task.get().addUserAssigned(userData.get());
            taskService.save(task.get());
            return taskService.findOne(task.get().getId());
        }
        return Optional.empty();
    }

    public Optional<Task> updateTask(UpdateTaskVM updateTaskVM){
        if(managerUpdateOfTheTask(updateTaskVM).isPresent()){
            task = taskService.findOne(updateTaskVM.getIdTask());
            taskList = taskListService.findOne(updateTaskVM.getIdGroup());
            group = groupService.findOne(updateTaskVM.getIdGroup());
            Optional<User> user = userService.getUser(updateTaskVM.getLogin());
            userData = userDataService.findOne(user.get().getId());

            if (group.get().getUserData() != null){
                group.get().getUserData().forEach(userData1 -> {
                    if(!userData1.getId().equals(userData.get().getId())){
                        throw new UserDoesNotExistInGroup();
                    }
                });
            }

            if (!updateTaskVM.getTaskName().equals(task.get().getTaskName())){
                taskList.get().getTasks().forEach(nameTask -> {
                    if(nameTask.getTaskName().equals(updateTaskVM.getTaskName())){
                        throw new TaskAlreadyUsedException();
                    } else {
                        task.get().setTaskName(updateTaskVM.getTaskName());
                    }
                });
            }

            if(!task.get().getDescription().equals(updateTaskVM.getDescription())){
                task.get().setDescription(updateTaskVM.getDescription());
            }

            task.get().setCancel(updateTaskVM.isCancel());
            taskService.save(task.get());

            return taskService.findOne(updateTaskVM.getIdTask());
        }
        throw new IncorrectParameters();
    }

    public Optional<Task> deleteUserToTask(AddUserToTaskVM addUserToTaskVM){
        if(managerUserOfTheTask(addUserToTaskVM).isPresent()){
            if (taskQueryService.findByIdAndUserAssignedsUserLogin(
                addUserToTaskVM.getIdTask(),
                addUserToTaskVM.getLogin()).isEmpty())
                throw new UserDoesNotExist();

            deleteUser();
            return taskService.findOne(task.get().getId());
        }
        return Optional.empty();
    }

    public void deleteTask(Long id) throws Exception{
        if(taskService.findOne(id).isEmpty()){
            throw new TaskDoesNotExist();
        } else {
            List<UserData> userData = userDataQueryService.getByTaskAsignedsId(id);
            task = taskService.findOne(id);
            userData.forEach(ud -> {
                AddUserToTaskVM addUserToTaskVM = new AddUserToTaskVM();
                addUserToTaskVM.setLogin(ud.getUser().getLogin());
                addUserToTaskVM.setIdTask(id);
                addUserToTaskVM.setIdList(task.get().getTaskList().getId());
                deleteUserToTask(addUserToTaskVM);
            });
            try {
                task.get().userData(null);
                task.get().taskList(null);
                taskService.save(task.get());
                taskRepository.delete(task.get());
            }catch (Exception e){
                throw e;
            }

        }
    }

    private void deleteUser(){

        userData.get().removeTaskAsigned(task.get());
        userDataService.save(userData.get());

        task.get().removeUserAssigned(userData.get());
        taskService.save(task.get());

    }

    private Optional<Task> managerUserOfTheTask(AddUserToTaskVM addUserToTaskVM){
        task = taskService.findOne(addUserToTaskVM.getIdTask());
        taskList = taskListService.findOne(addUserToTaskVM.getIdList());
        Optional<User> user = userService.getUser(addUserToTaskVM.getLogin());
        userData = userDataService.findOne(user.get().getId());

        if (userData.isEmpty() || task.isEmpty() || taskList.isEmpty()){
            return Optional.empty();
        }
        return task;
    }

    private Optional<Task> managerUpdateOfTheTask(UpdateTaskVM updateTaskVM){
        task = taskService.findOne(updateTaskVM.getIdTask());
        taskList = taskListService.findOne(updateTaskVM.getIdGroup());
        Optional<User> user = userService.getUser(updateTaskVM.getLogin());
        userData = userDataService.findOne(user.get().getId());

        if (userData.isEmpty() || task.isEmpty() || taskList.isEmpty()){
            return Optional.empty();
        }
        return task;
    }

}
