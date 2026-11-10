package dao;

import config.DatabaseConnection;
import model.Course;
import java.sql.*;

public class CourseDAO {

    public void add(long userId, Course course, String semester)
            throws SQLException {

        String sql = "INSERT INTO courses(user_id,course_name,credit,grade_point,semester) VALUES(?,?,?,?,?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setString(2, course.getName());
            ps.setDouble(3, course.getCredit());
            ps.setDouble(4, course.getGradePoint());
            ps.setString(5, semester);

            ps.executeUpdate();
        }
    }
}