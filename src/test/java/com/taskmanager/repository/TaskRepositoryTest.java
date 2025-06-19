package com.taskmanager.repository;

import com.taskmanager.model.PersonalTask;
import com.taskmanager.model.Task;
import com.taskmanager.model.WorkTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    private WorkTask workTask;
    private PersonalTask personalTask;
    private WorkTask overdueTask;

    @BeforeEach
    void setUp() {
        // Create test tasks
        workTask = new WorkTask(
                "Integration Test Work Task",
                "This is a work task for testing",
                LocalDate.now().plusDays(3),
                Task.Priority.HIGH,
                "Test Project",
                "Test Client",
                "IT"
        );

        personalTask = new PersonalTask(
                "Integration Test Personal Task",
                "This is a personal task for testing",
                LocalDate.now().plusDays(5),
                Task.Priority.MEDIUM,
                "Health",
                "Gym"
        );

        overdueTask = new WorkTask(
                "Overdue Task",
                "This task is overdue",
                LocalDate.now().minusDays(2),
                Task.Priority.LOW,
                "Old Project",
                "Old Client",
                "Marketing"
        );

        // Persist test data
        entityManager.persistAndFlush(workTask);
        entityManager.persistAndFlush(personalTask);
        entityManager.persistAndFlush(overdueTask);
    }

    @Test
    void findByTaskUuid_ShouldReturnTask_WhenExists() {
        // When
        Optional<Task> result = taskRepository.findByTaskUuid(workTask.getTaskUuid());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Integration Test Work Task");
        assertThat(result.get()).isInstanceOf(WorkTask.class);
    }

    @Test
    void findByTaskUuid_ShouldReturnEmpty_WhenNotExists() {
        // When
        Optional<Task> result = taskRepository.findByTaskUuid("nonexistent");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findAllOrderedByCompletionAndDueDate_ShouldReturnTasksInCorrectOrder() {
        // Mark one task as completed
        workTask.setCompleted(true);
        entityManager.persistAndFlush(workTask);

        // When
        List<Task> result = taskRepository.findAllOrderedByCompletionAndDueDate();

        // Then
        assertThat(result).hasSize(3);
        // Active tasks should come first, ordered by due date
        assertThat(result.get(0).isCompleted()).isFalse();
        assertThat(result.get(1).isCompleted()).isFalse();
        // Completed task should be last
        assertThat(result.get(2).isCompleted()).isTrue();
    }

    @Test
    void findByPriorityOrderByDueDateAsc_ShouldReturnTasksWithSpecificPriority() {
        // When
        List<Task> highPriorityTasks = taskRepository.findByPriorityOrderByDueDateAsc(Task.Priority.HIGH);
        List<Task> mediumPriorityTasks = taskRepository.findByPriorityOrderByDueDateAsc(Task.Priority.MEDIUM);

        // Then
        assertThat(highPriorityTasks).hasSize(1);
        assertThat(highPriorityTasks.get(0).getTitle()).isEqualTo("Integration Test Work Task");

        assertThat(mediumPriorityTasks).hasSize(1);
        assertThat(mediumPriorityTasks.get(0).getTitle()).isEqualTo("Integration Test Personal Task");
    }

    @Test
    void findUpcomingTasks_ShouldReturnTasksDueWithinPeriod() {
        // Given
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);

        // When
        List<Task> upcomingTasks = taskRepository.findUpcomingTasks(today, nextWeek);

        // Then
        assertThat(upcomingTasks).hasSize(2); // workTask and personalTask
        assertThat(upcomingTasks)
                .extracting(Task::getTitle)
                .contains("Integration Test Work Task", "Integration Test Personal Task");

        // Should not include overdue task
        assertThat(upcomingTasks)
                .extracting(Task::getTitle)
                .doesNotContain("Overdue Task");
    }

    @Test
    void findOverdueTasks_ShouldReturnOverdueTasks() {
        // When
        List<Task> overdueTasks = taskRepository.findOverdueTasks(LocalDate.now());

        // Then
        assertThat(overdueTasks).hasSize(1);
        assertThat(overdueTasks.get(0).getTitle()).isEqualTo("Overdue Task");
        assertThat(overdueTasks.get(0).getDueDate()).isBefore(LocalDate.now());
    }

    @Test
    void countByCompleted_ShouldReturnCorrectCounts() {
        // Mark one task as completed
        personalTask.setCompleted(true);
        entityManager.persistAndFlush(personalTask);

        // When
        long completedCount = taskRepository.countByCompleted(true);
        long pendingCount = taskRepository.countByCompleted(false);

        // Then
        assertThat(completedCount).isEqualTo(1);
        assertThat(pendingCount).isEqualTo(2);
    }

    @Test
    void countOverdueTasks_ShouldReturnCorrectCount() {
        // When
        long overdueCount = taskRepository.countOverdueTasks(LocalDate.now());

        // Then
        assertThat(overdueCount).isEqualTo(1);
    }

    @Test
    void findByTitleContainingIgnoreCase_ShouldReturnMatchingTasks() {
        // When
        List<Task> searchResults = taskRepository.findByTitleContainingIgnoreCaseOrderByDueDateAsc("INTEGRATION");

        // Then
        assertThat(searchResults).hasSize(2);
        assertThat(searchResults)
                .extracting(Task::getTitle)
                .allMatch(title -> title.toLowerCase().contains("integration"));
    }

    @Test
    void findByCompletedTrueOrderByCompletedDateDesc_ShouldReturnCompletedTasks() {
        // Mark tasks as completed at different times
        workTask.setCompleted(true);
        entityManager.persistAndFlush(workTask);

        // Wait a bit to ensure different timestamps
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        personalTask.setCompleted(true);
        entityManager.persistAndFlush(personalTask);

        // When
        List<Task> completedTasks = taskRepository.findByCompletedTrueOrderByCompletedDateDesc();

        // Then
        assertThat(completedTasks).hasSize(2);
        assertThat(completedTasks.get(0).getCompletedDate())
                .isAfterOrEqualTo(completedTasks.get(1).getCompletedDate());
    }

    @Test
    void findByCompletedFalseOrderByDueDateAsc_ShouldReturnPendingTasks() {
        // Mark one task as completed
        workTask.setCompleted(true);
        entityManager.persistAndFlush(workTask);

        // When
        List<Task> pendingTasks = taskRepository.findByCompletedFalseOrderByDueDateAsc();

        // Then
        assertThat(pendingTasks).hasSize(2);
        assertThat(pendingTasks)
                .allMatch(task -> !task.isCompleted());

        // Should be ordered by due date
        assertThat(pendingTasks.get(0).getDueDate())
                .isBeforeOrEqualTo(pendingTasks.get(1).getDueDate());
    }

    @Test
    void existsByTaskUuid_ShouldReturnTrue_WhenTaskExists() {
        // When
        boolean exists = taskRepository.existsByTaskUuid(workTask.getTaskUuid());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByTaskUuid_ShouldReturnFalse_WhenTaskNotExists() {
        // When
        boolean exists = taskRepository.existsByTaskUuid("nonexistent");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void deleteByTaskUuid_ShouldRemoveTask() {
        // Given
        String taskUuid = personalTask.getTaskUuid();
        assertThat(taskRepository.existsByTaskUuid(taskUuid)).isTrue();

        // When
        taskRepository.deleteByTaskUuid(taskUuid);
        entityManager.flush();

        // Then
        assertThat(taskRepository.existsByTaskUuid(taskUuid)).isFalse();
        assertThat(taskRepository.findByTaskUuid(taskUuid)).isEmpty();
    }

    @Test
    void save_ShouldPersistTaskWithGeneratedUuid() {
        // Given
        WorkTask newTask = new WorkTask(
                "New Task",
                "Description",
                LocalDate.now().plusDays(1),
                Task.Priority.MEDIUM,
                "Project",
                "Client",
                "Department"
        );

        // When
        Task savedTask = taskRepository.save(newTask);

        // Then
        assertThat(savedTask.getId()).isNotNull();
        assertThat(savedTask.getTaskUuid()).isNotNull();
        assertThat(savedTask.getTaskUuid()).hasSize(8);
        assertThat(savedTask.getCreatedDate()).isNotNull();
    }

    @Test
    void inheritance_ShouldWorkCorrectly() {
        // When
        List<Task> allTasks = taskRepository.findAll();

        // Then
        assertThat(allTasks).hasSize(3);

        long workTasks = allTasks.stream()
                .filter(task -> task instanceof WorkTask)
                .count();

        long personalTasks = allTasks.stream()
                .filter(task -> task instanceof PersonalTask)
                .count();

        assertThat(workTasks).isEqualTo(2); // workTask and overdueTask
        assertThat(personalTasks).isEqualTo(1); // personalTask
    }
}