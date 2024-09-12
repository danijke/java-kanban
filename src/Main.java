import model.*;
import service.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Вебинары", "Посмотреть вебинары 3-4 спринта");
        taskManager.addTask(task1);

        Task task2 = new Task("Push", "Придумать название для push");
        taskManager.addTask(task2);

        Epic epic1 = new Epic("1-й модуль", "Завершить первый модуль до 16-го сентября");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("4-спринт", "Сдать финальное тз 4-го спринта", epic1.getId());
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("5-спринт", "Успеть пройти 5-й спринт до конца модуля", epic1.getId());
        taskManager.addSubtask(subtask2);

        Epic epic2 = new Epic("Оплата", "Оплатить следующий модуль");
        taskManager.addEpic(epic2);

        Subtask subtask3 = new Subtask("Работа", "Найти работу для оплаты учебы", epic2.getId());
        taskManager.addSubtask(subtask3);

        printTasks(taskManager);

        task1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task1);

        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);

        subtask3.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask3);

        printTasks(taskManager);

        taskManager.removeTaskById(task2.getId());
        taskManager.clearSubtasks();

        printTasks(taskManager);
    }

    private static void printTasks(TaskManager taskManager) {
        System.out.println("Список задач: ");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
    }
}