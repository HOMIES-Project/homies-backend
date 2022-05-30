package com.homies.app.repository;

import com.homies.app.domain.TaskList;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data SQL repository for the TaskList entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TaskListRepository extends JpaRepository<TaskList, Long>, JpaSpecificationExecutor<TaskList> {

    Optional<TaskList> findByIdAndTasks_TaskName(Long id, String taskName);

    Optional<TaskList> findByTasks_UserData_Id(Long id);

    Optional<TaskList> findByTasks_UserAssigneds_Id(Long id);

    Optional<TaskList> getByIdAndTasks_UserAssigneds_Id(Long id, Long id1);

}
