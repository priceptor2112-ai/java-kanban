package manager;

import model.Task;
import model.Epic;
import model.Subtask;

import java.util.List;

public interface TaskManager {

    List<Task> getAllTasks();

    void deleteAllTasks();

    Task getTask(int id);

    void addTask(Task task);

    void updateTask(Task task);

    void deleteTask(int id);

    List<Epic> getAllEpics();

    void deleteAllEpics();

    Epic getEpic(int id);

    void addEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteEpic(int id);

    // Методы для Subtask
    List<Subtask> getAllSubtasks();

    void deleteAllSubtasks();

    Subtask getSubtask(int id);

    void addSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void deleteSubtask(int id);

    // Дополнительные методы
    List<Subtask> getSubtasksByEpic(int epicId);

    List<Task> getHistory();
}