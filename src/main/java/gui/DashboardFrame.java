package gui;

import gui.components.ModernButton;
import gui.theme.UITheme;
import model.User;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {

    private final User user;

    public DashboardFrame(User user) {
        this.user = user;

        setTitle("EduVault - Academic & Personal Suite");
        setSize(1020, 680);
        setMinimumSize(new Dimension(850, 550));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.CARD_BORDER),
                BorderFactory.createEmptyBorder(14, 24, 14, 24)
        ));

        // Brand & User Greeting
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandPanel.setOpaque(false);

        JLabel brandLabel = new JLabel("EduVault");
        brandLabel.setFont(UITheme.FONT_TITLE);
        brandLabel.setForeground(UITheme.PRIMARY);

        JLabel separator = new JLabel("|");
        separator.setFont(UITheme.FONT_HEADER);
        separator.setForeground(UITheme.CARD_BORDER);

        JLabel welcomeLabel = new JLabel("Welcome, " + user.getName());
        welcomeLabel.setFont(UITheme.FONT_HEADER);
        welcomeLabel.setForeground(UITheme.TEXT_PRIMARY);

        brandPanel.add(brandLabel);
        brandPanel.add(separator);
        brandPanel.add(welcomeLabel);

        // Logout Button
        ModernButton logoutButton = new ModernButton("Sign Out", ModernButton.Style.DANGER, 8);
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to sign out?",
                    "Sign Out Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });

        topPanel.add(brandPanel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);

        // Modern Tabbed Pane
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.FONT_BUTTON);
        tabs.setBackground(Color.WHITE);

        tabs.addTab("Password Vault", new VaultPanel(user));
        tabs.addTab("Task Manager", new TaskPanel(user));
        tabs.addTab("CGPA Calculator", new CGPACalculatorPanel());
        tabs.addTab("Blood Donors", new DonorPanel());

        add(topPanel, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }
}
