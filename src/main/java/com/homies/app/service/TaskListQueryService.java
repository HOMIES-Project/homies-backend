package com.homies.app.service;

import com.homies.app.domain.*; // for static metamodels
import com.homies.app.domain.TaskList;
import com.homies.app.repository.TaskListRepository;
import com.homies.app.service.criteria.TaskListCriteria;
import java.util.List;
import java.util.Optional;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link TaskList} entities in the database.
 * The main input is a {@link TaskListCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TaskList} or a {@link Page} of {@link TaskList} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TaskListQueryService extends QueryService<TaskList> {

    private final Logger log = LoggerFactory.getLogger(TaskListQueryService.class);

    private final TaskListRepository taskListRepository;

    public TaskListQueryService(TaskListRepository taskListRepository) {
        this.taskListRepository = taskListRepository;
    }

    /**
     * Return a {@link List} of {@link TaskList} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TaskList> findByCriteria(TaskListCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<TaskList> specification = createSpecification(criteria);
        return taskListRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link TaskList} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TaskList> findByCriteria(TaskListCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TaskList> specification = createSpecification(criteria);
        return taskListRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TaskListCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<TaskList> specification = createSpecification(criteria);
        return taskListRepository.count(specification);
    }

    /**
     * Find Task in TaskList
     * @param id and taskName
     * @return Optional
     */
    public Optional<TaskList> findByIdAndTasks_TaskName(Long id, String taskName){
        return taskListRepository.findByIdAndTasks_TaskName(id, taskName);
    }

   @Transactional(readOnly = true)
   public Optional<TaskList> findByTasks_UserData_Id(Long id){
        return taskListRepository.findByTasks_UserData_Id(id);
   }

   @Transactional(readOnly = true)
   public Optional<TaskList> findByTasks_UserAssigneds_Id(Long id){
        return taskListRepository.findByTasks_UserAssigneds_Id(id);
   }

   @Transactional(readOnly = true)
   public Optional<TaskList> getByIdAndTasksUserAssignedsId(Long TaskList, Long idUserData){
        return taskListRepository.getByIdAndTasks_UserAssigneds_Id(TaskList, idUserData);
   }

    public void refreshTaskListEntity() {
        taskListRepository.flush();
    }

    /**
     * Function to convert {@link TaskListCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TaskList> createSpecification(TaskListCriteria criteria) {
        Specification<TaskList> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TaskList_.id));
            }
            if (criteria.getNameList() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNameList(), TaskList_.nameList));
            }
            if (criteria.getGroupId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getGroupId(), root -> root.join(TaskList_.group, JoinType.LEFT).get(Group_.id))
                    );
            }
            if (criteria.getTaskId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getTaskId(), root -> root.join(TaskList_.tasks, JoinType.LEFT).get(Task_.id))
                    );
            }
        }
        return specification;
    }
}
