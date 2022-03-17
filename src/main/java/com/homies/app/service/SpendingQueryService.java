package com.homies.app.service;

import com.homies.app.domain.*; // for static metamodels
import com.homies.app.domain.Spending;
import com.homies.app.repository.SpendingRepository;
import com.homies.app.service.criteria.SpendingCriteria;
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
 * Service for executing complex queries for {@link Spending} entities in the database.
 * The main input is a {@link SpendingCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Spending} or a {@link Page} of {@link Spending} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SpendingQueryService extends QueryService<Spending> {

    private final Logger log = LoggerFactory.getLogger(SpendingQueryService.class);

    private final SpendingRepository spendingRepository;

    public SpendingQueryService(SpendingRepository spendingRepository) {
        this.spendingRepository = spendingRepository;
    }

    /**
     * Return a {@link List} of {@link Spending} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Spending> findByCriteria(SpendingCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Spending> specification = createSpecification(criteria);
        return spendingRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Spending} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Spending> findByCriteria(SpendingCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Spending> specification = createSpecification(criteria);
        return spendingRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SpendingCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Spending> specification = createSpecification(criteria);
        return spendingRepository.count(specification);
    }

    /**
     * Function to convert {@link SpendingCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Spending> createSpecification(SpendingCriteria criteria) {
        Specification<Spending> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Spending_.id));
            }
            if (criteria.getPayer() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPayer(), Spending_.payer));
            }
            if (criteria.getNameCost() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNameCost(), Spending_.nameCost));
            }
            if (criteria.getCost() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCost(), Spending_.cost));
            }
            if (criteria.getDescripcion() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescripcion(), Spending_.descripcion));
            }
            if (criteria.getPaid() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPaid(), Spending_.paid));
            }
            if (criteria.getPending() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPending(), Spending_.pending));
            }
            if (criteria.getFinished() != null) {
                specification = specification.and(buildSpecification(criteria.getFinished(), Spending_.finished));
            }
        }
        return specification;
    }
}
