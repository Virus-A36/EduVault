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

public class ForgotPasswordDialog extends JDialog {

    private final AuthController authController = new AuthController();
    private boolean emailVerified = false;
    private String verifiedEmail = null;

    private final ModernTextField emailField = new ModernTextField("Enter your registered email");
    private final ModernPasswordField newPasswordField = new ModernPasswordField("Enter new password");
    private final ModernPasswordField confirmPasswordField = new ModernPasswordField("Confirm new password");

    private final JPanel passwordSection = new JPanel();
    private final ModernButton proceedButton = new ModernButton("Verify Email via OTP", ModernButton.Style.PRIMARY);
    private final JLabel statusLabel = new JLabel("Step 1: Verify your account email with OTP");

    public ForgotPasswordDialog(Frame parent) {
        super(parent, "EduVault - Reset Password", true);

        setSize(460, 540);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_PRIMARY);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        CardPanel card = new CardPanel(16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel titleLabel = new JLabel("Reset Password");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.TEXT_SECONDARY);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        emailField.setMaximumSize(new Dimension(360, 40));
        newPasswordField.setMaximumSize(new Dimension(360, 40));
        confirmPasswordField.setMaximumSize(new Dimension(360, 40));

        // Password Section (initially hidden/disabled)
        passwordSection.setLayout(new BoxLayout(passwordSection, BoxLayout.Y_AXIS));
        passwordSection.setOpaque(false);
        passwordSection.setVisible(false);

        JCheckBox showPassCheck = new JCheckBox("Show Passwords");
        showPassCheck.setFont(UITheme.FONT_SMALL);
        showPassCheck.setForeground(UITheme.TEXT_SECONDARY);
        showPassCheck.setFocusPainted(false);
        showPassCheck.setOpaque(false);
        showPassCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        showPassCheck.addActionListener(e -> {
            char echo = showPassCheck.isSelected() ? (char) 0 : '•';
            newPasswordField.setEchoChar(echo);
            confirmPasswordField.setEchoChar(echo);
        });

        passwordSection.add(createFieldHeader("New Password (Min 8 chars, Aa1@):"));
        passwordSection.add(Box.createVerticalStrut(4));
        passwordSection.add(newPasswordField);
        passwordSection.add(Box.createVerticalStrut(10));
        passwordSection.add(createFieldHeader("Confirm New Password:"));
        passwordSection.add(Box.createVerticalStrut(4));
        passwordSection.add(confirmPasswordField);
        passwordSection.add(Box.createVerticalStrut(6));
        passwordSection.add(showPassCheck);

        proceedButton.setMaximumSize(new Dimension(360, 42));
        proceedButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        ModernButton cancelButton = new ModernButton("Cancel", ModernButton.Style.OUTLINE);
        cancelButton.setMaximumSize(new Dimension(360, 36));
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(18));

        card.add(createFieldHeader("Registered Email Address:"));
        card.add(Box.createVerticalStrut(4));
        card.add(emailField);
        card.add(Box.createVerticalStrut(14));

        card.add(passwordSection);
        card.add(Box.createVerticalStrut(16));

        card.add(proceedButton);
        card.add(Box.createVerticalStrut(8));
        card.add(cancelButton);

        wrapper.add(card);
        add(wrapper, BorderLayout.CENTER);

        proceedButton.addActionListener(e -> handleProceed());
        cancelButton.addActionListener(e -> dispose());
    }

    private void handleProceed() {
        if (!emailVerified) {
            initiateEmailOTP();
        } else {
            finalizePasswordReset();
        }
    }

    private void initiateEmailOTP() {
        String email = emailField.getText().trim();

        try {
            ValidationUtil.validateEmail(email);

            if (!authController.isEmailRegistered(email)) {
                JOptionPane.showMessageDialog(this,
                        "No account found with the email: " + email,
                        "Email Not Found",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Launch OTP dialog
            OTPVerificationDialog otpDialog = new OTPVerificationDialog((Frame) getParent(), email);
            otpDialog.setVisible(true);

            if (otpDialog.isVerified()) {
                emailVerified = true;
                verifiedEmail = email;

                emailField.setEnabled(false);
                statusLabel.setText("✓ Email verified! Step 2: Set your new password");
                statusLabel.setForeground(UITheme.SUCCESS_HOVER);

                passwordSection.setVisible(true);
                proceedButton.setText("Update & Save Password");
                revalidate();
                repaint();
            } else {
                JOptionPane.showMessageDialog(this,
                        "OTP verification was not completed.",
                        "Verification Required",
                        JOptionPane.WARNING_MESSAGE);
            }

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void finalizePasswordReset() {
        char[] newPass = null;
        char[] confirmPass = null;

        try {
            newPass = newPasswordField.getPassword();
            confirmPass = confirmPasswordField.getPassword();

            if (newPass.length == 0) {
                throw new IllegalArgumentException("New password cannot be empty!");
            }

            ValidationUtil.validatePassword(new String(newPass));

            if (!Arrays.equals(newPass, confirmPass)) {
                throw new IllegalArgumentException("New Password and Confirm Password do not match!");
            }

            boolean success = authController.resetPassword(verifiedEmail, newPass);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Password reset successfully!\nYou can now sign in with your new password.",
                        "Password Updated",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update password in database. Please check your connection.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (newPass != null) Arrays.fill(newPass, '\0');
            if (confirmPass != null) Arrays.fill(confirmPass, '\0');
        }
    }

    private JPanel createFieldHeader(String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(UITheme.FONT_SMALL);
        label.setForeground(UITheme.TEXT_PRIMARY);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(360, 20));
        header.add(label);
        return header;
    }
}
