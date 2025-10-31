package manager;

import model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    //region Переопределенные методы с автосохранением
    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }
    //endregion

    // Метод сохранения в файл
    protected void save() {
        try (PrintWriter writer = new PrintWriter(file)) {
            // Записываем заголовок
            writer.println("id,type,name,status,description,epic,duration,startTime");

            // Сохраняем все задачи
            for (Task task : getAllTasks()) {
                writer.println(toString(task));
            }
            for (Epic epic : getAllEpics()) {
                writer.println(toString(epic));
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.println(toString(subtask));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл: " + file.getName(), e);
        }
    }

    // Преобразование задачи в строку CSV
    private String toString(Task task) {
        String epicId = "";
        if (task instanceof Subtask) {
            epicId = String.valueOf(((Subtask) task).getEpicId());
        }
        String startTime = task.getStartTime() != null ?
                task.getStartTime().format(Task.DATE_TIME_FORMATTER) : "";
        return String.format("%d,%s,%s,%s,%s,%s,%d,%s",
                task.getId(),
                getType(task).name(),
                task.getTitle(),
                task.getStatus().name(),
                task.getDescription(),
                epicId,
                task.getDuration(),
                startTime
        );
    }

    // Определение типа задачи
    private TaskType getType(Task task) {
        if (task instanceof Epic) {
            return TaskType.EPIC;
        } else if (task instanceof Subtask) {
            return TaskType.SUBTASK;
        } else {
            return TaskType.TASK;
        }
    }

    // Создание задачи из строки
    private Task fromString(String value) {
        String[] fields = value.split(",", -1); // -1 чтобы сохранить пустые поля
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        String epicIdStr = fields[5];
        String durationStr = fields[6];
        String startTimeStr = fields[7];

        int epicId = epicIdStr.isEmpty() ? 0 : Integer.parseInt(epicIdStr);
        long duration = durationStr.isEmpty() ? 0 : Long.parseLong(durationStr);
        LocalDateTime startTime = startTimeStr.isEmpty() ? null :
                LocalDateTime.parse(startTimeStr, Task.DATE_TIME_FORMATTER);

        switch (type) {
            case TASK:
                Task task = new Task(name, description, status, duration, startTime);
                task.setId(id);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                return epic;
            case SUBTASK:
                Subtask subtask = new Subtask(name, description, status, epicId, duration, startTime);
                subtask.setId(id);
                return subtask;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    // Статический метод для загрузки из файла
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            if (!file.exists()) {
                return manager;
            }

            String content = Files.readString(Path.of(file.getAbsolutePath()));
            String[] lines = content.split("\n");

            if (lines.length <= 1) {
                return manager; // только заголовок или пустой файл
            }

            // Пропускаем заголовок
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) continue;

                try {
                    Task task = manager.fromString(line);

                    // Восстанавливаем задачи в соответствующие коллекции
                    if (task instanceof Epic) {
                        manager.epics.put(task.getId(), (Epic) task);
                    } else if (task instanceof Subtask) {
                        manager.subtasks.put(task.getId(), (Subtask) task);
                        // Добавляем подзадачу в эпик
                        Epic epic = manager.epics.get(((Subtask) task).getEpicId());
                        if (epic != null) {
                            epic.addSubtaskId(task.getId());
                        }
                    } else {
                        manager.tasks.put(task.getId(), task);
                    }

                    // Обновляем счетчик id
                    if (task.getId() >= manager.idCounter) {
                        manager.idCounter = task.getId() + 1;
                    }
                } catch (Exception e) {
                    System.err.println("Ошибка при чтении строки: " + line);
                    e.printStackTrace();
                }
            }

            // Обновляем статусы эпиков после загрузки всех подзадач
            for (Epic epic : manager.epics.values()) {
                manager.updateEpicStatus(epic.getId());
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла: " + file.getName(), e);
        }

        return manager;
    }

    // Дополнительное задание: пользовательский сценарий
    public static void main(String[] args) {
        try {
            // Создаем временный файл
            File tempFile = File.createTempFile("tasks", ".csv");
            System.out.println("Файл для сохранения: " + tempFile.getAbsolutePath());

            // Тестируем FileBackedTaskManager
            testFileBackedTaskManager(tempFile);

        } catch (IOException e) {
            System.err.println("Ошибка создания временного файла: " + e.getMessage());
        }
    }

    private static void testFileBackedTaskManager(File file) {
        System.out.println("\n=== Тестирование FileBackedTaskManager ===");

        // Создаем первый менеджер и добавляем задачи
        FileBackedTaskManager manager1 = new FileBackedTaskManager(file);

        // Создаем задачи разных типов
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW, 60,
                LocalDateTime.of(2024, 1, 15, 10, 0));
        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.IN_PROGRESS, 30,
                LocalDateTime.of(2024, 1, 15, 14, 0));

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");

        manager1.addTask(task1);
        manager1.addTask(task2);
        manager1.addEpic(epic1);
        manager1.addEpic(epic2);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1",
                Status.DONE, epic1.getId(), 45, LocalDateTime.of(2024, 1, 16, 9, 0));
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2",
                Status.NEW, epic1.getId(), 20, LocalDateTime.of(2024, 1, 16, 11, 0));

        manager1.addSubtask(subtask1);
        manager1.addSubtask(subtask2);

        System.out.println("Добавлено в первый менеджер:");
        System.out.println("Задачи: " + manager1.getAllTasks().size());
        System.out.println("Эпики: " + manager1.getAllEpics().size());
        System.out.println("Подзадачи: " + manager1.getAllSubtasks().size());

        // Создаем второй менеджер из того же файла
        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(file);

        System.out.println("\nЗагружено во второй менеджер:");
        System.out.println("Задачи: " + manager2.getAllTasks().size());
        System.out.println("Эпики: " + manager2.getAllEpics().size());
        System.out.println("Подзадачи: " + manager2.getAllSubtasks().size());

        // Проверяем, что данные совпадают
        boolean tasksMatch = manager1.getAllTasks().size() == manager2.getAllTasks().size();
        boolean epicsMatch = manager1.getAllEpics().size() == manager2.getAllEpics().size();
        boolean subtasksMatch = manager1.getAllSubtasks().size() == manager2.getAllSubtasks().size();

        System.out.println("\nРезультаты проверки:");
        System.out.println("Задачи совпадают: " + tasksMatch);
        System.out.println("Эпики совпадают: " + epicsMatch);
        System.out.println("Подзадачи совпадают: " + subtasksMatch);

        // Выводим содержимое файла
        System.out.println("\nСодержимое файла:");
        try {
            String content = Files.readString(file.toPath());
            System.out.println(content);
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }

        // Проверяем восстановление связей
        Epic loadedEpic = manager2.getEpic(epic1.getId());
        if (loadedEpic != null) {
            System.out.println("\nПодзадачи восстановленного эпика:");
            for (Subtask subtask : manager2.getSubtasksByEpic(loadedEpic.getId())) {
                System.out.println("  - " + subtask.getTitle() + " (статус: " + subtask.getStatus() + ")");
            }
        }
    }
}