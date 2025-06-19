package com.taskmanager.service;

import com.taskmanager.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskManager taskManager;

    @InjectMocks
    private TaskService taskService;

    private WorkTask workTask;
    private PersonalTask personalTask;

    @BeforeEach
    void setUp() {
        workTask = new WorkTask(
                "Test Work Task",
                "Work task description",
                LocalDate.now().plusDays(1),
                Task.Priority.HIGH,
                "Test Project",
                "Test Client",
                "IT"
        );
        workTask.setTaskUuid("wt123456");

        personalTask = new PersonalTask(
                "Test Personal Task",
                "Personal task description",
                LocalDate.now().plusDays(2),
                Task.Priority.MEDIUM,
                "Shopping",
                "Store"
        );
        personalTask.setTaskUuid("pt789012");
    }

    @Test
    void addTask_ShouldCallTaskManager() {
        // When
        taskService.addTask(workTask);

        // Then
        verify(taskManager).addTask(workTask);
    }

    @Test
    void findTaskById_ShouldReturnTask_WhenExists() {
        // Given
        String taskUuid = "wt123456";
        when(taskManager.findTaskById(taskUuid)).thenReturn(Optional.of(workTask));

        // When
        Optional<Task> result = taskService.findTaskById(taskUuid);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(workTask);
        assertThat(result.get().getTitle()).isEqualTo("Test Work Task");
    }

    @Test
    void findTaskById_ShouldReturnEmpty_WhenNotExists() {
        // Given
        String taskUuid = "nonexistent";
        when(taskManager.findTaskById(taskUuid)).thenReturn(Optional.empty());

        // When
        Optional<Task> result = taskService.findTaskById(taskUuid);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void completeTask_ShouldReturnTrue_WhenTaskExists() {
        // Given
        String taskUuid = "wt123456";
        when(taskManager.completeTask(taskUuid)).thenReturn(true);

        // When
        boolean result = taskService.completeTask(taskUuid);

        // Then
        assertThat(result).isTrue();
        verify(taskManager).completeTask(taskUuid);
    }

    @Test
    void completeTask_ShouldReturnFalse_WhenTaskNotExists() {
        // Given
        String taskUuid = "nonexistent";
        when(taskManager.completeTask(taskUuid)).thenReturn(false);

        // When
        boolean result = taskService.completeTask(taskUuid);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void removeTask_ShouldReturnTrue_WhenTaskExists() {
        // Given
        String taskUuid = "wt123456";
        when(taskManager.removeTask(taskUuid)).thenReturn(true);

        // When
        boolean result = taskService.removeTask(taskUuid);

        // Then
        assertThat(result).isTrue();
        verify(taskManager).removeTask(taskUuid);
    }

    @Test
    void getAllTasks_ShouldReturnAllTasks() {
        // Given
        List<Task> expectedTasks = Arrays.asList(workTask, personalTask);
        when(taskManager.getAllTasks()).thenReturn(expectedTasks);

        // When
        List<Task> result = taskService.getAllTasks();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).contains(workTask, personalTask);
    }

    @Test
    void getUpcomingTasks_ShouldReturnUpcomingTasks() {
        // Given
        List<Task> upcomingTasks = Arrays.asList(workTask);
        when(taskManager.getUpcomingTasks()).thenReturn(upcomingTasks);

        // When
        List<Task> result = taskService.getUpcomingTasks();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).contains(workTask);
    }

    @Test
    void getTasksByPriority_ShouldReturnTasksWithSpecificPriority() {
        // Given
        Task.Priority priority = Task.Priority.HIGH;
        List<Task> highPriorityTasks = Arrays.asList(workTask);
        when(taskManager.getTasksByPriority(priority)).thenReturn(highPriorityTasks);

        // When
        List<Task> result = taskService.getTasksByPriority(priority);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPriority()).isEqualTo(Task.Priority.HIGH);
    }

    @Test
    void updateTask_ShouldReturnTrue_WhenTaskExists() {
        // Given
        String taskUuid = "wt123456";
        String newTitle = "Updated Title";
        String newDescription = "Updated Description";
        LocalDate newDueDate = LocalDate.now().plusDays(5);
        Task.Priority newPriority = Task.Priority.LOW;

        when(taskManager.updateTask(taskUuid, newTitle, newDescription, newDueDate, newPriority))
                .thenReturn(true);

        // When
        boolean result = taskService.updateTask(taskUuid, newTitle, newDescription, newDueDate, newPriority);

        // Then
        assertThat(result).isTrue();
        verify(taskManager).updateTask(taskUuid, newTitle, newDescription, newDueDate, newPriority);
    }

    @Test
    void getStatistics_ShouldReturnCorrectStats() {
        // Given
        Map<String, Long> expectedStats = Map.of(
                "total", 10L,
                "completed", 3L,
                "pending", 7L,
                "overdue", 2L
        );
        when(taskManager.getStatistics()).thenReturn(expectedStats);

        // When
        Map<String, Long> result = taskService.getStatistics();

        // Then
        assertThat(result).hasSize(4);
        assertThat(result.get("total")).isEqualTo(10L);
        assertThat(result.get("completed")).isEqualTo(3L);
        assertThat(result.get("pending")).isEqualTo(7L);
        assertThat(result.get("overdue")).isEqualTo(2L);
    }

    @Test
    void createWorkTask_ShouldReturnWorkTaskWithCorrectFields() {
        // When
        WorkTask result = taskService.createWorkTask(
                "Work Task",
                "Work Description",
                LocalDate.now().plusDays(2),
                Task.Priority.MEDIUM,
                "Test Project",
                "Test Client",
                "Development"
        );

        // Then
        assertThat(result.getTitle()).isEqualTo("Work Task");
        assertThat(result.getDescription()).isEqualTo("Work Description");
        assertThat(result.getProject()).isEqualTo("Test Project");
        assertThat(result.getClient()).isEqualTo("Test Client");
        assertThat(result.getDepartment()).isEqualTo("Development");
        assertThat(result.getPriority()).isEqualTo(Task.Priority.MEDIUM);
        assertThat(result.isCompleted()).isFalse();
        assertThat(result.getTaskUuid()).isNotNull();
    }

    @Test
    void createPersonalTask_ShouldReturnPersonalTaskWithCorrectFields() {
        // When
        PersonalTask result = taskService.createPersonalTask(
                "Personal Task",
                "Personal Description",
                LocalDate.now().plusDays(3),
                Task.Priority.LOW,
                "Health",
                "Gym"
        );

        // Then
        assertThat(result.getTitle()).isEqualTo("Personal Task");
        assertThat(result.getDescription()).isEqualTo("Personal Description");
        assertThat(result.getCategory()).isEqualTo("Health");
        assertThat(result.getLocation()).isEqualTo("Gym");
        assertThat(result.getPriority()).isEqualTo(Task.Priority.LOW);
        assertThat(result.isCompleted()).isFalse();
        assertThat(result.getTaskUuid()).isNotNull();
    }

    @Test
    void taskExists_ShouldReturnTrue_WhenTaskExists() {
        // Given
        String taskUuid = "wt123456";
        when(taskManager.taskExists(taskUuid)).thenReturn(true);

        // When
        boolean result = taskService.taskExists(taskUuid);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void taskExists_ShouldReturnFalse_WhenTaskNotExists() {
        // Given
        String taskUuid = "nonexistent";
        when(taskManager.taskExists(taskUuid)).thenReturn(false);

        // When
        boolean result = taskService.taskExists(taskUuid);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void getTaskCount_ShouldReturnCorrectCount() {
        // Given
        when(taskManager.getTaskCount()).thenReturn(15L);

        // When
        long result = taskService.getTaskCount();

        // Then
        assertThat(result).isEqualTo(15L);
    }

    @Test
    void searchTasksByTitle_ShouldReturnMatchingTasks() {
        // Given
        String searchQuery = "Test";
        List<Task> searchResults = Arrays.asList(workTask, personalTask);
        when(taskManager.searchTasksByTitle(searchQuery)).thenReturn(searchResults);

        // When
        List<Task> result = taskService.searchTasksByTitle(searchQuery);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).contains(workTask, personalTask);
    }

    @Test
    void deleteCompletedTasks_ShouldReturnNumberOfDeletedTasks() {
        // Given
        when(taskManager.deleteCompletedTasks()).thenReturn(3);

        // When
        int result = taskService.deleteCompletedTasks();

        // Then
        assertThat(result).isEqualTo(3);
        verify(taskManager).deleteCompletedTasks();
    }

    @Test
    void getTasksByType_ShouldReturnTypeDistribution() {
        // Given
        Map<String, Long> typeStats = Map.of(
                "work", 6L,
                "personal", 4L
        );
        when(taskManager.getTasksByType()).thenReturn(typeStats);

        // When
        Map<String, Long> result = taskService.getTasksByType();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get("work")).isEqualTo(6L);
        assertThat(result.get("personal")).isEqualTo(4L);
    }
}