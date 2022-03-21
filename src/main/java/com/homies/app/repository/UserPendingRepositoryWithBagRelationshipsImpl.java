package com.homies.app.repository;

import com.homies.app.domain.UserPending;
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
public class UserPendingRepositoryWithBagRelationshipsImpl implements UserPendingRepositoryWithBagRelationships {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Optional<UserPending> fetchBagRelationships(Optional<UserPending> userPending) {
        return userPending.map(this::fetchSpendings);
    }

    @Override
    public Page<UserPending> fetchBagRelationships(Page<UserPending> userPendings) {
        return new PageImpl<>(
            fetchBagRelationships(userPendings.getContent()),
            userPendings.getPageable(),
            userPendings.getTotalElements()
        );
    }

    @Override
    public List<UserPending> fetchBagRelationships(List<UserPending> userPendings) {
        return Optional.of(userPendings).map(this::fetchSpendings).get();
    }

    UserPending fetchSpendings(UserPending result) {
        return entityManager
            .createQuery(
                "select userPending from UserPending userPending left join fetch userPending.spendings where userPending is :userPending",
                UserPending.class
            )
            .setParameter("userPending", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<UserPending> fetchSpendings(List<UserPending> userPendings) {
        return entityManager
            .createQuery(
                "select distinct userPending from UserPending userPending left join fetch userPending.spendings where userPending in :userPendings",
                UserPending.class
            )
            .setParameter("userPendings", userPendings)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
