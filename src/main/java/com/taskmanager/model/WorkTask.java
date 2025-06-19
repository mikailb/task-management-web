package com.taskmanager.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("WORK")
public class WorkTask extends Task {

    @Size(max = 255, message = "Prosjekt kan ikke være lengre enn 255 tegn")
    @Column(name = "project")
    private String project;

    @Size(max = 255, message = "Kunde kan ikke være lengre enn 255 tegn")
    @Column(name = "client")
    private String client;

    @Size(max = 255, message = "Avdeling kan ikke være lengre enn 255 tegn")
    @Column(name = "department")
    private String department;

    // Default constructor for JPA
    protected WorkTask() {
        super();
    }

    public WorkTask(String title, String description, LocalDate dueDate, Priority priority,
                    String project, String client, String department) {
        super(title, description, dueDate, priority);
        this.project = project;
        this.client = client;
        this.department = department;
    }

    // Getters and Setters
    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" - Jobb (Prosjekt: %s, Kunde: %s)",
                project != null ? project : "N/A",
                client != null ? client : "N/A");
    }
}