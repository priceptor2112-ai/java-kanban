import java.util.List;

public interface TaskManager {

    List<Task> getAllTasks();

    Task getTask(int id);

    void addTask(Task task);

    void updateTask(Task task);

    void deleteTask(int id);

    void deleteAllTasks();

    List<Epic> getAllEpics();

    Epic getEpic(int id);

    void addEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteEpic(int id);

    void deleteAllEpics();

    List<Subtask> getAllSubtasks();

    Subtask getSubtask(int id);

    void addSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void deleteSubtask(int id);

    void deleteAllSubtasks();

    List<Subtask> getEpicSubtasks(int epicId);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}