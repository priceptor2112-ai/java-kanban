import manager.FileBackedTaskManager;
import manager.Managers;
import manager.TaskManager;
import model.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class tests {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        // Тестируем базовый функционал
        testInMemoryManager();

        // Тестируем FileBackedTaskManager
        testFileBackedManager();
    }

    private static void testInMemoryManager() {
        System.out.println("\n=== Тестирование InMemoryTaskManager ===");

        TaskManager manager = Managers.getDefault();

        // Создаем задачи
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.IN_PROGRESS);

        // Создаем эпики
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");

        // Добавляем задачи и эпики
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic1);
        manager.addEpic(epic2);

        // Создаем подзадачи
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.DONE, epic1.getId());
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", Status.NEW, epic2.getId());

        // Добавляем подзадачи
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        // Выводим все задачи
        System.out.println("\nВсе задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\nВсе эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
        }

        System.out.println("\nВсе подзадачи:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        // Тестируем историю
        System.out.println("\nТестируем историю:");
        manager.getTask(task1.getId());
        manager.getEpic(epic1.getId());
        manager.getSubtask(subtask1.getId());

        System.out.println("История просмотров:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

    private static void testFileBackedManager() {
        System.out.println("\n=== Тестирование FileBackedTaskManager ===");

        try {
            // Создаем временный файл
            File tempFile = File.createTempFile("task_manager", ".csv");

            // Запускаем пользовательский сценарий из FileBackedTaskManager
            FileBackedTaskManager.main(new String[]{});

        } catch (IOException e) {
            System.err.println("Ошибка тестирования FileBackedTaskManager: " + e.getMessage());
        }
    }
}