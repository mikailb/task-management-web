package com.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskManagementWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskManagementWebApplication.class, args);
        System.out.println("\n🎯 Oppgavebehandlingssystem startet!");
        System.out.println("📱 Åpne: http://localhost:8080");
        System.out.println("✨ Klar for bruk!\n");
    }
}