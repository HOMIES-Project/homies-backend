package com.homies.app.service;

import com.homies.app.domain.*; // for static metamodels
import com.homies.app.domain.SpendingList;
import com.homies.app.repository.SpendingListRepository;
import com.homies.app.service.criteria.SpendingListCriteria;
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
 * Service for executing complex queries for {@link SpendingList} entities in the database.
 * The main input is a {@link SpendingListCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SpendingList} or a {@link Page} of {@link SpendingList} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SpendingListQueryService extends QueryService<SpendingList> {

    private final Logger log = LoggerFactory.getLogger(SpendingListQueryService.class);

    private final SpendingListRepository spendingListRepository;

    public SpendingListQueryService(SpendingListRepository spendingListRepository) {
        this.spendingListRepository = spendingListRepository;
    }

    /**
     * Return a {@link List} of {@link SpendingList} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SpendingList> findByCriteria(SpendingListCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<SpendingList> specification = createSpecification(criteria);
        return spendingListRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link SpendingList} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SpendingList> findByCriteria(SpendingListCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SpendingList> specification = createSpecification(criteria);
        return spendingListRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SpendingListCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<SpendingList> specification = createSpecification(criteria);
        return spendingListRepository.count(specification);
    }

    /**
     * Function to convert {@link SpendingListCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SpendingList> createSpecification(SpendingListCriteria criteria) {
        Specification<SpendingList> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), SpendingList_.id));
            }
            if (criteria.getTotal() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTotal(), SpendingList_.total));
            }
            if (criteria.getNameSpendList() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNameSpendList(), SpendingList_.nameSpendList));
            }
            if (criteria.getSpendingId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getSpendingId(),
                            root -> root.join(SpendingList_.spendings, JoinType.LEFT).get(UserPending_.id)
                        )
                    );
            }
            if (criteria.getGroupId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getGroupId(), root -> root.join(SpendingList_.group, JoinType.LEFT).get(Group_.id))
                    );
            }
        }
        return specification;
    }
}
