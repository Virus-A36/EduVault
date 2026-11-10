package gui;

import controller.VaultController;
import gui.components.CardPanel;
import gui.components.ModernButton;
import gui.components.ModernPasswordField;
import gui.components.ModernTableHeaderRenderer;
import gui.components.ModernTextField;
import gui.theme.UITheme;
import model.Credential;
import model.User;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.security.SecureRandom;
import java.util.List;

public class VaultPanel extends JPanel {

    private final User user;
    private final VaultController controller = new VaultController();

    private final DefaultTableModel tableModel =
            new DefaultTableModel(new String[]{"ID", "Website / Service", "Username / Email"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable table = new JTable(tableModel);
    private final TableRowSorter<DefaultTableModel> rowSorter = new TableRowSorter<>(tableModel);
    private final JLabel countLabel = new JLabel("0 credentials secured");

    public VaultPanel(User user) {
        this.user = user;

        setLayout(new BorderLayout(14, 14));
        setBackground(UITheme.BG_PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        setupTable();

        // Top Toolbar
        JPanel topBar = new JPanel(new BorderLayout(12, 10));
        topBar.setOpaque(false);

        // Action Buttons
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonBar.setOpaque(false);

        ModernButton addButton = new ModernButton("+ Add Credential", ModernButton.Style.PRIMARY);
        ModernButton viewButton = new ModernButton("View Password", ModernButton.Style.SUCCESS);
        ModernButton deleteButton = new ModernButton("Delete", ModernButton.Style.DANGER);
        ModernButton refreshButton = new ModernButton("Refresh", ModernButton.Style.OUTLINE);

        buttonBar.add(addButton);
        buttonBar.add(viewButton);
        buttonBar.add(deleteButton);
        buttonBar.add(refreshButton);

        // Search Bar on Right
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        searchBar.setOpaque(false);
        ModernTextField searchField = new ModernTextField("Search credentials...", 8);
        searchField.setPreferredSize(new Dimension(220, 36));
        searchBar.add(searchField);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filter(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filter(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = searchField.getText().trim();
                if (text.isEmpty() || text.equals("Search credentials...")) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
                }
            }
        });

        topBar.add(buttonBar, BorderLayout.WEST);
        topBar.add(searchBar, BorderLayout.EAST);

        // Table Panel inside styled dark scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.CARD_BORDER, 1));
        scrollPane.getViewport().setBackground(UITheme.TABLE_BG);
        scrollPane.setBackground(UITheme.TABLE_BG);

        // Bottom Info Bar
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setOpaque(false);
        bottomBar.setBorder(BorderFactory.createEmptyBorder(6, 4, 0, 4));

        countLabel.setFont(UITheme.FONT_SMALL);
        countLabel.setForeground(UITheme.TEXT_SECONDARY);

        JLabel encryptionBadge = new JLabel("AES-256-GCM Encrypted | User-Isolated Storage");
        encryptionBadge.setFont(UITheme.FONT_SMALL);
        encryptionBadge.setForeground(UITheme.PRIMARY);

        bottomBar.add(countLabel, BorderLayout.WEST);
        bottomBar.add(encryptionBadge, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);

        // Event listeners
        addButton.addActionListener(e -> showAddCredentialDialog());
        viewButton.addActionListener(e -> showViewPasswordDialog());
        deleteButton.addActionListener(e -> deleteCredential());
        refreshButton.addActionListener(e -> loadData());

        loadData();
    }

    private void setupTable() {
        table.setRowHeight(38);
        table.setFont(UITheme.FONT_BODY);
        table.setBackground(UITheme.TABLE_BG);
        table.setForeground(UITheme.TEXT_PRIMARY);
        table.getTableHeader().setDefaultRenderer(new ModernTableHeaderRenderer());
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(UITheme.PRIMARY_BG);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(UITheme.CARD_BORDER);
        table.setShowVerticalLines(false);
        table.setRowSorter(rowSorter);

        // ID column compact
        table.getColumnModel().getColumn(0).setMaxWidth(80);
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Credential> credentials = controller.getCredentials(user.getId());
        for (Credential c : credentials) {
            tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getWebsite(),
                    c.getUsername()
            });
        }
        countLabel.setText(credentials.size() + " credentials secured in vault");
    }

    // ==========================================
    // MODERN ADD CREDENTIAL DIALOG
    // ==========================================
    private void showAddCredentialDialog() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentWindow, "Add Encrypted Credential", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(440, 460);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.getContentPane().setBackground(UITheme.BG_PRIMARY);
        dialog.setLayout(new BorderLayout());

        JPanel root = new JPanel(new GridBagLayout());
        root.setOpaque(false);
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        CardPanel card = new CardPanel(14);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));

        JLabel titleLabel = new JLabel("New Credential");
        titleLabel.setFont(UITheme.FONT_HEADER);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Encrypted with AES-256 before saving to database");
        subtitleLabel.setFont(UITheme.FONT_SMALL);
        subtitleLabel.setForeground(UITheme.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        ModernTextField websiteField = new ModernTextField("e.g. github.com, google.com");
        ModernTextField usernameField = new ModernTextField("e.g. farid@bup.edu.bd");
        ModernPasswordField passwordField = new ModernPasswordField("Enter secret password");

        websiteField.setMaximumSize(new Dimension(360, 38));
        usernameField.setMaximumSize(new Dimension(360, 38));
        passwordField.setMaximumSize(new Dimension(360, 38));

        // Password Tools (Generate & Show Password)
        JPanel passTools = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        passTools.setOpaque(false);
        passTools.setMaximumSize(new Dimension(360, 28));

        JCheckBox showPassCheck = new JCheckBox("Show Password");
        showPassCheck.setFont(UITheme.FONT_SMALL);
        showPassCheck.setForeground(UITheme.TEXT_SECONDARY);
        showPassCheck.setOpaque(false);
        showPassCheck.setFocusPainted(false);
        showPassCheck.addActionListener(e -> {
            passwordField.setEchoChar(showPassCheck.isSelected() ? (char) 0 : '•');
        });

        JButton genPassBtn = new JButton("Generate Strong");
        genPassBtn.setFont(new Font(UITheme.FONT_FAMILY, Font.PLAIN, 11));
        genPassBtn.setForeground(UITheme.PRIMARY);
        genPassBtn.setBackground(UITheme.CARD_BG);
        genPassBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.CARD_BORDER, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        genPassBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        genPassBtn.addActionListener(e -> {
            String gen = generateRandomPassword(16);
            passwordField.setText(gen);
            showPassCheck.setSelected(true);
            passwordField.setEchoChar((char) 0);
        });

        passTools.add(genPassBtn);
        passTools.add(showPassCheck);

        // Buttons
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionRow.setOpaque(false);
        actionRow.setMaximumSize(new Dimension(360, 42));

        ModernButton cancelBtn = new ModernButton("Cancel", ModernButton.Style.OUTLINE);
        ModernButton saveBtn = new ModernButton("Save & Encrypt", ModernButton.Style.PRIMARY);

        actionRow.add(cancelBtn);
        actionRow.add(saveBtn);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitleLabel);
        card.add(Box.createVerticalStrut(16));

        card.add(createFieldLabel("Website or Service:"));
        card.add(Box.createVerticalStrut(4));
        card.add(websiteField);
        card.add(Box.createVerticalStrut(10));

        card.add(createFieldLabel("Username or Email:"));
        card.add(Box.createVerticalStrut(4));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(10));

        card.add(createFieldLabel("Secret Password:"));
        card.add(Box.createVerticalStrut(4));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(4));
        card.add(passTools);

        card.add(Box.createVerticalStrut(18));
        card.add(actionRow);

        root.add(card);
        dialog.add(root, BorderLayout.CENTER);

        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {
            String website = websiteField.getText().trim();
            String username = usernameField.getText().trim();
            char[] passChars = passwordField.getPassword();
            String secret = new String(passChars).trim();
            java.util.Arrays.fill(passChars, '\0');

            if (website.isEmpty() || username.isEmpty() || secret.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please fill in all fields (Website, Username, and Password).",
                        "Input Required",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean success = controller.addCredential(user.getId(), website, username, secret);
            if (success) {
                dialog.dispose();
                JOptionPane.showMessageDialog(this,
                        "Credential encrypted and stored securely in vault!",
                        "Saved Successfully",
                        JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                String error = controller.getLastError();
                String msg = "Unable to save credential.";
                if (error != null) msg += "\nReason: " + error;
                JOptionPane.showMessageDialog(dialog, msg, "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    // ==========================================
    // MODERN VIEW PASSWORD DIALOG
    // ==========================================
    private void showViewPasswordDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a credential from the table first.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        long id = Long.parseLong(tableModel.getValueAt(modelRow, 0).toString());
        String website = tableModel.getValueAt(modelRow, 1).toString();
        String username = tableModel.getValueAt(modelRow, 2).toString();

        List<Credential> credentials = controller.getCredentials(user.getId());
        Credential target = null;
        for (Credential c : credentials) {
            if (c.getId() == id) {
                target = c;
                break;
            }
        }

        if (target == null) {
            JOptionPane.showMessageDialog(this, "Credential record not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String decrypted = controller.decryptPassword(target.getSecret());
        if (decrypted == null) {
            JOptionPane.showMessageDialog(this,
                    "Decryption failed. Please check your encryption key.",
                    "Decryption Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentWindow, "Decrypted Credential - " + website, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(440, 360);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.getContentPane().setBackground(UITheme.BG_PRIMARY);
        dialog.setLayout(new BorderLayout());

        JPanel root = new JPanel(new GridBagLayout());
        root.setOpaque(false);
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        CardPanel card = new CardPanel(14);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));

        JLabel titleLabel = new JLabel("Decrypted Password");
        titleLabel.setFont(UITheme.FONT_HEADER);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subLabel = new JLabel(website + " (" + username + ")");
        subLabel.setFont(UITheme.FONT_SMALL);
        subLabel.setForeground(UITheme.TEXT_SECONDARY);

        JTextField passDisplayField = new JTextField(decrypted);
        passDisplayField.setEditable(false);
        passDisplayField.setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, 18));
        passDisplayField.setForeground(UITheme.PRIMARY);
        passDisplayField.setBackground(UITheme.INPUT_BG);
        passDisplayField.setHorizontalAlignment(JTextField.CENTER);
        passDisplayField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.CARD_BORDER, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        passDisplayField.setMaximumSize(new Dimension(360, 44));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonRow.setOpaque(false);
        buttonRow.setMaximumSize(new Dimension(360, 40));

        ModernButton copyBtn = new ModernButton("Copy Password", ModernButton.Style.PRIMARY);
        ModernButton closeBtn = new ModernButton("Close", ModernButton.Style.OUTLINE);

        copyBtn.addActionListener(e -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(decrypted), null);
            copyBtn.setText("Copied!");
            Timer t = new Timer(2000, evt -> copyBtn.setText("Copy Password"));
            t.setRepeats(false);
            t.start();
        });

        closeBtn.addActionListener(e -> dialog.dispose());

        buttonRow.add(copyBtn);
        buttonRow.add(closeBtn);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(subLabel);
        card.add(Box.createVerticalStrut(20));
        card.add(passDisplayField);
        card.add(Box.createVerticalStrut(22));
        card.add(buttonRow);

        root.add(card);
        dialog.add(root, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void deleteCredential() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select a credential from the table to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        long id = Long.parseLong(tableModel.getValueAt(modelRow, 0).toString());
        String website = tableModel.getValueAt(modelRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to permanently delete the credential for:\n" + website + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = controller.deleteCredential(id, user.getId());
            if (ok) {
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete credential.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.FONT_SMALL);
        label.setForeground(UITheme.TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private String generateRandomPassword(int length) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
