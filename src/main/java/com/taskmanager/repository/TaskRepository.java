package com.taskmanager.repository;

import com.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Find task by UUID (the 8-character identifier shown to users)
     */
    Optional<Task> findByTaskUuid(String taskUuid);

    /**
     * Find all tasks ordered by completion status and due date
     */
    @Query("SELECT t FROM Task t ORDER BY t.completed ASC, t.dueDate ASC")
    List<Task> findAllOrderedByCompletionAndDueDate();

    /**
     * Find tasks by priority
     */
    List<Task> findByPriorityOrderByDueDateAsc(Task.Priority priority);

    /**
     * Find upcoming tasks (not completed, due within next 7 days)
     */
    @Query("SELECT t FROM Task t WHERE t.completed = false " +
            "AND t.dueDate >= :today AND t.dueDate <= :nextWeek " +
            "ORDER BY t.dueDate ASC")
    List<Task> findUpcomingTasks(@Param("today") LocalDate today,
                                 @Param("nextWeek") LocalDate nextWeek);

    /**
     * Find overdue tasks (not completed and due date passed)
     */
    @Query("SELECT t FROM Task t WHERE t.completed = false AND t.dueDate < :today")
    List<Task> findOverdueTasks(@Param("today") LocalDate today);

    /**
     * Count tasks by completion status
     */
    long countByCompleted(boolean completed);

    /**
     * Count overdue tasks
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.completed = false AND t.dueDate < :today")
    long countOverdueTasks(@Param("today") LocalDate today);

    /**
     * Find tasks by title containing (case insensitive)
     */
    List<Task> findByTitleContainingIgnoreCaseOrderByDueDateAsc(String title);

    /**
     * Find completed tasks
     */
    List<Task> findByCompletedTrueOrderByCompletedDateDesc();

    /**
     * Find pending tasks
     */
    List<Task> findByCompletedFalseOrderByDueDateAsc();

    /**
     * Find tasks created between dates
     */
    @Query("SELECT t FROM Task t WHERE t.createdDate >= :startDate AND t.createdDate <= :endDate " +
            "ORDER BY t.createdDate DESC")
    List<Task> findTasksCreatedBetween(@Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

    /**
     * Delete task by UUID
     */
    void deleteByTaskUuid(String taskUuid);

    /**
     * Check if task exists by UUID
     */
    boolean existsByTaskUuid(String taskUuid);
}