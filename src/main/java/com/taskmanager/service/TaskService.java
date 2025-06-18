package com.taskmanager.service;

import com.taskmanager.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskManager taskManager;

    public void addTask(Task task) {
        taskManager.addTask(task);
    }

    public boolean removeTask(String id) {
        return taskManager.removeTask(id);
    }

    public boolean completeTask(String id) {
        return taskManager.completeTask(id);
    }

    public boolean updateTask(String id, String title, String description, LocalDate dueDate, Task.Priority priority) {
        Optional<Task> taskOpt = taskManager.findTaskById(id);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setTitle(title);
            task.setDescription(description);
            task.setDueDate(dueDate);
            task.setPriority(priority);
            taskManager.saveTasks();
            return true;
        }
        return false;
    }

    public Optional<Task> findTaskById(String id) {
        return taskManager.findTaskById(id);
    }

    public List<Task> getAllTasks() {
        return taskManager.getAllTasks();
    }

    public List<Task> getUpcomingTasks() {
        return taskManager.getUpcomingTasks();
    }

    public List<Task> getTasksByPriority(Task.Priority priority) {
        return taskManager.getTasksByPriority(priority);
    }

    public Map<String, Long> getStatistics() {
        return taskManager.getStatistics();
    }

    public WorkTask createWorkTask(String title, String description, LocalDate dueDate,
                                   Task.Priority priority, String project, String client, String department) {
        return new WorkTask(title, description, dueDate, priority, project, client, department);
    }

    public PersonalTask createPersonalTask(String title, String description, LocalDate dueDate,
                                           Task.Priority priority, String category, String location) {
        return new PersonalTask(title, description, dueDate, priority, category, location);
    }
}