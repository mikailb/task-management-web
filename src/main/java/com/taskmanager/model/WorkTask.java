package com.taskmanager.model;

import java.time.LocalDate;

public class WorkTask extends Task {
    private static final long serialVersionUID = 1L;

    private String project;
    private String client;
    private String department;

    public WorkTask(String title, String description, LocalDate dueDate, Priority priority,
                    String project, String client, String department) {
        super(title, description, dueDate, priority);
        this.project = project;
        this.client = client;
        this.department = department;
    }

    public String getProject() { return project; }
    public void setProject(String project) { this.project = project; }

    public String getClient() { return client; }
    public void setClient(String client) { this.client = client; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public String toString() {
        return super.toString() + String.format(" - Jobb (Prosjekt: %s, Kunde: %s)", project, client);
    }
}