package com.homies.app.repository;

import com.homies.app.domain.UserPending;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the UserPending entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserPendingRepository extends JpaRepository<UserPending, Long>, JpaSpecificationExecutor<UserPending> {}
