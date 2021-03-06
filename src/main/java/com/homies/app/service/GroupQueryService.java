package com.homies.app.service;

import com.homies.app.domain.*; // for static metamodels
import com.homies.app.domain.Group;
import com.homies.app.repository.GroupRepository;
import com.homies.app.service.criteria.GroupCriteria;

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
 * Service for executing complex queries for {@link Group} entities in the database.
 * The main input is a {@link GroupCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Group} or a {@link Page} of {@link Group} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class GroupQueryService extends QueryService<Group> {

    private final Logger log = LoggerFactory.getLogger(GroupQueryService.class);

    @Autowired
    private final GroupRepository groupRepository;

    public GroupQueryService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Transactional
    public void updateUserAdmin(UserData userAdmin, Long idGroup, UserData userData) {
        groupRepository.updateUserAdmin(userAdmin, idGroup, userData);
    }

    @Transactional(readOnly = true)
    public List<Group> getAllGroupsUserId(Long userId, Long userDataId) {
        return groupRepository.getDistinctByUserAdmin_IdOrUserData_Id(userId, userDataId);
    }

    @Transactional(readOnly = true)
    public List<Group> getUseGroupsByUserDataId(Long id) {
        return groupRepository.getByUserData_Id(id);
    }

    @Transactional(readOnly = true)
    public List<Group> getAdminGroupsByUserDataId(Long id) {
        return groupRepository.getByUserAdmin_Id(id);
    }

    @Transactional(readOnly = true)
    public Optional<Group> findGroupByIdAndUserDataUserLogin(Long id, String login) {
        return groupRepository.findByIdAndUserData_User_Login(id, login);
    }

    public void refreshGroupEntity() {
        groupRepository.flush();
    }

    /**
     * find group by name
     *
     * @param nameGroup
     * @return boolean
     */
    @Transactional(readOnly = true)
    public boolean findOneByName(String nameGroup) {
        return groupRepository.existsByGroupName(nameGroup);
    }

    /**
     * Return a {@link List} of {@link Group} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Group> findByCriteria(GroupCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Group> specification = createSpecification(criteria);
        return groupRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Group} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Group> findByCriteria(GroupCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Group> specification = createSpecification(criteria);
        return groupRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(GroupCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Group> specification = createSpecification(criteria);
        return groupRepository.count(specification);
    }

    /**
     * Function to convert {@link GroupCriteria} to a {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Group> createSpecification(GroupCriteria criteria) {
        Specification<Group> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Group_.id));
            }
            if (criteria.getGroupKey() != null) {
                specification = specification.and(buildStringSpecification(criteria.getGroupKey(), Group_.groupKey));
            }
            if (criteria.getGroupName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getGroupName(), Group_.groupName));
            }
            if (criteria.getGroupRelationName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getGroupRelationName(), Group_.groupRelationName));
            }
            if (criteria.getAddGroupDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAddGroupDate(), Group_.addGroupDate));
            }
            if (criteria.getUserAdminId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserAdminId(), root -> root.join(Group_.userAdmin, JoinType.LEFT).get(UserData_.id))
                    );
            }
            if (criteria.getTaskListId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getTaskListId(), root -> root.join(Group_.taskList, JoinType.LEFT).get(TaskList_.id))
                    );
            }
            if (criteria.getSpendingListId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getSpendingListId(),
                            root -> root.join(Group_.spendingList, JoinType.LEFT).get(SpendingList_.id)
                        )
                    );
            }
            if (criteria.getShoppingListId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getShoppingListId(),
                            root -> root.join(Group_.shoppingList, JoinType.LEFT).get(ShoppingList_.id)
                        )
                    );
            }
            if (criteria.getSettingsListId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getSettingsListId(),
                            root -> root.join(Group_.settingsList, JoinType.LEFT).get(SettingsList_.id)
                        )
                    );
            }
            if (criteria.getUserDataId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserDataId(), root -> root.join(Group_.userData, JoinType.LEFT).get(UserData_.id))
                    );
            }
        }
        return specification;
    }
}
