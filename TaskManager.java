import java.util.List;

public interface TaskManager {

    List<Task> getAllTasks();
    void deleteAllTasks();
    Task getTask(int id);
    int createTask(Task task);
    void updateTask(Task task);
    void deleteTask(int id);

    List<Epic> getAllEpics();
    void deleteAllEpics();
    Epic getEpic(int id);
    int createEpic(Epic epic);
    void updateEpic(Epic epic);
    void deleteEpic(int id);

    List<Subtask> getAllSubtasks();
    void deleteAllSubtasks();
    Subtask getSubtask(int id);
    int createSubtask(Subtask subtask);
    void updateSubtask(Subtask subtask);
    void deleteSubtask(int id);

    List<Subtask> getSubtasksByEpic(int epicId);
}