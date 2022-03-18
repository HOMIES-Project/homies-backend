package com.homies.app.repository;

import com.homies.app.domain.UserData;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the UserData entity.
 */
@Repository
public interface UserDataRepository
    extends UserDataRepositoryWithBagRelationships, JpaRepository<UserData, Long>, JpaSpecificationExecutor<UserData> {
    default Optional<UserData> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<UserData> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<UserData> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select distinct userData from UserData userData left join fetch userData.user",
        countQuery = "select count(distinct userData) from UserData userData"
    )
    Page<UserData> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct userData from UserData userData left join fetch userData.user")
    List<UserData> findAllWithToOneRelationships();

    @Query("select userData from UserData userData left join fetch userData.user where userData.id =:id")
    Optional<UserData> findOneWithToOneRelationships(@Param("id") Long id);
}
