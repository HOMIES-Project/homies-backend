package com.homies.app.repository;

import com.homies.app.domain.Group;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.homies.app.domain.UserData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data SQL repository for the Group entity.
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, Long>, JpaSpecificationExecutor<Group> {
    //String GROUP_BY_USERDATA = "userData"

    default Optional<Group> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Group> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Group> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    Optional<Group> findByGroupName(String groupName);

    boolean existsByGroupName(String groupName);

    List<Group> getDistinctByUserAdmin_IdOrUserData_Id(Long id, Long id1);

    List<Group> getByUserData_Id(Long id);

    List<Group> getByUserAdmin_Id(Long id);


    /** Change user admin */
    @Transactional
    @Modifying
    @Query("update Group g set g.userAdmin = :userAdmin1 where g.id = :id and g.userAdmin = :userAdmin")
    int updateUserAdmin(@Param("userAdmin") UserData userAdmin,
                        @Param("id") Long id,
                        @Param("userAdmin1") UserData userAdmin1);


    /** Delete the Group by id. */
    @Transactional
    @Modifying
    @Query("delete from Group g where g.id = :id and g.userAdmin = :userAdmin")
    int deleteByIdAndUserAdmin(
        @Param("id") Long id,
        @Param("userAdmin") UserData userAdmin);

    Optional<Group> findByIdAndUserData_User_Login(Long id, String login);

    @Query(
        value = "select distinct jhiGroup from Group jhiGroup left join fetch jhiGroup.taskList",
        countQuery = "select count(distinct jhiGroup) from Group jhiGroup"
    )
    Page<Group> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct jhiGroup from Group jhiGroup left join fetch jhiGroup.taskList")
    List<Group> findAllWithToOneRelationships();

    @Query("select jhiGroup from Group jhiGroup left join fetch jhiGroup.taskList where jhiGroup.id =:id")
    Optional<Group> findOneWithToOneRelationships(@Param("id") Long id);
}
