package com.homies.app.repository;

import com.homies.app.domain.UserData;
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
public class UserDataRepositoryWithBagRelationshipsImpl implements UserDataRepositoryWithBagRelationships {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Optional<UserData> fetchBagRelationships(Optional<UserData> userData) {
        return userData.map(this::fetchTaskAsigneds).map(this::fetchGroups);
    }

    @Override
    public Page<UserData> fetchBagRelationships(Page<UserData> userData) {
        return new PageImpl<>(fetchBagRelationships(userData.getContent()), userData.getPageable(), userData.getTotalElements());
    }

    @Override
    public List<UserData> fetchBagRelationships(List<UserData> userData) {
        return Optional.of(userData).map(this::fetchTaskAsigneds).map(this::fetchGroups).get();
    }

    UserData fetchTaskAsigneds(UserData result) {
        return entityManager
            .createQuery(
                "select userData from UserData userData left join fetch userData.taskAsigneds where userData is :userData",
                UserData.class
            )
            .setParameter("userData", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<UserData> fetchTaskAsigneds(List<UserData> userData) {
        return entityManager
            .createQuery(
                "select distinct userData from UserData userData left join fetch userData.taskAsigneds where userData in :userData",
                UserData.class
            )
            .setParameter("userData", userData)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }

    UserData fetchGroups(UserData result) {
        return entityManager
            .createQuery(
                "select userData from UserData userData left join fetch userData.groups where userData is :userData",
                UserData.class
            )
            .setParameter("userData", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<UserData> fetchGroups(List<UserData> userData) {
        return entityManager
            .createQuery(
                "select distinct userData from UserData userData left join fetch userData.groups where userData in :userData",
                UserData.class
            )
            .setParameter("userData", userData)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
