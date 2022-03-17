package com.homies.app.repository;

import com.homies.app.domain.Spending;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Spending entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SpendingRepository extends JpaRepository<Spending, Long>, JpaSpecificationExecutor<Spending> {}
