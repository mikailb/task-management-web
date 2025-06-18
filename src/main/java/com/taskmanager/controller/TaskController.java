package com.taskmanager.controller;

import com.taskmanager.model.*;
import com.taskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String addTask(@RequestParam String taskType,
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
            LocalDate parsedDate = LocalDate.parse(dueDate);
            Task newTask;

            if ("work".equals(taskType)) {
                newTask = taskService.createWorkTask(title, description, parsedDate, priority,
                        project != null ? project : "",
                        client != null ? client : "",
                        department != null ? department : "");
            } else {
                newTask = taskService.createPersonalTask(title, description, parsedDate, priority,
                        category != null ? category : "Generell",
                        location != null ? location : "");
            }

            taskService.addTask(newTask);
            redirectAttributes.addFlashAttribute("success", "✅ Oppgave '" + title + "' ble lagt til!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Feil ved opprettelse: " + e.getMessage());
        }

        return "redirect:/";
    }

    @PostMapping("/complete-task/{id}")
    public String completeTask(@PathVariable String id, RedirectAttributes redirectAttributes) {
        Optional<Task> task = taskService.findTaskById(id);
        if (taskService.completeTask(id)) {
            redirectAttributes.addFlashAttribute("success", "✅ Oppgave fullført!");
        } else {
            redirectAttributes.addFlashAttribute("error", "❌ Kunne ikke finne oppgave");
        }
        return "redirect:/";
    }

    @PostMapping("/delete-task/{id}")
    public String deleteTask(@PathVariable String id, RedirectAttributes redirectAttributes) {
        Optional<Task> task = taskService.findTaskById(id);
        if (taskService.removeTask(id)) {
            redirectAttributes.addFlashAttribute("success", "🗑️ Oppgave slettet");
        } else {
            redirectAttributes.addFlashAttribute("error", "❌ Kunne ikke slette oppgave");
        }
        return "redirect:/";
    }

    @GetMapping("/edit-task/{id}")
    public String showEditTaskForm(@PathVariable String id, Model model) {
        Optional<Task> task = taskService.findTaskById(id);
        if (task.isPresent()) {
            model.addAttribute("task", task.get());
            model.addAttribute("priorities", Task.Priority.values());
            return "edit-task";
        }
        return "redirect:/";
    }

    @PostMapping("/edit-task/{id}")
    public String editTask(@PathVariable String id,
                           @RequestParam String title,
                           @RequestParam String description,
                           @RequestParam String dueDate,
                           @RequestParam Task.Priority priority,
                           RedirectAttributes redirectAttributes) {

        try {
            LocalDate parsedDate = LocalDate.parse(dueDate);
            if (taskService.updateTask(id, title, description, parsedDate, priority)) {
                redirectAttributes.addFlashAttribute("success", "✏️ Oppgave oppdatert!");
            } else {
                redirectAttributes.addFlashAttribute("error", "❌ Kunne ikke oppdatere oppgave");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Ugyldig dato");
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
}