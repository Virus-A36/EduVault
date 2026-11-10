
package dao;

import config.DatabaseConnection;
import model.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.Date;

import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    // Add Task
    public void add(Task task) throws SQLException {

        String sql = "INSERT INTO tasks(user_id,title,description,due_date,status) VALUES(?,?,?,?,?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, task.getUserId());
            ps.setString(2, task.getTitle());
            ps.setString(3, task.getDescription());

            if (task.getDueDate() == null) {
                ps.setNull(4, Types.DATE);
            } else {
                ps.setDate(
                        4,
                        Date.valueOf(task.getDueDate())
                );
            }

            ps.setString(5, task.getStatus());

            ps.executeUpdate();
        }
    }

    // List Tasks
    public List<Task> list(long userId) throws SQLException {

        List<Task> tasks = new ArrayList<>();

        String sql = "SELECT * FROM tasks WHERE user_id=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Date date = rs.getDate("due_date");

                    tasks.add(new Task(
                            rs.getLong("id"),
                            userId,
                            rs.getString("title"),
                            rs.getString("description"),
                            date == null ? null : date.toLocalDate(),
                            rs.getString("status")
                    ));
                }
            }
        }

        return tasks;
    }

    // Update Task Status
    public void status(long id, long userId, String status)
            throws SQLException {

        String sql = "UPDATE tasks SET status=? WHERE id=? AND user_id=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setLong(2, id);
            ps.setLong(3, userId);

            ps.executeUpdate();
        }
    }

    // Delete Task
    public void delete(long id, long userId) throws SQLException {

        String sql = "DELETE FROM tasks WHERE id=? AND user_id=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.setLong(2, userId);

            ps.executeUpdate();
        }
    }
}
