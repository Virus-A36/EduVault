
package util;

public class ValidationUtil {

    // Name Validation
    public static void validateName(String name) {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Name cannot be empty!"
            );
        }

        if (!name.matches("[a-zA-Z ]+")) {
            throw new IllegalArgumentException(
                    "Name can contain only letters and spaces!"
            );
        }

        if (name.trim().length() < 2) {
            throw new IllegalArgumentException(
                    "Name must contain at least 2 characters!"
            );
        }
    }

    // Email Validation
    public static void validateEmail(String email) {

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Email cannot be empty!"
            );
        }

        String emailRegex =
                "^[A-Za-z0-9]+([._+-]?[A-Za-z0-9]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$";

        if (!email.matches(emailRegex)) {
            throw new IllegalArgumentException(
                    "Invalid email address!"
            );
        }
    }

    // Password Validation
    public static void validatePassword(String password) {

        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters!"
            );
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException(
                    "Password must contain an uppercase letter!"
            );
        }

        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException(
                    "Password must contain a lowercase letter!"
            );
        }

        if (!password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException(
                    "Password must contain a number!"
            );
        }

        if (!password.matches(".*[^A-Za-z0-9\\s].*")) {
            throw new IllegalArgumentException(
                    "Password must contain a special character!"
            );
        }
    }
}
