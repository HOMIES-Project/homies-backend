package com.homies.app.repository;

import com.homies.app.domain.SpendingList;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the SpendingList entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SpendingListRepository extends JpaRepository<SpendingList, Long>, JpaSpecificationExecutor<SpendingList> {}
