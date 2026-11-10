package dao;

import config.DatabaseConnection;
import model.User;
import java.sql.*;

public class UserDAO {

    public boolean create(User user) throws SQLException {

        String sql = "INSERT INTO users(name,email,password_hash) VALUES(?,?,?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());

            return ps.executeUpdate() == 1;
        }
    }

    public User byEmail(String email) throws SQLException {

        String sql = "SELECT * FROM users WHERE email=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                User user = new User(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email")
                );

                user.setPasswordHash(rs.getString("password_hash"));

                return user;
            }
        }
    }

    public boolean updatePassword(String email, String newPasswordHash) throws SQLException {
        String sql = "UPDATE users SET password_hash=? WHERE email=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newPasswordHash);
            ps.setString(2, email);

            return ps.executeUpdate() == 1;
        }
    }
}
