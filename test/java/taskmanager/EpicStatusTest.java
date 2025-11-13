package taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.service.InMemoryTaskManager;
import taskmanager.service.TaskManager;
import taskmanager.model.Epic;
import taskmanager.model.Subtask;
import taskmanager.model.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EpicStatusTest {
    private TaskManager manager;
    private Epic epic;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
        epic = new Epic("Test Epic", "Epic Description", Status.NEW);
        manager.addEpic(epic);
    }

    @Test
    void epicShouldBeNewWhenNoSubtasks() {
        assertEquals(Status.NEW, epic.getStatus(), "Эпик без подзадач должен быть NEW");
    }

    @Test
    void epicShouldBeNewWhenAllSubtasksNew() {
        // a. Все подзадачи со статусом NEW
        Subtask subtask1 = new Subtask("Subtask 1", "Desc 1", Status.NEW, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Desc 2", Status.NEW, epic.getId(),
                Duration.ofMinutes(45), LocalDateTime.now().plusHours(1));

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        assertEquals(Status.NEW, epic.getStatus(), "Эпик со всеми подзадачами NEW должен быть NEW");
    }

    @Test
    void epicShouldBeDoneWhenAllSubtasksDone() {
        // b. Все подзадачи со статусом DONE
        Subtask subtask1 = new Subtask("Subtask 1", "Desc 1", Status.DONE, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Desc 2", Status.DONE, epic.getId(),
                Duration.ofMinutes(45), LocalDateTime.now().plusHours(1));

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        assertEquals(Status.DONE, epic.getStatus(), "Эпик со всеми подзадачами DONE должен быть DONE");
    }

    @Test
    void epicShouldBeInProgressWhenMixedStatus() {
        // c. Подзадачи со статусами NEW и DONE
        Subtask subtask1 = new Subtask("Subtask 1", "Desc 1", Status.NEW, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Desc 2", Status.DONE, epic.getId(),
                Duration.ofMinutes(45), LocalDateTime.now().plusHours(1));

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(),
                "Эпик с подзадачами NEW и DONE должен быть IN_PROGRESS");
    }

    @Test
    void epicShouldBeInProgressWhenAllSubtasksInProgress() {
        // d. Подзадачи со статусом IN_PROGRESS
        Subtask subtask1 = new Subtask("Subtask 1", "Desc 1", Status.IN_PROGRESS, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Desc 2", Status.IN_PROGRESS, epic.getId(),
                Duration.ofMinutes(45), LocalDateTime.now().plusHours(1));

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(),
                "Эпик со всеми подзадачами IN_PROGRESS должен быть IN_PROGRESS");
    }

    @Test
    void epicShouldUpdateStatusWhenSubtaskChanges() {
        Subtask subtask1 = new Subtask("Subtask 1", "Desc 1", Status.NEW, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Desc 2", Status.NEW, epic.getId(),
                Duration.ofMinutes(45), LocalDateTime.now().plusHours(1));

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        // Проверяем начальный статус
        assertEquals(Status.NEW, epic.getStatus());

        // Меняем одну подзадачу на DONE
        subtask1.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(),
                "Эпик должен стать IN_PROGRESS при изменении статуса подзадачи");

        // Меняем вторую подзадачу на DONE
        subtask2.setStatus(Status.DONE);
        manager.updateSubtask(subtask2);

        assertEquals(Status.DONE, epic.getStatus(),
                "Эпик должен стать DONE когда все подзадачи DONE");
    }

    @Test
    void epicShouldBeNewWhenAllSubtasksRemoved() {
        Subtask subtask1 = new Subtask("Subtask 1", "Desc 1", Status.IN_PROGRESS, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.now());
        manager.addSubtask(subtask1);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());

        // Удаляем подзадачу
        manager.deleteSubtask(subtask1.getId());

        assertEquals(Status.NEW, epic.getStatus(),
                "Эпик без подзадач должен вернуться в статус NEW");
    }

    @Test
    void epicTimeCalculationWithSubtasks() {
        LocalDateTime baseTime = LocalDateTime.now();

        Subtask subtask1 = new Subtask("Subtask 1", "Desc 1", Status.NEW, epic.getId(),
                Duration.ofMinutes(30), baseTime.plusHours(1));
        Subtask subtask2 = new Subtask("Subtask 2", "Desc 2", Status.NEW, epic.getId(),
                Duration.ofMinutes(45), baseTime.plusHours(2));

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        // Проверяем расчет времени эпика
        Epic savedEpic = manager.getEpic(epic.getId());
        assertNotNull(savedEpic.getStartTime(), "Время начала эпика должно быть рассчитано");
        assertNotNull(savedEpic.getEndTime(), "Время окончания эпика должно быть рассчитано");
        assertEquals(Duration.ofMinutes(75), savedEpic.getDuration(),
                "Продолжительность эпика должна быть суммой продолжительностей подзадач");
    }

    @Test
    void shouldGetCorrectEpicSubtasks() {
        Subtask subtask1 = new Subtask("Subtask 1", "Desc 1", Status.NEW, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Desc 2", Status.NEW, epic.getId(),
                Duration.ofMinutes(45), LocalDateTime.now().plusHours(1));

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        List<Subtask> epicSubtasks = manager.getEpicSubtasks(epic.getId());

        assertEquals(2, epicSubtasks.size(), "Должны вернуться все подзадачи эпика");
        assertTrue(epicSubtasks.contains(subtask1), "Список должен содержать первую подзадачу");
        assertTrue(epicSubtasks.contains(subtask2), "Список должен содержать вторую подзадачу");
    }
}