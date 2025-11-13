package taskmanager;

import taskmanager.service.TaskManager;
import taskmanager.service.Managers;
import taskmanager.model.Task;
import taskmanager.model.Epic;
import taskmanager.model.Subtask;
import taskmanager.model.Status;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        // Тест создания задач с временем
        Task task1 = new Task("Task 1", "Description 1", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        manager.addTask(task1);

        Epic epic1 = new Epic("Epic 1", "Epic description", Status.NEW);
        manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Subtask 1", "Sub desc", Status.NEW, epic1.getId(),
                Duration.ofMinutes(45), LocalDateTime.now().plusHours(2));
        manager.addSubtask(subtask1);

        System.out.println("=== Все задачи ===");
        System.out.println("Tasks: " + manager.getAllTasks());
        System.out.println("Epics: " + manager.getAllEpics());
        System.out.println("Subtasks: " + manager.getAllSubtasks());

        System.out.println("\n=== Приоритетные задачи ===");
        System.out.println("Prioritized: " + manager.getPrioritizedTasks());

        System.out.println("\n=== История ===");
        System.out.println("History: " + manager.getHistory());

        System.out.println("\n=== Время эпика ===");
        Epic savedEpic = manager.getEpic(epic1.getId());
        System.out.println("Epic start: " + savedEpic.getStartTime());
        System.out.println("Epic end: " + savedEpic.getEndTime());
        System.out.println("Epic duration: " + savedEpic.getDuration());
    }
}