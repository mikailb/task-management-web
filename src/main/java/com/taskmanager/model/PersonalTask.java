package com.taskmanager.model;

import java.time.LocalDate;

public class PersonalTask extends Task {
    private static final long serialVersionUID = 1L;

    private String category;
    private String location;

    public PersonalTask(String title, String description, LocalDate dueDate, Priority priority,
                        String category, String location) {
        super(title, description, dueDate, priority);
        this.category = category;
        this.location = location;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    @Override
    public String toString() {
        return super.toString() + String.format(" - Personlig (Kategori: %s)", category);
    }
}