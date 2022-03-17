package com.homies.app.service;

import com.homies.app.domain.*; // for static metamodels
import com.homies.app.domain.UserPending;
import com.homies.app.repository.UserPendingRepository;
import com.homies.app.service.criteria.UserPendingCriteria;
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
 * Service for executing complex queries for {@link UserPending} entities in the database.
 * The main input is a {@link UserPendingCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link UserPending} or a {@link Page} of {@link UserPending} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UserPendingQueryService extends QueryService<UserPending> {

    private final Logger log = LoggerFactory.getLogger(UserPendingQueryService.class);

    private final UserPendingRepository userPendingRepository;

    public UserPendingQueryService(UserPendingRepository userPendingRepository) {
        this.userPendingRepository = userPendingRepository;
    }

    /**
     * Return a {@link List} of {@link UserPending} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<UserPending> findByCriteria(UserPendingCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<UserPending> specification = createSpecification(criteria);
        return userPendingRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link UserPending} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<UserPending> findByCriteria(UserPendingCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<UserPending> specification = createSpecification(criteria);
        return userPendingRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UserPendingCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<UserPending> specification = createSpecification(criteria);
        return userPendingRepository.count(specification);
    }

    /**
     * Function to convert {@link UserPendingCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<UserPending> createSpecification(UserPendingCriteria criteria) {
        Specification<UserPending> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), UserPending_.id));
            }
            if (criteria.getPending() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPending(), UserPending_.pending));
            }
            if (criteria.getPaid() != null) {
                specification = specification.and(buildSpecification(criteria.getPaid(), UserPending_.paid));
            }
        }
        return specification;
    }
}
