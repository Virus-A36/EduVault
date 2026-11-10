package gui;

import controller.AuthController;
import gui.components.CardPanel;
import gui.components.ModernButton;
import gui.components.ModernPasswordField;
import gui.components.ModernTextField;
import gui.theme.UITheme;
import util.ValidationUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class RegisterFrame extends JFrame {

    private final ModernTextField nameField = new ModernTextField("Enter your full name");
    private final ModernTextField emailField = new ModernTextField("Enter your email address");
    private final ModernPasswordField passwordField = new ModernPasswordField("Enter secure password");
    private final ModernPasswordField confirmPasswordField = new ModernPasswordField("Re-enter password");

    private final AuthController authController = new AuthController();

    public RegisterFrame() {
        setTitle("EduVault - Create Account");
        setSize(480, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        CardPanel card = new CardPanel(16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(26, 32, 26, 32));

        // Header
        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Join EduVault with OTP-verified security", SwingConstants.CENTER);
        subtitleLabel.setFont(UITheme.FONT_SMALL);
        subtitleLabel.setForeground(UITheme.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitleLabel);
        card.add(Box.createVerticalStrut(18));

        // Input Fields Helper
        addField(card, "Full Name", nameField);
        card.add(Box.createVerticalStrut(10));

        addField(card, "Email Address", emailField);
        card.add(Box.createVerticalStrut(10));

        addField(card, "Password (Min 8 chars, uppercase, lowercase, number, symbol)", passwordField);
        card.add(Box.createVerticalStrut(10));

        addField(card, "Confirm Password", confirmPasswordField);
        card.add(Box.createVerticalStrut(20));

        ModernButton registerButton = new ModernButton("Verify & Register", ModernButton.Style.PRIMARY);
        registerButton.setMaximumSize(new Dimension(360, 42));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        ModernButton loginButton = new ModernButton("Already have an account? Sign In", ModernButton.Style.OUTLINE);
        loginButton.setMaximumSize(new Dimension(360, 38));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(registerButton);
        card.add(Box.createVerticalStrut(10));
        card.add(loginButton);

        root.add(card);
        add(root, BorderLayout.CENTER);

        registerButton.addActionListener(e -> initiateRegistration());
        loginButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }

    private void addField(JPanel parent, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(UITheme.FONT_SMALL);
        label.setForeground(UITheme.TEXT_PRIMARY);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(360, 18));
        header.add(label);

        field.setMaximumSize(new Dimension(360, 38));

        parent.add(header);
        parent.add(Box.createVerticalStrut(5));
        parent.add(field);
    }

    private void initiateRegistration() {
        char[] password = null;
        char[] confirmPassword = null;

        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            password = passwordField.getPassword();
            confirmPassword = confirmPasswordField.getPassword();

            // Validate Input
            ValidationUtil.validateName(name);
            ValidationUtil.validateEmail(email);

            if (password.length == 0) {
                throw new IllegalArgumentException("Password cannot be empty!");
            }

            ValidationUtil.validatePassword(new String(password));

            if (!Arrays.equals(password, confirmPassword)) {
                throw new IllegalArgumentException("Password and Confirm Password do not match!");
            }

            if (authController.isEmailRegistered(email)) {
                throw new IllegalArgumentException("This email is already registered!");
            }

            // Prompt OTP Verification Dialog
            OTPVerificationDialog otpDialog = new OTPVerificationDialog(this, email);
            otpDialog.setVisible(true);

            if (!otpDialog.isVerified()) {
                JOptionPane.showMessageDialog(this,
                        "Registration was not completed because email was not verified.",
                        "Verification Incomplete",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Register user in database
            boolean success = authController.register(name, email, password);

            if (success) {
                JOptionPane.showMessageDialog(
                        this,
                        "Account verified & created successfully! You can now log in.",
                        "Welcome to EduVault",
                        JOptionPane.INFORMATION_MESSAGE
                );

                new LoginFrame().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Registration failed in database. Please check your connection and try again.",
                        "Registration Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "An unexpected error occurred: " + ex.getMessage(),
                    "System Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } finally {
            if (password != null) Arrays.fill(password, '\0');
            if (confirmPassword != null) Arrays.fill(confirmPassword, '\0');
        }
    }
}
