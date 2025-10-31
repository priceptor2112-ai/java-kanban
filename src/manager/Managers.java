package manager;

import java.io.File;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    // Новый метод для получения FileBackedTaskManager
    public static FileBackedTaskManager getFileBackedTaskManager(File file) {
        return new FileBackedTaskManager(file);
    }

    // Метод для загрузки из файла
    public static FileBackedTaskManager loadFromFile(File file) {
        return FileBackedTaskManager.loadFromFile(file);
    }
}