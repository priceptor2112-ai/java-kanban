import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int nextId = 1;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager history = Managers.getDefaultHistory();

    protected final Set<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime,
                    Comparator.nullsLast(Comparator.naturalOrder()))
    );

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.values().forEach(prioritizedTasks::remove);
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.values().forEach(prioritizedTasks::remove);
        subtasks.values().forEach(prioritizedTasks::remove);
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.values().forEach(prioritizedTasks::remove);
        subtasks.clear();

        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
            updateEpicStatus(epic.getId());
            updateEpicTime(epic.getId());
        }
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) history.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) history.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) history.add(subtask);
        return subtask;
    }

    @Override
    public void addTask(Task task) {
        if (task == null) return;

        if (hasTimeOverlap(task)) {
            throw new TimeConflictException("Задача пересекается по времени с существующей задачей");
        }

        task.setId(nextId++);
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic == null) return;

        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (subtask == null) return;

        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            throw new IllegalArgumentException("Эпик с id=" + subtask.getEpicId() + " не существует");
        }

        if (hasTimeOverlap(subtask)) {
            throw new TimeConflictException("Подзадача пересекается по времени с существующей задачей");
        }

        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtaskId(subtask.getId());

        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }

        updateEpicStatus(epic.getId());
        updateEpicTime(epic.getId());
    }

    @Override
    public void updateTask(Task task) {
        if (task == null || !tasks.containsKey(task.getId())) return;

        Task oldTask = tasks.get(task.getId());
        prioritizedTasks.remove(oldTask);

        if (hasTimeOverlap(task)) {
            prioritizedTasks.add(oldTask);
            throw new TimeConflictException("Обновленная задача пересекается по времени с существующей задачей");
        }

        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) return;

        Epic existingEpic = epics.get(epic.getId());
        existingEpic.setTitle(epic.getTitle());
        existingEpic.setDescription(epic.getDescription());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null || !subtasks.containsKey(subtask.getId())) return;

        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            throw new IllegalArgumentException("Эпик с id=" + subtask.getEpicId() + " не существует");
        }

        Subtask oldSubtask = subtasks.get(subtask.getId());
        prioritizedTasks.remove(oldSubtask);

        if (hasTimeOverlap(subtask)) {
            prioritizedTasks.add(oldSubtask);
            throw new TimeConflictException("Обновленная подзадача пересекается по времени с существующей задачей");
        }

        subtasks.put(subtask.getId(), subtask);
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }

        updateEpicStatus(epic.getId());
        updateEpicTime(epic.getId());
    }

    @Override
    public void deleteTask(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            prioritizedTasks.remove(task);
            history.remove(id);
        }
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                Subtask subtask = subtasks.remove(subtaskId);
                if (subtask != null) {
                    prioritizedTasks.remove(subtask);
                    history.remove(subtaskId);
                }
            }
            history.remove(id);
        }
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            prioritizedTasks.remove(subtask);
            history.remove(id);

            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic.getId());
                updateEpicTime(epic.getId());
            }
        }
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return Collections.emptyList();

        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    protected void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        List<Subtask> epicSubtasks = getEpicSubtasks(epicId);

        if (epicSubtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = epicSubtasks.stream().allMatch(subtask -> subtask.getStatus() == Status.NEW);
        boolean allDone = epicSubtasks.stream().allMatch(subtask -> subtask.getStatus() == Status.DONE);

        if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    protected void updateEpicTime(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            List<Subtask> epicSubtasks = getEpicSubtasks(epicId);
            epic.calcTime(epicSubtasks);
        }
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean isTimeOverlapping(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null ||
                task1.getEndTime() == null || task2.getEndTime() == null) {
            return false;
        }

        return !task1.getEndTime().isBefore(task2.getStartTime()) &&
                !task1.getStartTime().isAfter(task2.getEndTime());
    }

    private boolean hasTimeOverlap(Task task) {
        if (task.getStartTime() == null || task.getEndTime() == null) {
            return false;
        }

        return prioritizedTasks.stream()
                .filter(t -> t.getId() != task.getId())
                .filter(t -> t.getStartTime() != null && t.getEndTime() != null)
                .anyMatch(existingTask -> isTimeOverlapping(task, existingTask));
    }
}