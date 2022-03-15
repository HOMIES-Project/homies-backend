package com.homies.app.service;

import com.homies.app.domain.*; // for static metamodels
import com.homies.app.domain.UserName;
import com.homies.app.repository.UserNameRepository;
import com.homies.app.service.criteria.UserNameCriteria;
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
 * Service for executing complex queries for {@link UserName} entities in the database.
 * The main input is a {@link UserNameCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link UserName} or a {@link Page} of {@link UserName} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UserNameQueryService extends QueryService<UserName> {

    private final Logger log = LoggerFactory.getLogger(UserNameQueryService.class);

    private final UserNameRepository userNameRepository;

    public UserNameQueryService(UserNameRepository userNameRepository) {
        this.userNameRepository = userNameRepository;
    }

    /**
     * Return a {@link List} of {@link UserName} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<UserName> findByCriteria(UserNameCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<UserName> specification = createSpecification(criteria);
        return userNameRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link UserName} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<UserName> findByCriteria(UserNameCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<UserName> specification = createSpecification(criteria);
        return userNameRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UserNameCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<UserName> specification = createSpecification(criteria);
        return userNameRepository.count(specification);
    }

    /**
     * Function to convert {@link UserNameCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<UserName> createSpecification(UserNameCriteria criteria) {
        Specification<UserName> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), UserName_.id));
            }
            if (criteria.getUser_name() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUser_name(), UserName_.user_name));
            }
            if (criteria.getPassword() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPassword(), UserName_.password));
            }
            if (criteria.getToken() != null) {
                specification = specification.and(buildStringSpecification(criteria.getToken(), UserName_.token));
            }
            if (criteria.getUserDataId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserDataId(), root -> root.join(UserName_.userData, JoinType.LEFT).get(UserData_.id))
                    );
            }
            if (criteria.getGroupId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getGroupId(), root -> root.join(UserName_.groups, JoinType.LEFT).get(Group_.id))
                    );
            }
        }
        return specification;
    }
}
