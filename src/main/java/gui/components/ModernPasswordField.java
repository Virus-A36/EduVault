package gui.components;

import gui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

public class ModernPasswordField extends JPasswordField {

    private final String placeholder;
    private boolean isFocused = false;
    private final int radius;

    public ModernPasswordField() {
        this("", 10);
    }

    public ModernPasswordField(String placeholder) {
        this(placeholder, 10);
    }

    public ModernPasswordField(String placeholder, int radius) {
        this.placeholder = placeholder;
        this.radius = radius;

        setOpaque(false);
        setFont(UITheme.FONT_BODY);
        setForeground(UITheme.TEXT_PRIMARY);
        setCaretColor(UITheme.PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Fill background
        RoundRectangle2D shape = new RoundRectangle2D.Float(2, 2, width - 4, height - 4, radius, radius);
        g2.setColor(UITheme.INPUT_BG);
        g2.fill(shape);

        // Draw border and focus glow
        if (isFocused) {
            g2.setColor(UITheme.INPUT_FOCUS_GLOW);
            g2.setStroke(new BasicStroke(3.5f));
            g2.draw(new RoundRectangle2D.Float(1.5f, 1.5f, width - 3f, height - 3f, radius, radius));

            g2.setColor(UITheme.INPUT_FOCUS);
            g2.setStroke(new BasicStroke(1.8f));
            g2.draw(shape);
        } else {
            g2.setColor(UITheme.INPUT_BORDER);
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(shape);
        }

        // Draw placeholder if password is empty and not focused
        if (getPassword().length == 0 && placeholder != null && !placeholder.isEmpty()) {
            g2.setColor(UITheme.TEXT_MUTED);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            Insets insets = getInsets();
            g2.drawString(placeholder, insets.left, (height - fm.getHeight()) / 2 + fm.getAscent());
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
