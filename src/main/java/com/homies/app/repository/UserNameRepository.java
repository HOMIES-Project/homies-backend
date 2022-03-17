package com.homies.app.repository;

import com.homies.app.domain.UserName;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the UserName entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserNameRepository extends JpaRepository<UserName, Long>, JpaSpecificationExecutor<UserName> {}
