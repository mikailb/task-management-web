package com.taskmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "tasks")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "task_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_uuid", unique = true, nullable = false, length = 8)
    private String taskUuid;

    @NotBlank(message = "Tittel kan ikke være tom")
    @Size(max = 255, message = "Tittel kan ikke være lengre enn 255 tegn")
    @Column(name = "title", nullable = false)
    private String title;

    @Size(max = 1000, message = "Beskrivelse kan ikke være lengre enn 1000 tegn")
    @Column(name = "description", length = 1000)
    private String description;

    @NotNull(message = "Forfallsdato må angis")
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "completed", nullable = false)
    private boolean completed = false;

    @NotNull(message = "Prioritet må angis")
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority;

    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

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

    // Default constructor for JPA
    protected Task() {}

    public Task(String title, String description, LocalDate dueDate, Priority priority) {
        this.taskUuid = java.util.UUID.randomUUID().toString().substring(0, 8);
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = false;
        this.priority = priority;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTaskUuid() { return taskUuid; }
    public void setTaskUuid(String taskUuid) { this.taskUuid = taskUuid; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) {
        this.completed = completed;
        if (completed && this.completedDate == null) {
            this.completedDate = LocalDateTime.now();
        } else if (!completed) {
            this.completedDate = null;
        }
    }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }

    public LocalDateTime getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDateTime completedDate) { this.completedDate = completedDate; }

    // Business methods
    public boolean isOverdue() {
        return !completed && dueDate.isBefore(LocalDate.now());
    }

    public String getFormattedDueDate() {
        return dueDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public String getTaskType() {
        return this.getClass().getSimpleName();
    }

    public boolean isWorkTask() {
        return this instanceof WorkTask;
    }

    public boolean isPersonalTask() {
        return this instanceof PersonalTask;
    }

    // JPA lifecycle methods
    @PrePersist
    protected void onCreate() {
        if (taskUuid == null) {
            taskUuid = java.util.UUID.randomUUID().toString().substring(0, 8);
        }
    }

    @Override
    public String toString() {
        return String.format("Task [%s] %s (Frist: %s, Prioritet: %s, %s)",
                taskUuid, title, getFormattedDueDate(), priority.getDisplayName(),
                completed ? "Fullført" : "Aktiv");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return id != null && id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}