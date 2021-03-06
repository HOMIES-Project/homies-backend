package com.homies.app.service;

import com.homies.app.domain.*; // for static metamodels
import com.homies.app.domain.UserData;
import com.homies.app.repository.UserDataRepository;
import com.homies.app.service.criteria.UserDataCriteria;
import java.util.List;
import java.util.Optional;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link UserData} entities in the database.
 * The main input is a {@link UserDataCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link UserData} or a {@link Page} of {@link UserData} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UserDataQueryService extends QueryService<UserData> {

    private final Logger log = LoggerFactory.getLogger(UserDataQueryService.class);
    @Autowired
    private final UserDataRepository userDataRepository;

    public UserDataQueryService(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    /**
     * Get user with login user
     * @param login
     * @return entity
     */
    @Transactional
    public Optional<UserData> getByUser_Login(String login){
        return userDataRepository.getByUser_Login(login);
    }

    /**
     * Only for refresh entity
     */
    public void refreshUserDataEntity() {
        userDataRepository.flush();
    }

    /**
     * Return a {@link List} of {@link UserData} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<UserData> findByCriteria(UserDataCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<UserData> specification = createSpecification(criteria);
        return userDataRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link UserData} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<UserData> findByCriteria(UserDataCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<UserData> specification = createSpecification(criteria);
        return userDataRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UserDataCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<UserData> specification = createSpecification(criteria);
        return userDataRepository.count(specification);
    }

    /**
     * Function to convert {@link UserDataCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<UserData> createSpecification(UserDataCriteria criteria) {
        Specification<UserData> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), UserData_.id));
            }
            if (criteria.getPhone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhone(), UserData_.phone));
            }
            if (criteria.getPremium() != null) {
                specification = specification.and(buildSpecification(criteria.getPremium(), UserData_.premium));
            }
            if (criteria.getBirthDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getBirthDate(), UserData_.birthDate));
            }
            if (criteria.getAddDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAddDate(), UserData_.addDate));
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(UserData_.user, JoinType.LEFT).get(User_.id))
                    );
            }
            if (criteria.getAdminGroupsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getAdminGroupsId(),
                            root -> root.join(UserData_.adminGroups, JoinType.LEFT).get(Group_.id)
                        )
                    );
            }
            if (criteria.getTaskAsignedId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTaskAsignedId(),
                            root -> root.join(UserData_.taskAsigneds, JoinType.LEFT).get(Task_.id)
                        )
                    );
            }
            if (criteria.getProductCreatedId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getProductCreatedId(),
                            root -> root.join(UserData_.productCreateds, JoinType.LEFT).get(Products_.id)
                        )
                    );
            }
            if (criteria.getGroupId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getGroupId(), root -> root.join(UserData_.groups, JoinType.LEFT).get(Group_.id))
                    );
            }
        }
        return specification;
    }
}
