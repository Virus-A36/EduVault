package dao;

import config.DatabaseConnection;
import model.Credential;
import java.sql.*;
import java.util.*;

public class VaultDAO {

    public VaultDAO() {
        ensureSchema();
    }

    private void ensureSchema() {
        String sql = "CREATE TABLE IF NOT EXISTS credentials ("
                + "id BIGINT PRIMARY KEY AUTO_INCREMENT,"
                + "user_id BIGINT NOT NULL,"
                + "website VARCHAR(200) NOT NULL,"
                + "username VARCHAR(150) NOT NULL,"
                + "encrypted_secret TEXT NOT NULL,"
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
                + ")";
        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (Exception ignored) {
        }
    }

    public void add(Credential credential) throws SQLException {

        String sql = "INSERT INTO credentials(user_id,website,username,encrypted_secret) VALUES(?,?,?,?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, credential.getUserId());
            ps.setString(2, credential.getWebsite());
            ps.setString(3, credential.getUsername());
            ps.setString(4, credential.getSecret());

            ps.executeUpdate();
        }
    }

    public List<Credential> list(long userId) throws SQLException {

        List<Credential> credentials = new ArrayList<>();

        String sql = "SELECT * FROM credentials WHERE user_id=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    credentials.add(new Credential(
                            rs.getLong("id"),
                            userId,
                            rs.getString("website"),
                            rs.getString("username"),
                            rs.getString("encrypted_secret")
                    ));
                }
            }
        }

        return credentials;
    }

    public void delete(long id, long userId) throws SQLException {

        String sql = "DELETE FROM credentials WHERE id=? AND user_id=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.setLong(2, userId);

            ps.executeUpdate();
        }
    }
}
