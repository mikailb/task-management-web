package com.taskmanager.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("PERSONAL")
public class PersonalTask extends Task {

    @Size(max = 100, message = "Kategori kan ikke være lengre enn 100 tegn")
    @Column(name = "category")
    private String category;

    @Size(max = 255, message = "Sted kan ikke være lengre enn 255 tegn")
    @Column(name = "location")
    private String location;

    // Default constructor for JPA
    protected PersonalTask() {
        super();
    }

    public PersonalTask(String title, String description, LocalDate dueDate, Priority priority,
                        String category, String location) {
        super(title, description, dueDate, priority);
        this.category = category;
        this.location = location;
    }

    // Getters and Setters
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" - Personlig (Kategori: %s)",
                category != null ? category : "Generell");
    }
}