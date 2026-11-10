package gui;

import controller.TaskController;
import gui.components.ModernButton;
import gui.components.ModernTableHeaderRenderer;
import gui.theme.UITheme;
import model.Task;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class TaskPanel extends JPanel {

    private final User user;
    private final TaskController controller = new TaskController();

    private final DefaultTableModel model =
            new DefaultTableModel(new String[]{"ID", "Title", "Description", "Due Date", "Status"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable table = new JTable(model);

    public TaskPanel(User user) {
        this.user = user;

        setLayout(new BorderLayout(12, 12));
        setBackground(UITheme.BG_PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        setupTable();

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        ModernButton addButton = new ModernButton("Add Task", ModernButton.Style.PRIMARY);
        ModernButton completeButton = new ModernButton("Mark Completed", ModernButton.Style.SUCCESS);
        ModernButton deleteButton = new ModernButton("Delete", ModernButton.Style.DANGER);
        ModernButton refreshButton = new ModernButton("Refresh", ModernButton.Style.OUTLINE);

        toolbar.add(addButton);
        toolbar.add(completeButton);
        toolbar.add(deleteButton);
        toolbar.add(refreshButton);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.CARD_BORDER, 1));
        scrollPane.getViewport().setBackground(UITheme.TABLE_BG);
        scrollPane.setBackground(UITheme.TABLE_BG);

        add(toolbar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        addButton.addActionListener(e -> addTask());
        completeButton.addActionListener(e -> completeTask());
        deleteButton.addActionListener(e -> deleteTask());
        refreshButton.addActionListener(e -> loadTasks());

        loadTasks();
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

        // Status Renderer
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
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
                    if ("Completed".equalsIgnoreCase(status)) {
                        label.setBackground(UITheme.SUCCESS_BG);
                        label.setForeground(UITheme.SUCCESS_HOVER);
                    } else {
                        label.setBackground(UITheme.WARNING_BG);
                        label.setForeground(UITheme.WARNING);
                    }
                    label.setOpaque(true);
                }
                label.setFont(UITheme.FONT_BODY_BOLD);
                return label;
            }
        });
    }

    private void loadTasks() {
        model.setRowCount(0);
        List<Task> tasks = controller.getTasks(user.getId());
        for (Task task : tasks) {
            model.addRow(new Object[]{
                    task.getId(),
                    task.getTitle(),
                    task.getDescription(),
                    task.getDueDate() != null ? task.getDueDate().toString() : "No Due Date",
                    task.getStatus()
            });
        }
    }

    private void addTask() {
        JTextField title = new JTextField();
        JTextField description = new JTextField();
        JTextField dueDate = new JTextField(LocalDate.now().plusDays(3).toString());

        JPanel panel = new JPanel(new GridLayout(3, 2, 8, 8));
        panel.add(new JLabel("Task Title:"));
        panel.add(title);
        panel.add(new JLabel("Description:"));
        panel.add(description);
        panel.add(new JLabel("Due Date (YYYY-MM-DD):"));
        panel.add(dueDate);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add New Task",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            if (title.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                LocalDate date = dueDate.getText().trim().isEmpty()
                        ? null
                        : LocalDate.parse(dueDate.getText().trim());

                boolean success = controller.addTask(
                        user.getId(),
                        title.getText().trim(),
                        description.getText().trim(),
                        date
                );

                if (success) {
                    JOptionPane.showMessageDialog(this, "Task created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadTasks();
                } else {
                    JOptionPane.showMessageDialog(this, "Unable to add task.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.", "Date Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void completeTask() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a task first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        long id = Long.parseLong(model.getValueAt(row, 0).toString());
        controller.updateStatus(id, user.getId(), "Completed");
        loadTasks();
    }

    private void deleteTask() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a task first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        long id = Long.parseLong(model.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete selected task?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteTask(id, user.getId());
            loadTasks();
        }
    }
}
