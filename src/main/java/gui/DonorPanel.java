package gui;

import controller.DonorController;
import gui.components.ModernButton;
import gui.components.ModernTableHeaderRenderer;
import gui.theme.UITheme;
import model.Donor;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class DonorPanel extends JPanel {

    private final DonorController controller = new DonorController();

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{
                    "ID", "Name", "Blood Group", "Phone", "Location",
                    "Last Donated", "Availability", "Cooldown / Details"
            },
            0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);

    public DonorPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(UITheme.BG_PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        setupTable();

        // Control Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        ModernButton registerButton = new ModernButton("Register as Donor", ModernButton.Style.PRIMARY);
        ModernButton updateDateButton = new ModernButton("Update Donation Date", ModernButton.Style.SUCCESS);
        ModernButton searchButton = new ModernButton("Search by Blood Group", ModernButton.Style.SECONDARY);
        ModernButton refreshButton = new ModernButton("Show All Donors", ModernButton.Style.OUTLINE);

        toolbar.add(registerButton);
        toolbar.add(updateDateButton);
        toolbar.add(searchButton);
        toolbar.add(refreshButton);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.CARD_BORDER, 1));
        scrollPane.getViewport().setBackground(UITheme.TABLE_BG);
        scrollPane.setBackground(UITheme.TABLE_BG);

        add(toolbar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Action Handlers
        registerButton.addActionListener(e -> registerDonor());
        updateDateButton.addActionListener(e -> updateDonationDate());
        searchButton.addActionListener(e -> searchDonors());
        refreshButton.addActionListener(e -> loadAllDonors());

        // Initial Load
        loadAllDonors();
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

        // Custom Cell Renderer for Availability Status
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                String status = value != null ? value.toString() : "";

                if (isSelected) {
                    label.setBackground(table.getSelectionBackground());
                    label.setForeground(table.getSelectionForeground());
                } else {
                    if ("Available".equalsIgnoreCase(status)) {
                        label.setBackground(UITheme.SUCCESS_BG);
                        label.setForeground(UITheme.SUCCESS_HOVER);
                    } else {
                        label.setBackground(UITheme.DANGER_BG);
                        label.setForeground(UITheme.DANGER_HOVER);
                    }
                    label.setOpaque(true);
                }
                label.setFont(UITheme.FONT_BODY_BOLD);
                return label;
            }
        });
    }

    private void loadAllDonors() {
        loadDonorsList(controller.searchDonors("ALL"));
    }

    private void loadDonorsList(List<Donor> donors) {
        model.setRowCount(0);
        for (Donor donor : donors) {
            String lastDonationStr = donor.getLastDonationDate() != null
                    ? donor.getLastDonationDate().toString()
                    : "Never Donated";

            model.addRow(new Object[]{
                    donor.getId(),
                    donor.getName(),
                    donor.getBloodGroup(),
                    donor.getPhone(),
                    donor.getLocation(),
                    lastDonationStr,
                    donor.getAvailabilityStatus(),
                    donor.getStatusDetails()
            });
        }
    }

    private void registerDonor() {
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField locationField = new JTextField();
        JTextField lastDonationField = new JTextField(); // Format YYYY-MM-DD or empty
        lastDonationField.setToolTipText("Leave empty if never donated, or format YYYY-MM-DD");

        JComboBox<String> bloodGroup = new JComboBox<>(
                new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"}
        );

        JCheckBox consent = new JCheckBox(
                "I agree to share my contact information with people searching for donors.",
                true
        );

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 8, 8));
        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Blood Group:"));
        formPanel.add(bloodGroup);

        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Location:"));
        formPanel.add(locationField);

        formPanel.add(new JLabel("Last Donation Date (YYYY-MM-DD):"));
        formPanel.add(lastDonationField);

        formPanel.add(new JLabel("Consent:"));
        formPanel.add(consent);

        int result = JOptionPane.showConfirmDialog(
                this,
                formPanel,
                "Register New Blood Donor",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            LocalDate donationDate = null;
            String dateText = lastDonationField.getText().trim();
            if (!dateText.isEmpty()) {
                try {
                    donationDate = LocalDate.parse(dateText);
                    if (donationDate.isAfter(LocalDate.now())) {
                        JOptionPane.showMessageDialog(this,
                                "Last donation date cannot be in the future!",
                                "Invalid Date",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid date format! Please use YYYY-MM-DD (e.g., 2026-10-15).",
                            "Date Format Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            boolean success = controller.registerDonor(
                    nameField.getText().trim(),
                    bloodGroup.getSelectedItem().toString(),
                    phoneField.getText().trim(),
                    locationField.getText().trim(),
                    consent.isSelected(),
                    donationDate
            );

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Donor registered successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadAllDonors();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Registration failed. Please fill all required fields and grant consent.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateDonationDate() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a donor from the table first.",
                    "No Donor Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        long donorId = (long) model.getValueAt(selectedRow, 0);
        String donorName = (String) model.getValueAt(selectedRow, 1);

        JPanel panel = new JPanel(new GridLayout(3, 1, 8, 8));
        JLabel prompt = new JLabel("Update donation date for: " + donorName);
        prompt.setFont(UITheme.FONT_BODY_BOLD);

        JTextField dateField = new JTextField(LocalDate.now().toString());
        JLabel hint = new JLabel("Format: YYYY-MM-DD (Defaults to today)");
        hint.setFont(UITheme.FONT_SMALL);
        hint.setForeground(UITheme.TEXT_SECONDARY);

        panel.add(prompt);
        panel.add(dateField);
        panel.add(hint);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Update Last Blood Donation Date",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String dateText = dateField.getText().trim();
            try {
                LocalDate date = LocalDate.parse(dateText);
                if (date.isAfter(LocalDate.now())) {
                    JOptionPane.showMessageDialog(this,
                            "Donation date cannot be in the future!",
                            "Invalid Date",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                boolean updated = controller.updateDonationDate(donorId, date);
                if (updated) {
                    JOptionPane.showMessageDialog(this,
                            "Donation date updated! Cooldown is active for 3 months from donation.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadAllDonors();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to update database record.",
                            "Update Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid date format! Please use YYYY-MM-DD (e.g. " + LocalDate.now() + ")",
                        "Format Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchDonors() {
        String[] groups = {"ALL", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Select Blood Group to Filter:",
                "Search Donors",
                JOptionPane.QUESTION_MESSAGE,
                null,
                groups,
                groups[0]
        );

        if (selected == null) {
            return;
        }

        List<Donor> donors = controller.searchDonors(selected);
        loadDonorsList(donors);

        if (donors.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No registered donors found for group: " + selected,
                    "Search Results",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
