package com.taskmanager.model;

import com.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Component
@Transactional
public class TaskManager {

    @Autowired
    private TaskRepository taskRepository;

    /**
     * Add a new task
     */
    public void addTask(Task task) {
        taskRepository.save(task);
        System.out.println("✅ Oppgave lagret i database: " + task.getTitle());
    }

    /**
     * Remove task by UUID
     */
    public boolean removeTask(String taskUuid) {
        Optional<Task> task = taskRepository.findByTaskUuid(taskUuid);
        if (task.isPresent()) {
            taskRepository.delete(task.get());
            System.out.println("🗑️ Oppgave slettet: " + task.get().getTitle());
            return true;
        }
        return false;
    }

    /**
     * Mark task as completed
     */
    public boolean completeTask(String taskUuid) {
        Optional<Task> taskOpt = taskRepository.findByTaskUuid(taskUuid);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setCompleted(true);
            taskRepository.save(task);
            System.out.println("✅ Oppgave fullført: " + task.getTitle());
            return true;
        }
        return false;
    }

    /**
     * Find task by UUID
     */
    @Transactional(readOnly = true)
    public Optional<Task> findTaskById(String taskUuid) {
        return taskRepository.findByTaskUuid(taskUuid);
    }

    /**
     * Get all tasks ordered by completion status and due date
     */
    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        return taskRepository.findAllOrderedByCompletionAndDueDate();
    }

    /**
     * Get upcoming tasks (next 7 days)
     */
    @Transactional(readOnly = true)
    public List<Task> getUpcomingTasks() {
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);
        return taskRepository.findUpcomingTasks(today, nextWeek);
    }

    /**
     * Get tasks by priority
     */
    @Transactional(readOnly = true)
    public List<Task> getTasksByPriority(Task.Priority priority) {
        return taskRepository.findByPriorityOrderByDueDateAsc(priority);
    }

    /**
     * Get overdue tasks
     */
    @Transactional(readOnly = true)
    public List<Task> getOverdueTasks() {
        return taskRepository.findOverdueTasks(LocalDate.now());
    }

    /**
     * Get completed tasks
     */
    @Transactional(readOnly = true)
    public List<Task> getCompletedTasks() {
        return taskRepository.findByCompletedTrueOrderByCompletedDateDesc();
    }

    /**
     * Get pending tasks
     */
    @Transactional(readOnly = true)
    public List<Task> getPendingTasks() {
        return taskRepository.findByCompletedFalseOrderByDueDateAsc();
    }

    /**
     * Search tasks by title
     */
    @Transactional(readOnly = true)
    public List<Task> searchTasksByTitle(String title) {
        return taskRepository.findByTitleContainingIgnoreCaseOrderByDueDateAsc(title);
    }

    /**
     * Get task statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Long> getStatistics() {
        Map<String, Long> stats = new HashMap<>();

        long totalTasks = taskRepository.count();
        long completedTasks = taskRepository.countByCompleted(true);
        long pendingTasks = taskRepository.countByCompleted(false);
        long overdueTasks = taskRepository.countOverdueTasks(LocalDate.now());

        stats.put("total", totalTasks);
        stats.put("completed", completedTasks);
        stats.put("pending", pendingTasks);
        stats.put("overdue", overdueTasks);

        return stats;
    }

    /**
     * Update task details
     */
    public boolean updateTask(String taskUuid, String title, String description,
                              LocalDate dueDate, Task.Priority priority) {
        Optional<Task> taskOpt = taskRepository.findByTaskUuid(taskUuid);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setTitle(title);
            task.setDescription(description);
            task.setDueDate(dueDate);
            task.setPriority(priority);
            taskRepository.save(task);
            System.out.println("✏️ Oppgave oppdatert: " + task.getTitle());
            return true;
        }
        return false;
    }

    /**
     * Get task count
     */
    @Transactional(readOnly = true)
    public long getTaskCount() {
        return taskRepository.count();
    }

    /**
     * Check if task exists
     */
    @Transactional(readOnly = true)
    public boolean taskExists(String taskUuid) {
        return taskRepository.existsByTaskUuid(taskUuid);
    }

    /**
     * Get tasks created between dates
     */
    @Transactional(readOnly = true)
    public List<Task> getTasksCreatedBetween(LocalDate startDate, LocalDate endDate) {
        return taskRepository.findTasksCreatedBetween(startDate, endDate);
    }

    /**
     * Get tasks by type
     */
    @Transactional(readOnly = true)
    public Map<String, Long> getTasksByType() {
        List<Task> allTasks = taskRepository.findAll();
        Map<String, Long> typeCount = new HashMap<>();

        long workTasks = allTasks.stream().filter(task -> task instanceof WorkTask).count();
        long personalTasks = allTasks.stream().filter(task -> task instanceof PersonalTask).count();

        typeCount.put("work", workTasks);
        typeCount.put("personal", personalTasks);

        return typeCount;
    }

    /**
     * Bulk delete completed tasks
     */
    public int deleteCompletedTasks() {
        List<Task> completedTasks = taskRepository.findByCompletedTrueOrderByCompletedDateDesc();
        int count = completedTasks.size();
        taskRepository.deleteAll(completedTasks);
        System.out.println("🗑️ Slettet " + count + " fullførte oppgaver");
        return count;
    }

    // No longer needed - JPA handles persistence automatically
    public void saveTasks() {
        // This method is kept for backward compatibility but does nothing
        // JPA automatically persists changes when transaction commits
        System.out.println("💾 JPA håndterer automatisk lagring");
    }
}