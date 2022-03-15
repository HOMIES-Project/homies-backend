package com.homies.app.repository;

import com.homies.app.domain.Group;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.hibernate.annotations.QueryHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class GroupRepositoryWithBagRelationshipsImpl implements GroupRepositoryWithBagRelationships {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Optional<Group> fetchBagRelationships(Optional<Group> group) {
        return group.map(this::fetchUserNames);
    }

    @Override
    public Page<Group> fetchBagRelationships(Page<Group> groups) {
        return new PageImpl<>(fetchBagRelationships(groups.getContent()), groups.getPageable(), groups.getTotalElements());
    }

    @Override
    public List<Group> fetchBagRelationships(List<Group> groups) {
        return Optional.of(groups).map(this::fetchUserNames).get();
    }

    Group fetchUserNames(Group result) {
        return entityManager
            .createQuery("select group from Group group left join fetch group.userNames where group is :group", Group.class)
            .setParameter("group", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Group> fetchUserNames(List<Group> groups) {
        return entityManager
            .createQuery("select distinct group from Group group left join fetch group.userNames where group in :groups", Group.class)
            .setParameter("groups", groups)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
