package dao;

import config.DatabaseConnection;
import model.Donor;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DonorDAO {

    public DonorDAO() {
        ensureSchema();
    }

    // Auto-migration: safely check and add last_donation_date column if not already present
    private void ensureSchema() {
        try (Connection con = DatabaseConnection.getConnection()) {
            DatabaseMetaData meta = con.getMetaData();
            try (ResultSet rs = meta.getColumns(null, null, "donors", "last_donation_date")) {
                if (!rs.next()) {
                    try (Statement stmt = con.createStatement()) {
                        stmt.executeUpdate("ALTER TABLE donors ADD COLUMN last_donation_date DATE DEFAULT NULL");
                    }
                }
            }
        } catch (Exception ignored) {
            // Table not yet initialized or column already exists
        }
    }

    public void add(Donor donor) throws SQLException {
        String sql = "INSERT INTO donors(name, blood_group, phone, location, consent_to_share, last_donation_date) VALUES(?,?,?,?,?,?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, donor.getName());
            ps.setString(2, donor.getBloodGroup());
            ps.setString(3, donor.getPhone());
            ps.setString(4, donor.getLocation());
            ps.setBoolean(5, donor.isConsent());

            if (donor.getLastDonationDate() != null) {
                ps.setDate(6, java.sql.Date.valueOf(donor.getLastDonationDate()));
            } else {
                ps.setNull(6, Types.DATE);
            }

            ps.executeUpdate();
        }
    }

    public void updateDonationDate(long id, LocalDate date) throws SQLException {
        String sql = "UPDATE donors SET last_donation_date = ? WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (date != null) {
                ps.setDate(1, java.sql.Date.valueOf(date));
            } else {
                ps.setNull(1, Types.DATE);
            }
            ps.setLong(2, id);

            ps.executeUpdate();
        }
    }

    public List<Donor> search(String bloodGroup) throws SQLException {
        List<Donor> donors = new ArrayList<>();
        String sql;
        boolean filterGroup = bloodGroup != null && !bloodGroup.equalsIgnoreCase("ALL");

        if (filterGroup) {
            sql = "SELECT * FROM donors WHERE blood_group=? AND consent_to_share=TRUE ORDER BY name ASC";
        } else {
            sql = "SELECT * FROM donors WHERE consent_to_share=TRUE ORDER BY blood_group ASC, name ASC";
        }

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (filterGroup) {
                ps.setString(1, bloodGroup);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.sql.Date sqlDate = rs.getDate("last_donation_date");
                    LocalDate lastDonation = sqlDate != null ? sqlDate.toLocalDate() : null;

                    donors.add(new Donor(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("blood_group"),
                            rs.getString("phone"),
                            rs.getString("location"),
                            true,
                            lastDonation
                    ));
                }
            }
        }

        return donors;
    }
}
