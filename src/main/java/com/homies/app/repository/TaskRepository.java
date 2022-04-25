package com.homies.app.repository;

import com.homies.app.domain.Task;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Task entity.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    default Optional<Task> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Task> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Task> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    Optional<Task> findByIdAndUserAssigneds_User_Login(Long id, String login);



    @Query(
        value = "select distinct task from Task task left join fetch task.taskList",
        countQuery = "select count(distinct task) from Task task"
    )
    Page<Task> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct task from Task task left join fetch task.taskList")
    List<Task> findAllWithToOneRelationships();

    @Query("select task from Task task left join fetch task.taskList where task.id =:id")
    Optional<Task> findOneWithToOneRelationships(@Param("id") Long id);
}
