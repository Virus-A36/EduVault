package gui;

import gui.theme.UITheme;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                // Apply system look and feel as baseline
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                // Set modern anti-aliasing rendering hints across Swing
                System.setProperty("awt.useSystemAAFontSettings", "on");
                System.setProperty("swing.aatext", "true");

                // Customize global UI defaults to clean pure white theme
                UIManager.put("Panel.background", Color.WHITE);
                UIManager.put("OptionPane.background", Color.WHITE);
                UIManager.put("OptionPane.messageForeground", UITheme.TEXT_PRIMARY);
                UIManager.put("OptionPane.messageFont", UITheme.FONT_BODY);
                UIManager.put("Label.font", UITheme.FONT_BODY);
                UIManager.put("Label.foreground", UITheme.TEXT_PRIMARY);
                UIManager.put("TabbedPane.font", UITheme.FONT_BUTTON);
                UIManager.put("TabbedPane.background", Color.WHITE);
                UIManager.put("TabbedPane.selected", Color.WHITE);
                UIManager.put("TableHeader.font", UITheme.FONT_BUTTON);
                UIManager.put("Table.font", UITheme.FONT_BODY);
                UIManager.put("Table.rowHeight", 34);
                UIManager.put("Table.background", Color.WHITE);
                UIManager.put("ScrollPane.background", Color.WHITE);
                UIManager.put("Viewport.background", Color.WHITE);

            } catch (Exception e) {
                System.err.println("Theme initialization error: " + e.getMessage());
            }

            new LoginFrame().setVisible(true);
        });
    }
}
