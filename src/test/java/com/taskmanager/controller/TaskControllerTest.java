package com.taskmanager.controller;

import com.taskmanager.model.PersonalTask;
import com.taskmanager.model.Task;
import com.taskmanager.model.WorkTask;
import com.taskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
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
    void index_ShouldDisplayHomePage() throws Exception {
        // Given
        when(taskService.getAllTasks()).thenReturn(Arrays.asList(workTask, personalTask));
        when(taskService.getStatistics()).thenReturn(Map.of(
                "total", 2L,
                "completed", 0L,
                "pending", 2L,
                "overdue", 0L
        ));
        when(taskService.getUpcomingTasks()).thenReturn(Arrays.asList(workTask));

        // When & Then
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attributeExists("stats"))
                .andExpect(model().attributeExists("upcomingTasks"))
                .andExpect(model().attribute("tasks", hasSize(2)))
                .andExpect(model().attribute("stats", hasEntry("total", 2L)));
    }

    @Test
    void showAddTaskForm_ShouldDisplayAddTaskPage() throws Exception {
        // When & Then
        mockMvc.perform(get("/add-task"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-task"))
                .andExpect(model().attributeExists("priorities"))
                .andExpect(model().attribute("priorities", hasSize(3)));
    }

    @Test
    void addWorkTask_ShouldCreateTaskAndRedirect() throws Exception {
        // Given
        WorkTask newWorkTask = new WorkTask(
                "New Work Task",
                "Work task description",
                LocalDate.now().plusDays(1),
                Task.Priority.HIGH,
                "Test Project",
                "Test Client",
                "IT"
        );
        when(taskService.createWorkTask(
                eq("New Work Task"),
                eq("Work task description"),
                any(LocalDate.class),
                eq(Task.Priority.HIGH),
                eq("Test Project"),
                eq("Test Client"),
                eq("IT")
        )).thenReturn(newWorkTask);

        // When & Then
        mockMvc.perform(post("/add-task")
                        .param("taskType", "work")
                        .param("title", "New Work Task")
                        .param("description", "Work task description")
                        .param("dueDate", LocalDate.now().plusDays(1).toString())
                        .param("priority", "HIGH")
                        .param("project", "Test Project")
                        .param("client", "Test Client")
                        .param("department", "IT"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("success"));

        verify(taskService).createWorkTask(
                eq("New Work Task"),
                eq("Work task description"),
                any(LocalDate.class),
                eq(Task.Priority.HIGH),
                eq("Test Project"),
                eq("Test Client"),
                eq("IT")
        );
        verify(taskService).addTask(any(WorkTask.class));
    }

    @Test
    void addPersonalTask_ShouldCreateTaskAndRedirect() throws Exception {
        // Given
        PersonalTask newPersonalTask = new PersonalTask(
                "New Personal Task",
                "Personal task description",
                LocalDate.now().plusDays(2),
                Task.Priority.MEDIUM,
                "Health",
                "Gym"
        );
        when(taskService.createPersonalTask(
                eq("New Personal Task"),
                eq("Personal task description"),
                any(LocalDate.class),
                eq(Task.Priority.MEDIUM),
                eq("Health"),
                eq("Gym")
        )).thenReturn(newPersonalTask);

        // When & Then
        mockMvc.perform(post("/add-task")
                        .param("taskType", "personal")
                        .param("title", "New Personal Task")
                        .param("description", "Personal task description")
                        .param("dueDate", LocalDate.now().plusDays(2).toString())
                        .param("priority", "MEDIUM")
                        .param("category", "Health")
                        .param("location", "Gym"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("success"));

        verify(taskService).createPersonalTask(
                eq("New Personal Task"),
                eq("Personal task description"),
                any(LocalDate.class),
                eq(Task.Priority.MEDIUM),
                eq("Health"),
                eq("Gym")
        );
        verify(taskService).addTask(any(PersonalTask.class));
    }

    @Test
    void addTask_WithEmptyTitle_ShouldShowError() throws Exception {
        // When & Then
        mockMvc.perform(post("/add-task")
                        .param("taskType", "work")
                        .param("title", "")
                        .param("description", "Description")
                        .param("dueDate", LocalDate.now().plusDays(1).toString())
                        .param("priority", "HIGH"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("error"));

        verify(taskService, never()).addTask(ArgumentMatchers.any());
    }

    @Test
    void completeTask_ShouldCompleteTaskAndRedirect() throws Exception {
        // Given
        when(taskService.findTaskById("wt123456")).thenReturn(Optional.of(workTask));
        when(taskService.completeTask("wt123456")).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/complete-task/wt123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("success"));

        verify(taskService).completeTask("wt123456");
    }

    @Test
    void completeTask_WithNonExistentTask_ShouldShowError() throws Exception {
        // Given
        when(taskService.findTaskById("nonexistent")).thenReturn(Optional.empty());
        when(taskService.completeTask("nonexistent")).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/complete-task/nonexistent"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    void deleteTask_ShouldDeleteTaskAndRedirect() throws Exception {
        // Given
        when(taskService.findTaskById("wt123456")).thenReturn(Optional.of(workTask));
        when(taskService.removeTask("wt123456")).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/delete-task/wt123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("success"));

        verify(taskService).removeTask("wt123456");
    }

    @Test
    void showEditTaskForm_ShouldDisplayEditPage() throws Exception {
        // Given
        when(taskService.findTaskById("wt123456")).thenReturn(Optional.of(workTask));

        // When & Then
        mockMvc.perform(get("/edit-task/wt123456"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-task"))
                .andExpect(model().attributeExists("task"))
                .andExpect(model().attributeExists("priorities"))
                .andExpect(model().attribute("task", is(workTask)));
    }

    @Test
    void showEditTaskForm_WithNonExistentTask_ShouldRedirect() throws Exception {
        // Given
        when(taskService.findTaskById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/edit-task/nonexistent"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    void editTask_ShouldUpdateTaskAndRedirect() throws Exception {
        // Given
        when(taskService.updateTask(
                eq("wt123456"),
                eq("Updated Title"),
                eq("Updated Description"),
                any(LocalDate.class),
                eq(Task.Priority.MEDIUM)
        )).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/edit-task/wt123456")
                        .param("title", "Updated Title")
                        .param("description", "Updated Description")
                        .param("dueDate", LocalDate.now().plusDays(3).toString())
                        .param("priority", "MEDIUM"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("success"));

        verify(taskService).updateTask(
                eq("wt123456"),
                eq("Updated Title"),
                eq("Updated Description"),
                any(LocalDate.class),
                eq(Task.Priority.MEDIUM)
        );
    }

    @Test
    void upcomingTasks_ShouldDisplayUpcomingTasksPage() throws Exception {
        // Given
        when(taskService.getUpcomingTasks()).thenReturn(Arrays.asList(workTask));

        // When & Then
        mockMvc.perform(get("/upcoming"))
                .andExpect(status().isOk())
                .andExpect(view().name("task-list"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attributeExists("pageTitle"))
                .andExpect(model().attribute("tasks", hasSize(1)))
                .andExpect(model().attribute("pageTitle", containsString("Kommende oppgaver")));
    }

    @Test
    void tasksByPriority_ShouldDisplayTasksWithSpecificPriority() throws Exception {
        // Given
        when(taskService.getTasksByPriority(Task.Priority.HIGH))
                .thenReturn(Arrays.asList(workTask));

        // When & Then
        mockMvc.perform(get("/priority/HIGH"))
                .andExpect(status().isOk())
                .andExpect(view().name("task-list"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attributeExists("pageTitle"))
                .andExpect(model().attribute("tasks", hasSize(1)))
                .andExpect(model().attribute("pageTitle", containsString("høy prioritet")));
    }

    @Test
    void completedTasks_ShouldDisplayCompletedTasksPage() throws Exception {
        // Given
        workTask.setCompleted(true);
        when(taskService.getCompletedTasks()).thenReturn(Arrays.asList(workTask));

        // When & Then
        mockMvc.perform(get("/completed"))
                .andExpect(status().isOk())
                .andExpect(view().name("task-list"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attribute("pageTitle", containsString("Fullførte oppgaver")));
    }

    @Test
    void pendingTasks_ShouldDisplayPendingTasksPage() throws Exception {
        // Given
        when(taskService.getPendingTasks()).thenReturn(Arrays.asList(workTask, personalTask));

        // When & Then
        mockMvc.perform(get("/pending"))
                .andExpect(status().isOk())
                .andExpect(view().name("task-list"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attribute("pageTitle", containsString("Aktive oppgaver")));
    }

    @Test
    void overdueTasks_ShouldDisplayOverdueTasksPage() throws Exception {
        // Given
        workTask.setDueDate(LocalDate.now().minusDays(1));
        when(taskService.getOverdueTasks()).thenReturn(Arrays.asList(workTask));

        // When & Then
        mockMvc.perform(get("/overdue"))
                .andExpect(status().isOk())
                .andExpect(view().name("task-list"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attribute("pageTitle", containsString("Forfalte oppgaver")));
    }

    @Test
    void searchTasks_ShouldDisplaySearchResults() throws Exception {
        // Given
        String searchQuery = "test";
        when(taskService.searchTasksByTitle(searchQuery))
                .thenReturn(Arrays.asList(workTask, personalTask));

        // When & Then
        mockMvc.perform(get("/search").param("query", searchQuery))
                .andExpect(status().isOk())
                .andExpect(view().name("task-list"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attributeExists("pageTitle"))
                .andExpect(model().attributeExists("searchQuery"))
                .andExpect(model().attribute("tasks", hasSize(2)))
                .andExpect(model().attribute("searchQuery", is(searchQuery)));
    }

    @Test
    void searchTasks_WithEmptyQuery_ShouldRedirectToHome() throws Exception {
        // When & Then
        mockMvc.perform(get("/search").param("query", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(taskService, never()).searchTasksByTitle(ArgumentMatchers.any());
    }

    @Test
    void deleteCompletedTasks_ShouldDeleteAndRedirect() throws Exception {
        // Given
        when(taskService.deleteCompletedTasks()).thenReturn(3);

        // When & Then
        mockMvc.perform(post("/delete-completed"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("success"));

        verify(taskService).deleteCompletedTasks();
    }

    @Test
    void deleteCompletedTasks_WithNoCompletedTasks_ShouldShowInfoMessage() throws Exception {
        // Given
        when(taskService.deleteCompletedTasks()).thenReturn(0);

        // When & Then
        mockMvc.perform(post("/delete-completed"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("info"));
    }

    @Test
    void deleteTask_WithNonExistentTask_ShouldShowError() throws Exception {
        // Given
        when(taskService.findTaskById("nonexistent")).thenReturn(Optional.empty());
        when(taskService.removeTask("nonexistent")).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/delete-task/nonexistent"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    void editTask_WithEmptyTitle_ShouldShowError() throws Exception {
        // When & Then
        mockMvc.perform(post("/edit-task/wt123456")
                        .param("title", "")
                        .param("description", "Description")
                        .param("dueDate", LocalDate.now().plusDays(1).toString())
                        .param("priority", "MEDIUM"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("error"));

        verify(taskService, never()).updateTask(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any()
        );
    }

    @Test
    void editTask_WithInvalidDate_ShouldShowError() throws Exception {
        // When & Then
        mockMvc.perform(post("/edit-task/wt123456")
                        .param("title", "Valid Title")
                        .param("description", "Description")
                        .param("dueDate", "")
                        .param("priority", "MEDIUM"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("error"));

        verify(taskService, never()).updateTask(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any()
        );
    }

    @Test
    void addTask_WithInvalidDate_ShouldShowError() throws Exception {
        // When & Then
        mockMvc.perform(post("/add-task")
                        .param("taskType", "work")
                        .param("title", "Valid Title")
                        .param("description", "Description")
                        .param("dueDate", "invalid-date")
                        .param("priority", "HIGH"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("error"));

        verify(taskService, never()).addTask(ArgumentMatchers.any());
    }
}