package com.homies.app.service.impl;

import com.homies.app.domain.TaskList;
import com.homies.app.repository.TaskListRepository;
import com.homies.app.service.TaskListService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link TaskList}.
 */
@Service
@Transactional
public class TaskListServiceImpl implements TaskListService {

    private final Logger log = LoggerFactory.getLogger(TaskListServiceImpl.class);

    private final TaskListRepository taskListRepository;

    public TaskListServiceImpl(TaskListRepository taskListRepository) {
        this.taskListRepository = taskListRepository;
    }

    @Override
    public TaskList save(TaskList taskList) {
        log.debug("Request to save TaskList : {}", taskList);
        return taskListRepository.save(taskList);
    }

    @Override
    public Optional<TaskList> partialUpdate(TaskList taskList) {
        log.debug("Request to partially update TaskList : {}", taskList);

        return taskListRepository
            .findById(taskList.getId())
            .map(existingTaskList -> {
                if (taskList.getNameList() != null) {
                    existingTaskList.setNameList(taskList.getNameList());
                }

                return existingTaskList;
            })
            .map(taskListRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskList> findAll(Pageable pageable) {
        log.debug("Request to get all TaskLists");
        return taskListRepository.findAll(pageable);
    }

    /**
     *  Get all the taskLists where Group is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<TaskList> findAllWhereGroupIsNull() {
        log.debug("Request to get all taskLists where Group is null");
        return StreamSupport
            .stream(taskListRepository.findAll().spliterator(), false)
            .filter(taskList -> taskList.getGroup() == null)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TaskList> findOne(Long id) {
        log.debug("Request to get TaskList : {}", id);
        return taskListRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete TaskList : {}", id);
        taskListRepository.deleteById(id);
    }
}
