package com.taskmanager.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Task implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private boolean completed;
    private Priority priority;
    private LocalDate createdDate;

    public enum Priority {
        LOW("Lav"), MEDIUM("Medium"), HIGH("Høy");

        private final String displayName;

        Priority(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public Task(String title, String description, LocalDate dueDate, Priority priority) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = false;
        this.priority = priority;
        this.createdDate = LocalDate.now();
    }

    // Getters and Setters
    public String getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public LocalDate getCreatedDate() { return createdDate; }

    public boolean isOverdue() {
        return !completed && dueDate.isBefore(LocalDate.now());
    }

    public String getFormattedDueDate() {
        return dueDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    // Ny metode for å identifisere oppgavetype i Thymeleaf
    public String getTaskType() {
        return this.getClass().getSimpleName();
    }

    // Hjelpemetoder for å sjekke oppgavetype
    public boolean isWorkTask() {
        return this instanceof WorkTask;
    }

    public boolean isPersonalTask() {
        return this instanceof PersonalTask;
    }

    @Override
    public String toString() {
        return String.format("Task [%s] %s (Frist: %s, Prioritet: %s, %s)",
                id, title, getFormattedDueDate(), priority.getDisplayName(),
                completed ? "Fullført" : "Aktiv");
    }
}