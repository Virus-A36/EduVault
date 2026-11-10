package gui.components;

import gui.theme.UITheme;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

public class ModernTabbedPaneUI extends BasicTabbedPaneUI {

    public ModernTabbedPaneUI() {
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        tabPane.setBackground(UITheme.BG_PRIMARY);
        tabPane.setForeground(UITheme.TEXT_PRIMARY);
        tabPane.setFont(UITheme.FONT_BUTTON);
    }

    @Override
    protected Insets getTabInsets(int tabPlacement, int tabIndex) {
        return new Insets(10, 24, 10, 24);
    }

    @Override
    protected Insets getSelectedTabPadInsets(int tabPlacement) {
        return new Insets(0, 0, 0, 0);
    }

    @Override
    protected Insets getContentBorderInsets(int tabPlacement) {
        return new Insets(1, 0, 0, 0);
    }

    @Override
    protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
        return 42;
    }

    @Override
    protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
        return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + 24;
    }

    @Override
    protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(UITheme.BG_PRIMARY);
        g2.fillRect(0, 0, tabPane.getWidth(), 44);
        g2.dispose();

        super.paintTabArea(g, tabPlacement, selectedIndex);
    }

    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                      int x, int y, int w, int h, boolean isSelected) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (isSelected) {
            // Elevated card background for active tab
            g2.setColor(UITheme.CARD_BG);
            g2.fillRect(x, y, w, h);

            // Electric Violet Bottom Indicator Line
            g2.setColor(UITheme.PRIMARY);
            g2.fillRect(x, y + h - 3, w, 3);
        } else {
            // Darker background for inactive tabs
            g2.setColor(UITheme.BG_SECONDARY);
            g2.fillRect(x, y, w, h);
        }

        g2.dispose();
    }

    @Override
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                  int x, int y, int w, int h, boolean isSelected) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(UITheme.CARD_BORDER);

        // Divider between tabs
        g2.drawLine(x + w - 1, y + 8, x + w - 1, y + h - 8);

        // Subtle bottom border for inactive tabs
        if (!isSelected) {
            g2.drawLine(x, y + h - 1, x + w, y + h - 1);
        }

        g2.dispose();
    }

    @Override
    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(UITheme.CARD_BORDER);
        // Clean single line dividing tabs from content
        g2.drawLine(0, 42, tabPane.getWidth(), 42);
        g2.dispose();
    }

    @Override
    protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects,
                                       int tabIndex, Rectangle iconRect, Rectangle textRect,
                                       boolean isSelected) {
        // Suppress default dotted focus rectangle for clean modern UI
    }

    @Override
    protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics,
                             int tabIndex, String title, Rectangle textRect, boolean isSelected) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (isSelected) {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, 13));
        } else {
            g2.setColor(UITheme.TEXT_SECONDARY);
            g2.setFont(new Font(UITheme.FONT_FAMILY, Font.PLAIN, 13));
        }

        // Vertically center text
        int textY = textRect.y + metrics.getAscent() - 1;
        g2.drawString(title, textRect.x, textY);

        g2.dispose();
    }
}
