package com.homies.app.service.AuxiliarServices;

import com.homies.app.domain.*;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ManageTaskAuxService {

    private final Logger log = LoggerFactory.getLogger(TaskResource.class);
    @Autowired
    private final TaskService taskService;
    @Autowired
    private final TaskQueryService taskQueryService;
    @Autowired
    private final UserDataQueryService userDataQueryService;
    @Autowired
    private final UserDataService userDataService;
    @Autowired
    private final TaskListService taskListService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final GroupService groupService;



    public ManageTaskAuxService(TaskService taskService,
                                TaskQueryService taskQueryService,
                                UserDataQueryService userDataQueryService,
                                UserDataService userDataService,
                                TaskListService taskListService,
                                UserService userService,
                                GroupService groupService) {
        this.taskService = taskService;
        this.taskQueryService = taskQueryService;
        this.userDataQueryService = userDataQueryService;
        this.userDataService = userDataService;
        this.taskListService = taskListService;
        this.userService = userService;
        this.groupService = groupService;
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

            AtomicBoolean userExist = new AtomicBoolean(false);
            if (group.get().getUserData() != null){
                group.get().getUserData().forEach(userData1 -> {
                    if(userData1.getId().equals(userData.get().getId())){
                        userExist.set(true);
                    }
                });


                if(userExist.get()){
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

                    taskService.save(task.get());
                } else {
                    throw new UserDoesNotExistInGroup();
                }



            }



            return taskService.findOne(updateTaskVM.getIdTask());
        }
        throw new IncorrectParameters();
    }

    public Optional<Task> updateTaskCancel(UpdateTaskVM updateTaskVM){
        if(managerUpdateOfTheTask(updateTaskVM).isPresent()){
            task = taskService.findOne(updateTaskVM.getIdTask());
            group = groupService.findOne(updateTaskVM.getIdGroup());
            Optional<User> user = userService.getUser(updateTaskVM.getLogin());
            userData = userDataService.findOne(user.get().getId());

            AtomicBoolean userExist = new AtomicBoolean(false);
            if (group.get().getUserData() != null){
                group.get().getUserData().forEach(userData1 -> {
                    if(userData1.getId().equals(userData.get().getId())){
                        userExist.set(true);
                    }
                });
                if(userExist.get()){
                    task.get().setCancel(updateTaskVM.isCancel());
                    taskService.save(task.get());
                } else {
                    throw new UserDoesNotExistInGroup();
                }
            }
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
            try {
                task = taskService.findOne(id);
                Set<UserData> usersData = task.get().getUserAssigneds();
                TaskList taskList = new TaskList();
                boolean isTaskList = false;
                if (task.get().getTaskList() != null){
                    isTaskList = true;
                    taskList = task.get().getTaskList();
                }

                usersData.forEach(userData -> {
                    userData.removeTaskAsigned(task.get());
                    userDataService.save(userData);
                });

                task.get().setTaskList(null);
                taskService.save(task.get());

                if (isTaskList){
                    taskList.removeTask(task.get());
                    taskListService.save(taskList);
                }

                taskService.delete(task.get().getId());

            }catch (Exception e){
                e.printStackTrace();
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
