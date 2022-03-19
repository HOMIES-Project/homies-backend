package com.homies.app.repository;

import com.homies.app.domain.UserPending;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the UserPending entity.
 */
@Repository
public interface UserPendingRepository
    extends UserPendingRepositoryWithBagRelationships, JpaRepository<UserPending, Long>, JpaSpecificationExecutor<UserPending> {
    default Optional<UserPending> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<UserPending> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<UserPending> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select distinct userPending from UserPending userPending left join fetch userPending.spendingList",
        countQuery = "select count(distinct userPending) from UserPending userPending"
    )
    Page<UserPending> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct userPending from UserPending userPending left join fetch userPending.spendingList")
    List<UserPending> findAllWithToOneRelationships();

    @Query("select userPending from UserPending userPending left join fetch userPending.spendingList where userPending.id =:id")
    Optional<UserPending> findOneWithToOneRelationships(@Param("id") Long id);
}
