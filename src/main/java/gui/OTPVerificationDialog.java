package gui;

import gui.components.CardPanel;
import gui.components.ModernButton;
import gui.components.ModernTextField;
import gui.theme.UITheme;
import service.OTPService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class OTPVerificationDialog extends JDialog {

    private final String email;
    private final OTPService otpService = OTPService.getInstance();
    private boolean verified = false;

    private final ModernTextField otpField = new ModernTextField("Enter 6-digit code", 10);
    private final JLabel timerLabel = new JLabel("Code expires in 05:00", SwingConstants.CENTER);
    private final JLabel otpDisplayLabel = new JLabel();
    private int remainingSeconds = 300;
    private Timer countdownTimer;

    public OTPVerificationDialog(Frame parent, String email) {
        super(parent, "Verify Email - EduVault", true);
        this.email = email;

        setSize(440, 430);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_PRIMARY);
        setLayout(new BorderLayout());

        // Generate initial OTP
        String initialOtp = otpService.generateOTP(email);

        initUI(initialOtp);
        startTimer();
    }

    private void initUI(String initialOtp) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        CardPanel card = new CardPanel(16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel titleLabel = new JLabel("Verification Required");
        titleLabel.setFont(UITheme.FONT_HEADER);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("<html><center>We sent a 6-digit OTP code to<br><b>" + email + "</b></center></html>");
        subtitleLabel.setFont(UITheme.FONT_SMALL);
        subtitleLabel.setForeground(UITheme.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Green Screen Banner showing the OTP code directly
        JPanel otpNoticePanel = new JPanel();
        otpNoticePanel.setBackground(UITheme.SUCCESS_BG);
        otpNoticePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.SUCCESS, 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        otpNoticePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        otpDisplayLabel.setText("Your New OTP: " + initialOtp);
        otpDisplayLabel.setFont(UITheme.FONT_BODY_BOLD);
        otpDisplayLabel.setForeground(UITheme.SUCCESS);
        otpNoticePanel.add(otpDisplayLabel);

        otpField.setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, 22));
        otpField.setHorizontalAlignment(JTextField.CENTER);
        otpField.setMaximumSize(new Dimension(240, 45));
        otpField.setAlignmentX(Component.CENTER_ALIGNMENT);

        timerLabel.setFont(UITheme.FONT_SMALL);
        timerLabel.setForeground(UITheme.TEXT_MUTED);
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ModernButton verifyButton = new ModernButton("Verify & Complete", ModernButton.Style.PRIMARY);
        verifyButton.setMaximumSize(new Dimension(240, 42));
        verifyButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        ModernButton resendButton = new ModernButton("Resend Code", ModernButton.Style.OUTLINE);
        resendButton.setMaximumSize(new Dimension(240, 36));
        resendButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(subtitleLabel);
        card.add(Box.createVerticalStrut(14));
        card.add(otpNoticePanel);
        card.add(Box.createVerticalStrut(16));
        card.add(otpField);
        card.add(Box.createVerticalStrut(10));
        card.add(timerLabel);
        card.add(Box.createVerticalStrut(18));
        card.add(verifyButton);
        card.add(Box.createVerticalStrut(8));
        card.add(resendButton);

        wrapper.add(card);
        add(wrapper, BorderLayout.CENTER);

        // Event listeners
        verifyButton.addActionListener(e -> verify());
        otpField.addActionListener(e -> verify());

        resendButton.addActionListener(e -> {
            String newOtp = otpService.generateOTP(email);
            otpDisplayLabel.setText("Your New OTP: " + newOtp);
            remainingSeconds = 300;
            updateTimerDisplay();
            JOptionPane.showMessageDialog(this,
                    "A new 6-digit OTP code has been generated:\n" + newOtp,
                    "OTP Resent",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (countdownTimer != null) {
                    countdownTimer.stop();
                }
            }
        });
    }

    private void startTimer() {
        countdownTimer = new Timer(1000, e -> {
            remainingSeconds--;
            if (remainingSeconds <= 0) {
                countdownTimer.stop();
                timerLabel.setText("OTP has expired. Please click Resend.");
                timerLabel.setForeground(UITheme.DANGER);
            } else {
                updateTimerDisplay();
            }
        });
        countdownTimer.start();
    }

    private void updateTimerDisplay() {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        timerLabel.setText(String.format("Code expires in %02d:%02d", minutes, seconds));
        timerLabel.setForeground(UITheme.TEXT_MUTED);
    }

    private void verify() {
        String entered = otpField.getText().trim();
        if (entered.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter the 6-digit OTP code.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (otpService.verifyOTP(email, entered)) {
            if (countdownTimer != null) {
                countdownTimer.stop();
            }
            verified = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid or expired OTP code! Please try again.",
                    "Verification Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isVerified() {
        return verified;
    }
}
