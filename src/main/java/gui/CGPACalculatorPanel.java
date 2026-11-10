package gui;

import gui.components.ModernButton;
import gui.components.ModernTableHeaderRenderer;
import gui.components.ModernTextField;
import gui.theme.UITheme;
import model.Course;
import service.CGPAService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CGPACalculatorPanel extends JPanel {

    private final DefaultTableModel model =
            new DefaultTableModel(new String[]{"Course Name", "Credit Hours", "Grade Point (0.0 - 4.0)"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable table = new JTable(model);
    private final CGPAService service = new CGPAService();
    private final JLabel cgpaResultLabel = new JLabel("Cumulative GPA: --", SwingConstants.RIGHT);

    public CGPACalculatorPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(UITheme.BG_PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        setupTable();

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonBar.setOpaque(false);

        ModernButton addButton = new ModernButton("Add Course", ModernButton.Style.PRIMARY);
        ModernButton removeButton = new ModernButton("Remove Course", ModernButton.Style.DANGER);
        ModernButton calculateButton = new ModernButton("Calculate CGPA", ModernButton.Style.SUCCESS);
        ModernButton clearButton = new ModernButton("Clear All", ModernButton.Style.OUTLINE);

        buttonBar.add(addButton);
        buttonBar.add(removeButton);
        buttonBar.add(calculateButton);
        buttonBar.add(clearButton);

        cgpaResultLabel.setFont(UITheme.FONT_HEADER);
        cgpaResultLabel.setForeground(UITheme.PRIMARY);

        toolbar.add(buttonBar, BorderLayout.WEST);
        toolbar.add(cgpaResultLabel, BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.CARD_BORDER, 1));
        scrollPane.getViewport().setBackground(UITheme.TABLE_BG);
        scrollPane.setBackground(UITheme.TABLE_BG);

        add(toolbar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        addButton.addActionListener(e -> addCourse());
        removeButton.addActionListener(e -> removeCourse());
        calculateButton.addActionListener(e -> calculate());
        clearButton.addActionListener(e -> {
            model.setRowCount(0);
            cgpaResultLabel.setText("Cumulative GPA: --");
        });
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
    }

    private void addCourse() {
        ModernTextField name = new ModernTextField("Course Name", 8);
        ModernTextField credit = new ModernTextField("Credit", 8);
        credit.setText("3.0");
        ModernTextField grade = new ModernTextField("Grade", 8);
        grade.setText("4.0");

        JPanel fields = new JPanel(new GridLayout(3, 2, 8, 8));
        fields.setOpaque(false);
        JLabel nameLbl = new JLabel("Course Name:");
        nameLbl.setForeground(UITheme.TEXT_PRIMARY);
        fields.add(nameLbl);
        fields.add(name);

        JLabel creditLbl = new JLabel("Credit (e.g., 1.5, 3.0):");
        creditLbl.setForeground(UITheme.TEXT_PRIMARY);
        fields.add(creditLbl);
        fields.add(credit);

        JLabel gradeLbl = new JLabel("Grade Point (0.00 - 4.00):");
        gradeLbl.setForeground(UITheme.TEXT_PRIMARY);
        fields.add(gradeLbl);
        fields.add(grade);

        int result = JOptionPane.showConfirmDialog(
                this,
                fields,
                "Add Course Grade",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                double c = Double.parseDouble(credit.getText().trim());
                double g = Double.parseDouble(grade.getText().trim());

                if (name.getText().trim().isEmpty() || c <= 0 || c > 10 || g < 0 || g > 4) {
                    throw new IllegalArgumentException();
                }

                model.addRow(new Object[]{name.getText().trim(), c, g});
                calculate(); // Auto-recalculate

            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid input! Credit must be > 0 and Grade Point must be between 0.00 and 4.00.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void removeCourse() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            model.removeRow(row);
            if (model.getRowCount() > 0) {
                calculate();
            } else {
                cgpaResultLabel.setText("Cumulative GPA: --");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select a course to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void calculate() {
        List<Course> courses = new ArrayList<>();
        try {
            for (int i = 0; i < model.getRowCount(); i++) {
                courses.add(new Course(
                        model.getValueAt(i, 0).toString(),
                        Double.parseDouble(model.getValueAt(i, 1).toString()),
                        Double.parseDouble(model.getValueAt(i, 2).toString())
                ));
            }

            if (courses.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Add at least one course to compute CGPA.", "No Courses", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double cgpa = service.calculateCGPA(courses);
            cgpaResultLabel.setText(String.format("Cumulative GPA: %.2f / 4.00", cgpa));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to calculate CGPA. Check course values.", "Calculation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
