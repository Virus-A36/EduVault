package controller;

import dao.TaskDAO;
import model.Task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskController {

    private final TaskDAO taskDAO = new TaskDAO();

    public boolean addTask(long userId, String title,
                           String description, LocalDate dueDate) {

        try {
            if (title == null || title.trim().isEmpty()) {
                return false;
            }

            Task task = new Task(
                    userId, title, description, dueDate
            );

            taskDAO.add(task);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Task> getTasks(long userId) {

        try {
            return taskDAO.list(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean updateStatus(long id, long userId,
                                String status) {

        try {
            if (!status.equals("Pending")
                    && !status.equals("Completed")) {
                return false;
            }

            taskDAO.status(id, userId, status);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTask(long id, long userId) {

        try {
            taskDAO.delete(id, userId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
