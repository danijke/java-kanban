import model.*;
import service.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Вебинары", "Посмотреть вебинары 3-4 спринта");
        taskManager.addTask(task1);

        Task task2 = new Task("Push", "Придумать название для push");
        taskManager.addTask(task2);

        Epic epic1 = new Epic("1-й модуль", "Завершить первый модуль до 16-го сентября");
        taskManager.addTask(epic1);

        Subtask subtask1 = new Subtask("4-спринт", "Сдать финальное тз 4-го спринта", epic1.getId());
        taskManager.addTask(subtask1);

        Subtask subtask2 = new Subtask("5-спринт", "Успеть пройти 5-й спринт до конца модуля", epic1.getId());
        taskManager.addTask(subtask2);

        Epic epic2 = new Epic("Оплата", "Оплатить следующий модуль");
        taskManager.addTask(epic2);

        Subtask subtask3 = new Subtask("Работа", "Найти работу для оплаты учебы", epic2.getId());
        taskManager.addTask(subtask3);

        printTasks(taskManager.getAllTasks());

        task1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task1);

        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(subtask2);

        subtask3.setStatus(TaskStatus.DONE);
        taskManager.updateTask(subtask3);

        printTasks(taskManager.getAllTasks());

        taskManager.removeTaskById(task2.getId());
        taskManager.removeSubtaskById(subtask1.getId());

        printTasks(taskManager.getAllTasks());

    }

    private static void printTasks(ArrayList<Task> allTasks) {
        System.out.println("Список задач: ");
        System.out.println(allTasks);
    }
}

