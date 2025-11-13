package taskmanager;

import org.junit.jupiter.api.Test;
import taskmanager.model.*;
import taskmanager.service.TaskManager;
import taskmanager.util.TimeConflictException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    @Test
    void addTask() {
        Task task = new Task("Test", "Description", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        manager.addTask(task);

        Task savedTask = manager.getTask(task.getId());
        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Задачи не совпадают");
    }

    @Test
    void addEpic() {
        Epic epic = new Epic("Epic", "Description", Status.NEW);
        manager.addEpic(epic);

        Epic savedEpic = manager.getEpic(epic.getId());
        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(epic, savedEpic, "Эпики не совпадают");
    }

    @Test
    void addSubtask() {
        Epic epic = new Epic("Epic", "Description", Status.NEW);
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Description", Status.NEW, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.now());
        manager.addSubtask(subtask);

        Subtask savedSubtask = manager.getSubtask(subtask.getId());
        assertNotNull(savedSubtask, "Подзадача не найдена");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают");
        assertEquals(epic.getId(), savedSubtask.getEpicId(), "Подзадача должна быть связана с эпиком");
    }

    @Test
    void updateTask() {
        Task task = new Task("Test", "Description", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        manager.addTask(task);

        Task updatedTask = new Task("Updated", "Updated desc", Status.IN_PROGRESS,
                Duration.ofMinutes(45), LocalDateTime.now().plusHours(1));
        updatedTask.setId(task.getId());
        manager.updateTask(updatedTask);

        Task savedTask = manager.getTask(task.getId());
        assertEquals("Updated", savedTask.getTitle(), "Название задачи не обновлено");
        assertEquals(Status.IN_PROGRESS, savedTask.getStatus(), "Статус задачи не обновлен");
    }

    @Test
    void deleteTask() {
        Task task = new Task("Test", "Description", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        manager.addTask(task);

        manager.deleteTask(task.getId());
        assertNull(manager.getTask(task.getId()), "Задача не удалена");
    }

    @Test
    void deleteAllTasks() {
        Task task1 = new Task("Test1", "Description1", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task("Test2", "Description2", Status.NEW,
                Duration.ofMinutes(45), LocalDateTime.now().plusHours(1));
        manager.addTask(task1);
        manager.addTask(task2);

        manager.deleteAllTasks();
        assertTrue(manager.getAllTasks().isEmpty(), "Не все задачи удалены");
    }

    @Test
    void getHistory() {
        Task task = new Task("Test", "Description", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        manager.addTask(task);
        manager.getTask(task.getId());

        List<Task> history = manager.getHistory();
        assertEquals(1, history.size(), "История должна содержать 1 задачу");
        assertEquals(task, history.get(0), "Задача в истории не совпадает");
    }

    @Test
    void addTaskWithTime() {
        Task task = new Task("Test", "Desc", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        manager.addTask(task);

        Task saved = manager.getTask(task.getId());
        assertNotNull(saved);
        assertEquals(task.getDuration(), saved.getDuration());
        assertEquals(task.getStartTime(), saved.getStartTime());
    }

    @Test
    void getPrioritizedTasks() {
        LocalDateTime now = LocalDateTime.now();

        Task task1 = new Task("Task1", "Desc1", Status.NEW,
                Duration.ofMinutes(30), now.plusHours(2));
        Task task2 = new Task("Task2", "Desc2", Status.NEW,
                Duration.ofMinutes(45), now.plusHours(1));

        manager.addTask(task1);
        manager.addTask(task2);

        List<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(2, prioritized.size(), "Должно быть 2 задачи в списке приоритетов");
        assertEquals(task2.getId(), prioritized.get(0).getId(), "Задачи должны быть отсортированы по времени");
    }

    @Test
    void timeOverlapDetection() {
        LocalDateTime base = LocalDateTime.now();

        Task task1 = new Task("Task1", "Desc1", Status.NEW,
                Duration.ofMinutes(60), base);
        manager.addTask(task1);

        Task overlappingTask = new Task("Task2", "Desc2", Status.NEW,
                Duration.ofMinutes(30), base.plusMinutes(30));

        assertThrows(TimeConflictException.class, () -> manager.addTask(overlappingTask),
                "Должно быть выброшено исключение при пересечении времени");
    }

    @Test
    void getEpicSubtasksWithStream() {
        Epic epic = new Epic("Epic", "Desc", Status.NEW);
        manager.addEpic(epic);

        Subtask sub1 = new Subtask("Sub1", "Desc1", Status.NEW, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.now());
        manager.addSubtask(sub1);

        List<Subtask> subs = manager.getEpicSubtasks(epic.getId());
        assertEquals(1, subs.size(), "Должна вернуться 1 подзадача");
        assertEquals(sub1.getId(), subs.get(0).getId(), "ID подзадачи должен совпадать");
    }

    @Test
    void taskWithoutTimeShouldNotBeInPrioritized() {
        Task task = new Task("Test", "Description", Status.NEW, null, null);
        manager.addTask(task);

        List<Task> prioritized = manager.getPrioritizedTasks();
        assertFalse(prioritized.contains(task),
                "Задача без времени не должна быть в списке приоритетов");
    }
}