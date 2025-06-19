package com.taskmanager.config;

import com.taskmanager.model.PersonalTask;
import com.taskmanager.model.Task;
import com.taskmanager.model.WorkTask;
import com.taskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private TaskService taskService;

    @Override
    public void run(String... args) throws Exception {
        // Sjekk om databasen allerede har data
        if (taskService.getTaskCount() == 0) {
            System.out.println("🔄 Databasen er tom - laster inn sample data...");
            loadSampleData();
            System.out.println("✅ Sample data lastet inn!");
        } else {
            System.out.println("📊 Databasen har allerede " + taskService.getTaskCount() + " oppgaver");
        }
    }

    private void loadSampleData() {
        // Work Tasks
        WorkTask wt1 = taskService.createWorkTask(
                "Ferdigstille prosjektrapport",
                "Skriv sammendrag og konklusjon for Q4 rapport",
                LocalDate.now().plusDays(3),
                Task.Priority.HIGH,
                "Prosjekt Alpha",
                "Kunde AS",
                "IT"
        );
        wt1.setTaskUuid("wt001234");
        taskService.addTask(wt1);

        WorkTask wt2 = taskService.createWorkTask(
                "Teammøte",
                "Ukentlig statusmøte med utviklingsteamet",
                LocalDate.now().plusDays(2),
                Task.Priority.MEDIUM,
                "Drift",
                "Intern",
                "IT"
        );
        wt2.setTaskUuid("wt005678");
        taskService.addTask(wt2);

        WorkTask wt3 = taskService.createWorkTask(
                "Code review",
                "Gjennomgå kode for ny funksjonalitet",
                LocalDate.now().plusDays(1),
                Task.Priority.HIGH,
                "TaskFlow",
                "Intern",
                "Utvikling"
        );
        wt3.setTaskUuid("wt009876");
        taskService.addTask(wt3);

        WorkTask wt4 = taskService.createWorkTask(
                "Kundemøte",
                "Presentere ny løsning for kunde",
                LocalDate.now().plusDays(5),
                Task.Priority.MEDIUM,
                "Salgsprosjekt",
                "TechCorp",
                "Salg"
        );
        wt4.setTaskUuid("wt005432");
        taskService.addTask(wt4);

        WorkTask wt5 = taskService.createWorkTask(
                "Deployment",
                "Deploy applikasjon til produksjon",
                LocalDate.now().minusDays(1),
                Task.Priority.HIGH,
                "TaskFlow",
                "Intern",
                "DevOps"
        );
        wt5.setTaskUuid("wt001111");
        wt5.setCompleted(true);
        taskService.addTask(wt5);

        // Personal Tasks
        PersonalTask pt1 = taskService.createPersonalTask(
                "Handle mat",
                "Kjøp ingredienser til middag og frokost",
                LocalDate.now().plusDays(1),
                Task.Priority.MEDIUM,
                "Shopping",
                "Rema 1000"
        );
        pt1.setTaskUuid("pt001234");
        taskService.addTask(pt1);

        PersonalTask pt2 = taskService.createPersonalTask(
                "Tannlegetime",
                "Årlig kontroll hos tannlege",
                LocalDate.now().plusDays(7),
                Task.Priority.HIGH,
                "Helse",
                "Oslo sentrum"
        );
        pt2.setTaskUuid("pt005678");
        taskService.addTask(pt2);

        PersonalTask pt3 = taskService.createPersonalTask(
                "Trimavgift",
                "Betale månedlig treningsstudio avgift",
                LocalDate.now().plusDays(2),
                Task.Priority.LOW,
                "Sport",
                "SATS"
        );
        pt3.setTaskUuid("pt009876");
        taskService.addTask(pt3);

        PersonalTask pt4 = taskService.createPersonalTask(
                "Planlegge helgetur",
                "Finne hotell og aktiviteter for helgetur",
                LocalDate.now().plusDays(4),
                Task.Priority.LOW,
                "Reise",
                "Hjemme"
        );
        pt4.setTaskUuid("pt005432");
        taskService.addTask(pt4);

        PersonalTask pt5 = taskService.createPersonalTask(
                "Lese bok",
                "Fullføre \"Clean Code\" bok",
                LocalDate.now().minusDays(2),
                Task.Priority.MEDIUM,
                "Utdanning",
                "Hjemme"
        );
        pt5.setTaskUuid("pt002222");
        pt5.setCompleted(true);
        taskService.addTask(pt5);

        // Additional Work Tasks
        WorkTask wt6 = taskService.createWorkTask(
                "Sikkerheitsgjennomgang",
                "Gjennomgå sikkerhetsrutiner for Q1",
                LocalDate.now().plusDays(10),
                Task.Priority.MEDIUM,
                "Sikkerhet",
                "Intern",
                "IT"
        );
        wt6.setTaskUuid("wt007777");
        taskService.addTask(wt6);

        WorkTask wt7 = taskService.createWorkTask(
                "Dokumentasjon",
                "Oppdatere API dokumentasjon",
                LocalDate.now().plusDays(6),
                Task.Priority.LOW,
                "TaskFlow",
                "Intern",
                "Utvikling"
        );
        wt7.setTaskUuid("wt008888");
        taskService.addTask(wt7);

        // Additional Personal Tasks
        PersonalTask pt6 = taskService.createPersonalTask(
                "Vaskedag",
                "Vaske og rydde i hjemmet",
                LocalDate.now().plusDays(3),
                Task.Priority.LOW,
                "Hjem",
                "Hjemme"
        );
        pt6.setTaskUuid("pt003333");
        taskService.addTask(pt6);

        PersonalTask pt7 = taskService.createPersonalTask(
                "Bursdagsgave",
                "Kjøpe gave til søsters bursdag",
                LocalDate.now().plusDays(8),
                Task.Priority.MEDIUM,
                "Familie",
                "Sentralen"
        );
        pt7.setTaskUuid("pt004444");
        taskService.addTask(pt7);
    }
}