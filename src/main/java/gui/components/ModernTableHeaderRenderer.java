package gui.components;

import gui.theme.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ModernTableHeaderRenderer extends DefaultTableCellRenderer {

    public ModernTableHeaderRenderer() {
        setOpaque(true);
        setHorizontalAlignment(SwingConstants.LEFT);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        setText(value != null ? value.toString() : "");
        setFont(UITheme.FONT_BUTTON);
        setBackground(UITheme.TABLE_HEADER_BG); // Elevated Obsidian Slate Header
        setForeground(UITheme.TEXT_PRIMARY);    // Diamond Pure White
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 1, UITheme.CARD_BORDER),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        return this;
    }
}
