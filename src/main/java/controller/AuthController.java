package controller;

import dao.UserDAO;
import model.User;
import util.SecurityUtil;
import util.ValidationUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthController {

    private static final Logger LOGGER =
            Logger.getLogger(AuthController.class.getName());

    private final UserDAO userDAO = new UserDAO();

    public boolean isEmailRegistered(String email) {
        try {
            if (email == null) return false;
            return userDAO.byEmail(email.trim()) != null;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error checking email registration", e);
            return false;
        }
    }

    // Registration
    public boolean register(String name, String email,
                            char[] password) {

        try {
            // Clean input
            name = name == null ? null : name.trim();
            email = email == null ? null : email.trim();

            // Validate input
            ValidationUtil.validateName(name);
            ValidationUtil.validateEmail(email);

            if (password == null) {
                throw new IllegalArgumentException(
                        "Password cannot be empty!"
                );
            }

            ValidationUtil.validatePassword(
                    new String(password)
            );

            // Check duplicate email
            if (userDAO.byEmail(email) != null) {
                throw new IllegalArgumentException(
                        "This email is already registered!"
                );
            }

            // Hash password
            String hash = SecurityUtil.hash(password);

            // Create user
            User user = new User(name, email, hash);

            return userDAO.create(user);

        } catch (IllegalArgumentException e) {
            // Send validation error to RegisterFrame
            throw e;

        } catch (Exception e) {
            LOGGER.log(
                    Level.SEVERE,
                    "Registration failed",
                    e
            );

            return false;
        }
    }

    // Login
    public User login(String email, char[] password) {

        try {
            email = email == null ? null : email.trim();

            ValidationUtil.validateEmail(email);

            if (password == null || password.length == 0) {
                return null;
            }

            // Find user
            User user = userDAO.byEmail(email);

            if (user == null) {
                return null;
            }

            // Verify password
            if (SecurityUtil.verify(
                    password,
                    user.getPasswordHash()
            )) {
                return user;
            }

            return null;

        } catch (Exception e) {
            LOGGER.log(
                    Level.SEVERE,
                    "Login failed",
                    e
            );

            return null;
        }
    }

    // Reset Password
    public boolean resetPassword(String email, char[] newPassword) {
        try {
            email = email == null ? null : email.trim();
            ValidationUtil.validateEmail(email);

            if (newPassword == null || newPassword.length == 0) {
                throw new IllegalArgumentException("New password cannot be empty!");
            }

            ValidationUtil.validatePassword(new String(newPassword));

            User user = userDAO.byEmail(email);
            if (user == null) {
                throw new IllegalArgumentException("No account found with this email.");
            }

            String hash = SecurityUtil.hash(newPassword);
            return userDAO.updatePassword(email, hash);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Password reset failed", e);
            return false;
        }
    }
}
