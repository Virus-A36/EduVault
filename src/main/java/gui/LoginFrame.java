package gui;

import controller.AuthController;
import gui.components.CardPanel;
import gui.components.ModernButton;
import gui.components.ModernPasswordField;
import gui.components.ModernTextField;
import gui.theme.UITheme;
import model.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final ModernTextField emailField = new ModernTextField("Enter your email");
    private final ModernPasswordField passwordField = new ModernPasswordField("Enter your password");

    private final AuthController authController = new AuthController();

    public LoginFrame() {
        setTitle("EduVault - Sign In");
        setSize(460, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        CardPanel card = new CardPanel(16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(30, 32, 30, 32));

        // Brand Icon / Header
        JLabel logoLabel = new JLabel("EduVault", SwingConstants.CENTER);
        logoLabel.setFont(UITheme.FONT_TITLE);
        logoLabel.setForeground(UITheme.PRIMARY);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Sign in to your academic vault", SwingConstants.CENTER);
        subtitleLabel.setFont(UITheme.FONT_SMALL);
        subtitleLabel.setForeground(UITheme.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Form Fields
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(UITheme.FONT_SMALL);
        emailLabel.setForeground(UITheme.TEXT_PRIMARY);
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(UITheme.FONT_SMALL);
        passLabel.setForeground(UITheme.TEXT_PRIMARY);
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        emailField.setMaximumSize(new Dimension(340, 42));
        passwordField.setMaximumSize(new Dimension(340, 42));

        ModernButton loginButton = new ModernButton("Sign In", ModernButton.Style.PRIMARY);
        loginButton.setMaximumSize(new Dimension(340, 42));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        ModernButton registerButton = new ModernButton("Create New Account", ModernButton.Style.OUTLINE);
        registerButton.setMaximumSize(new Dimension(340, 38));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to card
        card.add(logoLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitleLabel);
        card.add(Box.createVerticalStrut(24));

        JPanel emailHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        emailHeader.setOpaque(false);
        emailHeader.setMaximumSize(new Dimension(340, 20));
        emailHeader.add(emailLabel);
        card.add(emailHeader);
        card.add(Box.createVerticalStrut(6));
        card.add(emailField);

        card.add(Box.createVerticalStrut(14));

        JPanel passHeader = new JPanel(new BorderLayout());
        passHeader.setOpaque(false);
        passHeader.setMaximumSize(new Dimension(340, 20));
        passHeader.add(passLabel, BorderLayout.WEST);

        JButton forgotBtn = new JButton("Forgot password?");
        forgotBtn.setFont(UITheme.FONT_SMALL);
        forgotBtn.setForeground(UITheme.PRIMARY);
        forgotBtn.setBorderPainted(false);
        forgotBtn.setContentAreaFilled(false);
        forgotBtn.setFocusPainted(false);
        forgotBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotBtn.addActionListener(e -> new ForgotPasswordDialog(this).setVisible(true));
        passHeader.add(forgotBtn, BorderLayout.EAST);
        card.add(passHeader);
        card.add(Box.createVerticalStrut(6));
        card.add(passwordField);

        card.add(Box.createVerticalStrut(24));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(10));
        card.add(registerButton);

        root.add(card);
        add(root, BorderLayout.CENTER);

        // Actions
        loginButton.addActionListener(e -> login());
        emailField.addActionListener(e -> login());
        passwordField.addActionListener(e -> login());

        registerButton.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });
    }

    private void login() {
        char[] password = passwordField.getPassword();
        String email = emailField.getText().trim();

        if (email.isEmpty() || password.length == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please fill in both email and password.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        User user = authController.login(email, password);
        java.util.Arrays.fill(password, '\0');

        if (user != null) {
            new DashboardFrame(user).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid email or password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
