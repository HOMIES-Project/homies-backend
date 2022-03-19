package com.homies.app.service;

import com.homies.app.domain.*; // for static metamodels
import com.homies.app.domain.Products;
import com.homies.app.repository.ProductsRepository;
import com.homies.app.service.criteria.ProductsCriteria;
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
 * Service for executing complex queries for {@link Products} entities in the database.
 * The main input is a {@link ProductsCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Products} or a {@link Page} of {@link Products} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProductsQueryService extends QueryService<Products> {

    private final Logger log = LoggerFactory.getLogger(ProductsQueryService.class);

    private final ProductsRepository productsRepository;

    public ProductsQueryService(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    /**
     * Return a {@link List} of {@link Products} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Products> findByCriteria(ProductsCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Products> specification = createSpecification(criteria);
        return productsRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Products} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Products> findByCriteria(ProductsCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Products> specification = createSpecification(criteria);
        return productsRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProductsCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Products> specification = createSpecification(criteria);
        return productsRepository.count(specification);
    }

    /**
     * Function to convert {@link ProductsCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Products> createSpecification(ProductsCriteria criteria) {
        Specification<Products> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Products_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Products_.name));
            }
            if (criteria.getPrice() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPrice(), Products_.price));
            }
            if (criteria.getUnits() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUnits(), Products_.units));
            }
            if (criteria.getTypeUnit() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTypeUnit(), Products_.typeUnit));
            }
            if (criteria.getNote() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNote(), Products_.note));
            }
            if (criteria.getDataCreated() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDataCreated(), Products_.dataCreated));
            }
            if (criteria.getShoppingDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getShoppingDate(), Products_.shoppingDate));
            }
            if (criteria.getPurchased() != null) {
                specification = specification.and(buildSpecification(criteria.getPurchased(), Products_.purchased));
            }
            if (criteria.getUserCreated() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUserCreated(), Products_.userCreated));
            }
            if (criteria.getUserCreatorId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getUserCreatorId(),
                            root -> root.join(Products_.userCreator, JoinType.LEFT).get(UserData_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
