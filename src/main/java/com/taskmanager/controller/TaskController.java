package com.taskmanager.controller;

import com.taskmanager.model.*;
import com.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("tasks", taskService.getAllTasks());
        model.addAttribute("stats", taskService.getStatistics());
        model.addAttribute("upcomingTasks", taskService.getUpcomingTasks());
        return "index";
    }

    @GetMapping("/add-task")
    public String showAddTaskForm(Model model) {
        model.addAttribute("priorities", Task.Priority.values());
        return "add-task";
    }

    @PostMapping("/add-task")
    public String addTask(@Valid @RequestParam String taskType,
                          @RequestParam String title,
                          @RequestParam String description,
                          @RequestParam String dueDate,
                          @RequestParam Task.Priority priority,
                          @RequestParam(required = false) String project,
                          @RequestParam(required = false) String client,
                          @RequestParam(required = false) String department,
                          @RequestParam(required = false) String category,
                          @RequestParam(required = false) String location,
                          RedirectAttributes redirectAttributes) {

        try {
            // Validate input
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Tittel kan ikke være tom");
            }

            if (dueDate == null || dueDate.trim().isEmpty()) {
                throw new IllegalArgumentException("Forfallsdato må angis");
            }

            LocalDate parsedDate = LocalDate.parse(dueDate);
            Task newTask;

            if ("work".equals(taskType)) {
                newTask = taskService.createWorkTask(
                        title.trim(),
                        description != null ? description.trim() : "",
                        parsedDate,
                        priority,
                        project != null ? project.trim() : "",
                        client != null ? client.trim() : "",
                        department != null ? department.trim() : ""
                );
            } else {
                newTask = taskService.createPersonalTask(
                        title.trim(),
                        description != null ? description.trim() : "",
                        parsedDate,
                        priority,
                        category != null ? category.trim() : "Generell",
                        location != null ? location.trim() : ""
                );
            }

            taskService.addTask(newTask);
            redirectAttributes.addFlashAttribute("success",
                    "✅ Oppgave '" + title + "' ble lagt til med ID: " + newTask.getTaskUuid());

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Feil ved opprettelse: " + e.getMessage());
        }

        return "redirect:/";
    }

    @PostMapping("/complete-task/{taskUuid}")
    public String completeTask(@PathVariable String taskUuid, RedirectAttributes redirectAttributes) {
        Optional<Task> task = taskService.findTaskById(taskUuid);
        if (taskService.completeTask(taskUuid)) {
            redirectAttributes.addFlashAttribute("success",
                    "✅ Oppgave '" + task.get().getTitle() + "' fullført!");
        } else {
            redirectAttributes.addFlashAttribute("error", "❌ Kunne ikke finne oppgave med ID: " + taskUuid);
        }
        return "redirect:/";
    }

    @PostMapping("/delete-task/{taskUuid}")
    public String deleteTask(@PathVariable String taskUuid, RedirectAttributes redirectAttributes) {
        Optional<Task> task = taskService.findTaskById(taskUuid);
        if (taskService.removeTask(taskUuid)) {
            redirectAttributes.addFlashAttribute("success",
                    "🗑️ Oppgave '" + task.get().getTitle() + "' slettet");
        } else {
            redirectAttributes.addFlashAttribute("error", "❌ Kunne ikke slette oppgave med ID: " + taskUuid);
        }
        return "redirect:/";
    }

    @GetMapping("/edit-task/{taskUuid}")
    public String showEditTaskForm(@PathVariable String taskUuid, Model model) {
        Optional<Task> task = taskService.findTaskById(taskUuid);
        if (task.isPresent()) {
            model.addAttribute("task", task.get());
            model.addAttribute("priorities", Task.Priority.values());
            return "edit-task";
        } else {
            model.addAttribute("error", "❌ Oppgave ikke funnet");
            return "redirect:/";
        }
    }

    @PostMapping("/edit-task/{taskUuid}")
    public String editTask(@PathVariable String taskUuid,
                           @Valid @RequestParam String title,
                           @RequestParam String description,
                           @RequestParam String dueDate,
                           @RequestParam Task.Priority priority,
                           RedirectAttributes redirectAttributes) {

        try {
            // Validate input
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Tittel kan ikke være tom");
            }

            if (dueDate == null || dueDate.trim().isEmpty()) {
                throw new IllegalArgumentException("Forfallsdato må angis");
            }

            LocalDate parsedDate = LocalDate.parse(dueDate);

            if (taskService.updateTask(taskUuid, title.trim(),
                    description != null ? description.trim() : "", parsedDate, priority)) {
                redirectAttributes.addFlashAttribute("success", "✏️ Oppgave oppdatert!");
            } else {
                redirectAttributes.addFlashAttribute("error", "❌ Kunne ikke oppdatere oppgave");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Feil ved oppdatering: " + e.getMessage());
        }

        return "redirect:/";
    }

    @GetMapping("/upcoming")
    public String upcomingTasks(Model model) {
        model.addAttribute("tasks", taskService.getUpcomingTasks());
        model.addAttribute("pageTitle", "Kommende oppgaver (neste 7 dager)");
        return "task-list";
    }

    @GetMapping("/priority/{priority}")
    public String tasksByPriority(@PathVariable Task.Priority priority, Model model) {
        model.addAttribute("tasks", taskService.getTasksByPriority(priority));
        model.addAttribute("pageTitle", "Oppgaver med " + priority.getDisplayName().toLowerCase() + " prioritet");
        return "task-list";
    }

    @GetMapping("/completed")
    public String completedTasks(Model model) {
        model.addAttribute("tasks", taskService.getCompletedTasks());
        model.addAttribute("pageTitle", "Fullførte oppgaver");
        return "task-list";
    }

    @GetMapping("/pending")
    public String pendingTasks(Model model) {
        model.addAttribute("tasks", taskService.getPendingTasks());
        model.addAttribute("pageTitle", "Aktive oppgaver");
        return "task-list";
    }

    @GetMapping("/overdue")
    public String overdueTasks(Model model) {
        model.addAttribute("tasks", taskService.getOverdueTasks());
        model.addAttribute("pageTitle", "Forfalte oppgaver");
        return "task-list";
    }

    @GetMapping("/search")
    public String searchTasks(@RequestParam String query, Model model) {
        if (query == null || query.trim().isEmpty()) {
            return "redirect:/";
        }

        model.addAttribute("tasks", taskService.searchTasksByTitle(query.trim()));
        model.addAttribute("pageTitle", "Søkeresultater for: \"" + query + "\"");
        model.addAttribute("searchQuery", query);
        return "task-list";
    }

    @PostMapping("/delete-completed")
    public String deleteCompletedTasks(RedirectAttributes redirectAttributes) {
        int deletedCount = taskService.deleteCompletedTasks();
        if (deletedCount > 0) {
            redirectAttributes.addFlashAttribute("success",
                    "🗑️ Slettet " + deletedCount + " fullførte oppgaver");
        } else {
            redirectAttributes.addFlashAttribute("info", "📭 Ingen fullførte oppgaver å slette");
        }
        return "redirect:/";
    }

    // Error handler for this controller
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "❌ En feil oppstod: " + e.getMessage());
        return "redirect:/";
    }
}