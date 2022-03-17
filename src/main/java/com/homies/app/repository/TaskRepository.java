package com.homies.app.repository;

import com.homies.app.domain.Task;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Task entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {}
