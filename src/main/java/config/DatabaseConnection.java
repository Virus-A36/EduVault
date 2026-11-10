package config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DatabaseConnection {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static final String CONFIG_FILE = "database.properties";
    private static volatile boolean schemaInitialized = false;

    private DatabaseConnection() {}

    private static Properties loadProperties() {
        Properties props = new Properties();
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            try (FileInputStream in = new FileInputStream(file)) {
                props.load(in);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Could not load " + CONFIG_FILE, e);
            }
        }
        return props;
    }

    public static String getUrl() {
        Properties props = loadProperties();
        return props.getProperty("db.url", System.getenv().getOrDefault(
                "EDUVAULT_DB_URL",
                "jdbc:mysql://localhost:3306/eduvault?serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false"
        ));
    }

    public static String getUser() {
        Properties props = loadProperties();
        return props.getProperty("db.user", System.getenv().getOrDefault("EDUVAULT_DB_USER", "root"));
    }

    public static String getPassword() {
        Properties props = loadProperties();
        return props.getProperty("db.password", System.getenv().getOrDefault("EDUVAULT_DB_PASSWORD", ""));
    }

    public static void saveConfig(String url, String user, String pass) throws Exception {
        Properties props = new Properties();
        props.setProperty("db.url", url.trim());
        props.setProperty("db.user", user.trim());
        props.setProperty("db.password", pass != null ? pass : "");
        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, "EduVault Database Config");
        }
        schemaInitialized = false; // re-run schema init on next connection
    }

    public static boolean testConnection(String url, String user, String pass) {
        try (Connection con = DriverManager.getConnection(url, user, pass)) {
            return con.isValid(3);
        } catch (Exception e) {
            return false;
        }
    }

    public static Connection getConnection() throws SQLException {

        String url = getUrl();
        String user = getUser();
        String pass = getPassword();

        if (!schemaInitialized) {
            synchronized (DatabaseConnection.class) {
                if (!schemaInitialized) {
                    initSchemaIfNeeded(url, user, pass);
                    schemaInitialized = true;
                }
            }
        }

        return DriverManager.getConnection(url, user, pass);
    }

    private static void initSchemaIfNeeded(String fullUrl, String user, String pass) {
        // 1. Try to ensure database exists by connecting to host root
        try {
            String baseUrl = fullUrl;
            int slashIdx = fullUrl.indexOf('/', "jdbc:mysql://".length());
            if (slashIdx != -1) {
                int qIdx = fullUrl.indexOf('?', slashIdx);
                String params = qIdx != -1 ? fullUrl.substring(qIdx) : "?serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false";
                baseUrl = fullUrl.substring(0, slashIdx + 1) + params;
            }

            try (Connection rootCon = DriverManager.getConnection(baseUrl, user, pass);
                 Statement stmt = rootCon.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS eduvault");
            }
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Could not create database from base URL: " + e.getMessage());
        }

        // 2. Connect to eduvault and ensure all tables exist
        try (Connection con = DriverManager.getConnection(fullUrl, user, pass);
             Statement stmt = con.createStatement()) {

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users ("
                    + "id BIGINT PRIMARY KEY AUTO_INCREMENT,"
                    + "name VARCHAR(100) NOT NULL,"
                    + "email VARCHAR(150) NOT NULL UNIQUE,"
                    + "password_hash VARCHAR(255) NOT NULL,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS credentials ("
                    + "id BIGINT PRIMARY KEY AUTO_INCREMENT,"
                    + "user_id BIGINT NOT NULL,"
                    + "website VARCHAR(200) NOT NULL,"
                    + "username VARCHAR(150) NOT NULL,"
                    + "encrypted_secret TEXT NOT NULL,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
                    + ")");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS tasks ("
                    + "id BIGINT PRIMARY KEY AUTO_INCREMENT,"
                    + "user_id BIGINT NOT NULL,"
                    + "title VARCHAR(200) NOT NULL,"
                    + "description TEXT,"
                    + "due_date DATE,"
                    + "status ENUM('Pending', 'Completed') DEFAULT 'Pending',"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
                    + ")");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS donors ("
                    + "id BIGINT PRIMARY KEY AUTO_INCREMENT,"
                    + "name VARCHAR(100) NOT NULL,"
                    + "blood_group VARCHAR(5) NOT NULL,"
                    + "phone VARCHAR(20) NOT NULL,"
                    + "location VARCHAR(200) NOT NULL,"
                    + "consent_to_share BOOLEAN NOT NULL DEFAULT FALSE,"
                    + "last_donation_date DATE DEFAULT NULL,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS courses ("
                    + "id BIGINT PRIMARY KEY AUTO_INCREMENT,"
                    + "user_id BIGINT NOT NULL,"
                    + "course_name VARCHAR(150) NOT NULL,"
                    + "credit DECIMAL(5,2) NOT NULL,"
                    + "grade_point DECIMAL(3,2) NOT NULL,"
                    + "semester VARCHAR(50),"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
                    + ")");

            System.out.println(">> EduVault schema verified successfully.");

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Auto-initialization of tables notice: " + e.getMessage());
        }
    }
}
