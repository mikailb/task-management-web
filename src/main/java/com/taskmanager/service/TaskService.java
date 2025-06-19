package com.taskmanager.service;

import com.taskmanager.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class TaskService {

    @Autowired
    private TaskManager taskManager;

    /**
     * Add a new task
     */
    public void addTask(Task task) {
        taskManager.addTask(task);
    }

    /**
     * Remove task by UUID
     */
    public boolean removeTask(String taskUuid) {
        return taskManager.removeTask(taskUuid);
    }

    /**
     * Mark task as completed
     */
    public boolean completeTask(String taskUuid) {
        return taskManager.completeTask(taskUuid);
    }

    /**
     * Update task details
     */
    public boolean updateTask(String taskUuid, String title, String description,
                              LocalDate dueDate, Task.Priority priority) {
        return taskManager.updateTask(taskUuid, title, description, dueDate, priority);
    }

    /**
     * Find task by UUID
     */
    @Transactional(readOnly = true)
    public Optional<Task> findTaskById(String taskUuid) {
        return taskManager.findTaskById(taskUuid);
    }

    /**
     * Get all tasks
     */
    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        return taskManager.getAllTasks();
    }

    /**
     * Get upcoming tasks (next 7 days)
     */
    @Transactional(readOnly = true)
    public List<Task> getUpcomingTasks() {
        return taskManager.getUpcomingTasks();
    }

    /**
     * Get tasks by priority
     */
    @Transactional(readOnly = true)
    public List<Task> getTasksByPriority(Task.Priority priority) {
        return taskManager.getTasksByPriority(priority);
    }

    /**
     * Get overdue tasks
     */
    @Transactional(readOnly = true)
    public List<Task> getOverdueTasks() {
        return taskManager.getOverdueTasks();
    }

    /**
     * Get completed tasks
     */
    @Transactional(readOnly = true)
    public List<Task> getCompletedTasks() {
        return taskManager.getCompletedTasks();
    }

    /**
     * Get pending tasks
     */
    @Transactional(readOnly = true)
    public List<Task> getPendingTasks() {
        return taskManager.getPendingTasks();
    }

    /**
     * Search tasks by title
     */
    @Transactional(readOnly = true)
    public List<Task> searchTasksByTitle(String title) {
        return taskManager.searchTasksByTitle(title);
    }

    /**
     * Get task statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Long> getStatistics() {
        return taskManager.getStatistics();
    }

    /**
     * Get tasks by type
     */
    @Transactional(readOnly = true)
    public Map<String, Long> getTasksByType() {
        return taskManager.getTasksByType();
    }

    /**
     * Create new work task
     */
    public WorkTask createWorkTask(String title, String description, LocalDate dueDate,
                                   Task.Priority priority, String project, String client, String department) {
        return new WorkTask(title, description, dueDate, priority, project, client, department);
    }

    /**
     * Create new personal task
     */
    public PersonalTask createPersonalTask(String title, String description, LocalDate dueDate,
                                           Task.Priority priority, String category, String location) {
        return new PersonalTask(title, description, dueDate, priority, category, location);
    }

    /**
     * Check if task exists
     */
    @Transactional(readOnly = true)
    public boolean taskExists(String taskUuid) {
        return taskManager.taskExists(taskUuid);
    }

    /**
     * Get task count
     */
    @Transactional(readOnly = true)
    public long getTaskCount() {
        return taskManager.getTaskCount();
    }

    /**
     * Get tasks created between dates
     */
    @Transactional(readOnly = true)
    public List<Task> getTasksCreatedBetween(LocalDate startDate, LocalDate endDate) {
        return taskManager.getTasksCreatedBetween(startDate, endDate);
    }

    /**
     * Delete all completed tasks
     */
    public int deleteCompletedTasks() {
        return taskManager.deleteCompletedTasks();
    }
}