package taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.service.HistoryManager;
import taskmanager.service.InMemoryHistoryManager;
import taskmanager.model.Task;
import taskmanager.model.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {
    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task("Task1", "Desc1", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        task1.setId(1);
        task2 = new Task("Task2", "Desc2", Status.NEW,
                Duration.ofMinutes(45), LocalDateTime.now().plusHours(1));
        task2.setId(2);
        task3 = new Task("Task3", "Desc3", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.now().plusHours(2));
        task3.setId(3);
    }

    @Test
    void addToHistory() {
        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать 2 задачи");
        assertTrue(history.contains(task1), "История должна содержать task1");
        assertTrue(history.contains(task2), "История должна содержать task2");
    }

    @Test
    void emptyHistory() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой");
    }

    @Test
    void removeDuplicates() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1); // Дублирование

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История не должна содержать дубликатов");
        assertEquals(task2, history.get(0), "Первой должна быть task2");
        assertEquals(task1, history.get(1), "Последней должна быть task1");
    }

    @Test
    void removeFromBeginning() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(1); // Удаляем из начала

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать 2 задачи");
        assertFalse(history.contains(task1), "История не должна содержать task1");
        assertEquals(task2, history.get(0), "Первой должна быть task2");
    }

    @Test
    void removeFromMiddle() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(2); // Удаляем из середины

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать 2 задачи");
        assertFalse(history.contains(task2), "История не должна содержать task2");
    }

    @Test
    void removeFromEnd() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(3); // Удаляем с конца

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать 2 задачи");
        assertFalse(history.contains(task3), "История не должна содержать task3");
    }

    @Test
    void removeNonExistent() {
        historyManager.add(task1);
        historyManager.remove(999); // Удаляем несуществующий

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать 1 задачу");
    }
}