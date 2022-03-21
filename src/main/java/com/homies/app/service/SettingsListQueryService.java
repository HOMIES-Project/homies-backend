package com.homies.app.service;

import com.homies.app.domain.*; // for static metamodels
import com.homies.app.domain.SettingsList;
import com.homies.app.repository.SettingsListRepository;
import com.homies.app.service.criteria.SettingsListCriteria;
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
 * Service for executing complex queries for {@link SettingsList} entities in the database.
 * The main input is a {@link SettingsListCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SettingsList} or a {@link Page} of {@link SettingsList} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SettingsListQueryService extends QueryService<SettingsList> {

    private final Logger log = LoggerFactory.getLogger(SettingsListQueryService.class);

    private final SettingsListRepository settingsListRepository;

    public SettingsListQueryService(SettingsListRepository settingsListRepository) {
        this.settingsListRepository = settingsListRepository;
    }

    /**
     * Return a {@link List} of {@link SettingsList} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SettingsList> findByCriteria(SettingsListCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<SettingsList> specification = createSpecification(criteria);
        return settingsListRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link SettingsList} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SettingsList> findByCriteria(SettingsListCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SettingsList> specification = createSpecification(criteria);
        return settingsListRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SettingsListCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<SettingsList> specification = createSpecification(criteria);
        return settingsListRepository.count(specification);
    }

    /**
     * Function to convert {@link SettingsListCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SettingsList> createSpecification(SettingsListCriteria criteria) {
        Specification<SettingsList> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), SettingsList_.id));
            }
            if (criteria.getSettingOne() != null) {
                specification = specification.and(buildSpecification(criteria.getSettingOne(), SettingsList_.settingOne));
            }
            if (criteria.getSettingTwo() != null) {
                specification = specification.and(buildSpecification(criteria.getSettingTwo(), SettingsList_.settingTwo));
            }
            if (criteria.getSettingThree() != null) {
                specification = specification.and(buildSpecification(criteria.getSettingThree(), SettingsList_.settingThree));
            }
            if (criteria.getSettingFour() != null) {
                specification = specification.and(buildSpecification(criteria.getSettingFour(), SettingsList_.settingFour));
            }
            if (criteria.getSettingFive() != null) {
                specification = specification.and(buildSpecification(criteria.getSettingFive(), SettingsList_.settingFive));
            }
            if (criteria.getSettingSix() != null) {
                specification = specification.and(buildSpecification(criteria.getSettingSix(), SettingsList_.settingSix));
            }
            if (criteria.getSettingSeven() != null) {
                specification = specification.and(buildSpecification(criteria.getSettingSeven(), SettingsList_.settingSeven));
            }
            if (criteria.getSpendingListId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getSpendingListId(),
                            root -> root.join(SettingsList_.spendingList, JoinType.LEFT).get(SpendingList_.id)
                        )
                    );
            }
            if (criteria.getUserPendingId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getUserPendingId(),
                            root -> root.join(SettingsList_.userPendings, JoinType.LEFT).get(UserPending_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
