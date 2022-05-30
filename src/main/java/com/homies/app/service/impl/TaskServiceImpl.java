package com.homies.app.service.impl;

import com.homies.app.domain.Task;
import com.homies.app.repository.TaskRepository;
import com.homies.app.service.TaskService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Task}.
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task save(Task task) {
        log.debug("Request to save Task : {}", task);
        return taskRepository.save(task);
    }

    @Override
    public Optional<Task> partialUpdate(Task task) {
        log.debug("Request to partially update Task : {}", task);

        return taskRepository
            .findById(task.getId())
            .map(existingTask -> {
                if (task.getTaskName() != null) {
                    existingTask.setTaskName(task.getTaskName());
                }
                if (task.getDataCreate() != null) {
                    existingTask.setDataCreate(task.getDataCreate());
                }
                if (task.getDataEnd() != null) {
                    existingTask.setDataEnd(task.getDataEnd());
                }
                if (task.getDescription() != null) {
                    existingTask.setDescription(task.getDescription());
                }
                if (task.getCancel() != null) {
                    existingTask.setCancel(task.getCancel());
                }
                if (task.getPhoto() != null) {
                    existingTask.setPhoto(task.getPhoto());
                }
                if (task.getPhotoContentType() != null) {
                    existingTask.setPhotoContentType(task.getPhotoContentType());
                }
                if (task.getPuntuacion() != null) {
                    existingTask.setPuntuacion(task.getPuntuacion());
                }

                return existingTask;
            })
            .map(taskRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Task> findAll(Pageable pageable) {
        log.debug("Request to get all Tasks");
        return taskRepository.findAll(pageable);
    }

    public Page<Task> findAllWithEagerRelationships(Pageable pageable) {
        return taskRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Task> findOne(Long id) {
        log.debug("Request to get Task : {}", id);
        return taskRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Task : {}", id);
        taskRepository.deleteById(id);
    }
}
