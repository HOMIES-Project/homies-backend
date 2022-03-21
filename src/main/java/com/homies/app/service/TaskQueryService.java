package com.homies.app.service;

import com.homies.app.domain.*; // for static metamodels
import com.homies.app.domain.Task;
import com.homies.app.repository.TaskRepository;
import com.homies.app.service.criteria.TaskCriteria;
import java.util.List;
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
 * Service for executing complex queries for {@link Task} entities in the database.
 * The main input is a {@link TaskCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Task} or a {@link Page} of {@link Task} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TaskQueryService extends QueryService<Task> {

    private final Logger log = LoggerFactory.getLogger(TaskQueryService.class);

    private final TaskRepository taskRepository;

    public TaskQueryService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Return a {@link List} of {@link Task} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Task> findByCriteria(TaskCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Task> specification = createSpecification(criteria);
        return taskRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Task} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Task> findByCriteria(TaskCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Task> specification = createSpecification(criteria);
        return taskRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TaskCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Task> specification = createSpecification(criteria);
        return taskRepository.count(specification);
    }

    /**
     * Function to convert {@link TaskCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Task> createSpecification(TaskCriteria criteria) {
        Specification<Task> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Task_.id));
            }
            if (criteria.getTaskName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTaskName(), Task_.taskName));
            }
            if (criteria.getDataCreate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDataCreate(), Task_.dataCreate));
            }
            if (criteria.getDataEnd() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDataEnd(), Task_.dataEnd));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Task_.description));
            }
            if (criteria.getCancel() != null) {
                specification = specification.and(buildSpecification(criteria.getCancel(), Task_.cancel));
            }
            if (criteria.getPuntuacion() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPuntuacion(), Task_.puntuacion));
            }
            if (criteria.getTaskListId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getTaskListId(), root -> root.join(Task_.taskList, JoinType.LEFT).get(TaskList_.id))
                    );
            }
            if (criteria.getUserDataId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserDataId(), root -> root.join(Task_.userData, JoinType.LEFT).get(UserData_.id))
                    );
            }
            if (criteria.getUserCreatorId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getUserCreatorId(),
                            root -> root.join(Task_.userCreator, JoinType.LEFT).get(UserData_.id)
                        )
                    );
            }
            if (criteria.getUserAssignedId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getUserAssignedId(),
                            root -> root.join(Task_.userAssigneds, JoinType.LEFT).get(UserData_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
