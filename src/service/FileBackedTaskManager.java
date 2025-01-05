package service;

import exception.ManagerSaveException;
import model.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    Path path;

    public FileBackedTaskManager(HistoryManager historyManager, Path path) {
        super(historyManager);
        this.path = path;

    }

    public static FileBackedTaskManager loadFromFile(Path path) {
        FileBackedTaskManager fileManager = new FileBackedTaskManager(new InMemoryHistoryManager(), path);
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                Task.fromString(line, fileManager);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка в файле: " + e.getMessage() + path, e);
        }
        return fileManager;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic task) {
        super.addEpic(task);
        save();
    }

    @Override
    public void addSubtask(Subtask task) {
        super.addSubtask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic task) {
        super.updateEpic(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask task) {
        super.updateSubtask(task);
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    private void save() {
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            if (Files.notExists(path)) {
                throw new IOException("файл не найден. ");
            }
            List<Task> allTasks = getSortedTasksById();
            bw.write("id,type,name,status,description,epic,time,duration\n");
            for (Task task : allTasks) {
                bw.write(task.toString());
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка в файле: " + e.getMessage() + path.toAbsolutePath(), e);
        }
    }
}
//todo исправить очередь id при сохранении