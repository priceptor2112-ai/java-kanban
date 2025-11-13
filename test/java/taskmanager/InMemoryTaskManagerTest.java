import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    protected InMemoryTaskManager manager;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void addAndGetTask() {
        Task task = new Task("Test", "Description", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        manager.addTask(task);

        Task saved = manager.getTask(task.getId());
        assertNotNull(saved, "Задача должна быть найдена");
        assertEquals(task.getTitle(), saved.getTitle(), "Названия должны совпадать");
    }

    @Test
    void addAndGetEpic() {
        Epic epic = new Epic("Epic", "Description", Status.NEW);
        manager.addEpic(epic);

        Epic saved = manager.getEpic(epic.getId());
        assertNotNull(saved, "Эпик должен быть найден");
        assertEquals(epic.getTitle(), saved.getTitle(), "Названия должны совпадать");
    }

    @Test
    void addAndGetSubtask() {
        Epic epic = new Epic("Epic", "Description", Status.NEW);
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Description", Status.NEW, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.now());
        manager.addSubtask(subtask);

        Subtask saved = manager.getSubtask(subtask.getId());
        assertNotNull(saved, "Подзадача должна быть найдена");
        assertEquals(subtask.getTitle(), saved.getTitle(), "Названия должны совпадать");
    }
}