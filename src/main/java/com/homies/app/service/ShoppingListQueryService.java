package com.homies.app.service;

import com.homies.app.domain.*; // for static metamodels
import com.homies.app.domain.ShoppingList;
import com.homies.app.repository.ShoppingListRepository;
import com.homies.app.service.criteria.ShoppingListCriteria;
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
 * Service for executing complex queries for {@link ShoppingList} entities in the database.
 * The main input is a {@link ShoppingListCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ShoppingList} or a {@link Page} of {@link ShoppingList} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ShoppingListQueryService extends QueryService<ShoppingList> {

    private final Logger log = LoggerFactory.getLogger(ShoppingListQueryService.class);

    private final ShoppingListRepository shoppingListRepository;

    public ShoppingListQueryService(ShoppingListRepository shoppingListRepository) {
        this.shoppingListRepository = shoppingListRepository;
    }

    /**
     * Return a {@link List} of {@link ShoppingList} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ShoppingList> findByCriteria(ShoppingListCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ShoppingList> specification = createSpecification(criteria);
        return shoppingListRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link ShoppingList} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ShoppingList> findByCriteria(ShoppingListCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ShoppingList> specification = createSpecification(criteria);
        return shoppingListRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ShoppingListCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ShoppingList> specification = createSpecification(criteria);
        return shoppingListRepository.count(specification);
    }

    /**
     * Function to convert {@link ShoppingListCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ShoppingList> createSpecification(ShoppingListCriteria criteria) {
        Specification<ShoppingList> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ShoppingList_.id));
            }
            if (criteria.getTotal() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTotal(), ShoppingList_.total));
            }
            if (criteria.getNameShopList() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNameShopList(), ShoppingList_.nameShopList));
            }
        }
        return specification;
    }
}
