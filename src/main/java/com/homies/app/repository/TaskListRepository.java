package com.homies.app.repository;

import com.homies.app.domain.TaskList;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the TaskList entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TaskListRepository extends JpaRepository<TaskList, Long>, JpaSpecificationExecutor<TaskList> {}
