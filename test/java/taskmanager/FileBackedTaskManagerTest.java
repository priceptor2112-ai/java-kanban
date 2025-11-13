package taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.service.FileBackedTaskManager;
import taskmanager.util.ManagerSaveException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("tasks", ".csv").toFile();
        manager = new FileBackedTaskManager(tempFile);
    }

    @Test
    void saveAndLoadWithTime() {
        LocalDateTime now = LocalDateTime.now();

        Task task = new Task("Test", "Desc", Status.NEW,
                Duration.ofMinutes(30), now);
        manager.addTask(task);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        Task loadedTask = loaded.getTask(task.getId());

        assertEquals(task.getDuration(), loadedTask.getDuration(), "Продолжительность должна совпадать");
        assertEquals(task.getStartTime(), loadedTask.getStartTime(), "Время начала должно совпадать");
    }

    @Test
    void saveAndLoadEpicWithSubtasks() {
        Epic epic = new Epic("Epic", "Desc", Status.NEW);
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Desc", Status.NEW, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.now());
        manager.addSubtask(subtask);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        Epic loadedEpic = loaded.getEpic(epic.getId());
        Subtask loadedSubtask = loaded.getSubtask(subtask.getId());

        assertNotNull(loadedEpic, "Эпик должен быть загружен");
        assertNotNull(loadedSubtask, "Подзадача должна быть загружена");
        assertEquals(epic.getTitle(), loadedEpic.getTitle(), "Название эпика должно совпадать");
        assertEquals(subtask.getTitle(), loadedSubtask.getTitle(), "Название подзадачи должно совпадать");
        assertEquals(epic.getId(), loadedSubtask.getEpicId(), "ID эпика в подзадаче должен совпадать");
    }

    @Test
    void shouldThrowExceptionWhenFileNotFound() {
        File nonExistentFile = new File("nonexistent.csv");
        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(nonExistentFile),
                "Должно быть выброшено исключение при загрузке из несуществующего файла");
    }
}