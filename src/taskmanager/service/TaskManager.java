package taskmanager.service;

import taskmanager.model.Task;
import taskmanager.model.Epic;
import taskmanager.model.Subtask;
import java.util.List;

public interface TaskManager {
    List<Task> getAllTasks();
    List<Epic> getAllEpics();
    List<Subtask> getAllSubtasks();
    void deleteAllTasks();
    void deleteAllEpics();
    void deleteAllSubtasks();
    Task getTask(int id);
    Epic getEpic(int id);
    Subtask getSubtask(int id);
    void addTask(Task task);
    void addEpic(Epic epic);
    void addSubtask(Subtask subtask);
    void updateTask(Task task);
    void updateEpic(Epic epic);
    void updateSubtask(Subtask subtask);
    void deleteTask(int id);
    void deleteEpic(int id);
    void deleteSubtask(int id);
    List<Subtask> getEpicSubtasks(int epicId);
    List<Task> getHistory();
    List<Task> getPrioritizedTasks();
}