package com.taskmanager.model;

import org.springframework.stereotype.Component;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TaskManager {
    private List<Task> tasks;
    private static final String FILE_NAME = "web_tasks.dat";

    public TaskManager() {
        tasks = new ArrayList<>();
        loadTasks();
    }

    @SuppressWarnings("unchecked")
    private void loadTasks() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            tasks = (List<Task>) ois.readObject();
            System.out.println("✅ Lastet " + tasks.size() + " oppgaver fra fil");
        } catch (FileNotFoundException e) {
            System.out.println("📝 Ingen lagrede oppgaver. Starter med tom liste.");
            createSampleTasks();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("⚠️ Feil ved lasting: " + e.getMessage());
            createSampleTasks();
        }
    }

    private void createSampleTasks() {
        // Legg til noen eksempel-oppgaver
        addTask(new WorkTask("Ferdigstille prosjektrapport", "Skriv sammendrag og konklusjon",
                LocalDate.now().plusDays(3), Task.Priority.HIGH, "Prosjekt Alpha", "Kunde AS", "IT"));

        addTask(new PersonalTask("Handle mat", "Kjøp ingredienser til middag",
                LocalDate.now().plusDays(1), Task.Priority.MEDIUM, "Hjem", "Rema 1000"));

        addTask(new WorkTask("Teammøte", "Ukentlig statusmøte",
                LocalDate.now().plusDays(2), Task.Priority.LOW, "Drift", "Intern", "IT"));

        saveTasks();
    }

    public synchronized void saveTasks() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(tasks);
            System.out.println("💾 Oppgaver lagret");
        } catch (IOException e) {
            System.out.println("⚠️ Feil ved lagring: " + e.getMessage());
        }
    }

    public void addTask(Task task) {
        tasks.add(task);
        saveTasks();
    }

    public boolean removeTask(String id) {
        boolean removed = tasks.removeIf(task -> task.getId().equals(id));
        if (removed) saveTasks();
        return removed;
    }

    public boolean completeTask(String id) {
        Optional<Task> task = findTaskById(id);
        if (task.isPresent()) {
            task.get().setCompleted(true);
            saveTasks();
            return true;
        }
        return false;
    }

    public Optional<Task> findTaskById(String id) {
        return tasks.stream().filter(task -> task.getId().equals(id)).findFirst();
    }

    public List<Task> getAllTasks() {
        return tasks.stream()
                .sorted((t1, t2) -> {
                    if (t1.isCompleted() != t2.isCompleted()) {
                        return Boolean.compare(t1.isCompleted(), t2.isCompleted());
                    }
                    return t1.getDueDate().compareTo(t2.getDueDate());
                })
                .collect(Collectors.toList());
    }

    public List<Task> getUpcomingTasks() {
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);

        return tasks.stream()
                .filter(task -> !task.isCompleted())
                .filter(task -> !task.getDueDate().isBefore(today))
                .filter(task -> !task.getDueDate().isAfter(nextWeek))
                .sorted(Comparator.comparing(Task::getDueDate))
                .collect(Collectors.toList());
    }

    public List<Task> getTasksByPriority(Task.Priority priority) {
        return tasks.stream()
                .filter(task -> task.getPriority() == priority)
                .sorted(Comparator.comparing(Task::getDueDate))
                .collect(Collectors.toList());
    }

    public Map<String, Long> getStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", (long) tasks.size());
        stats.put("completed", tasks.stream().filter(Task::isCompleted).count());
        stats.put("pending", tasks.stream().filter(task -> !task.isCompleted()).count());
        stats.put("overdue", tasks.stream().filter(Task::isOverdue).count());
        return stats;
    }
}