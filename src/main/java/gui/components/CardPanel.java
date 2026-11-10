package gui.components;

import gui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class CardPanel extends JPanel {

    private final int radius;

    public CardPanel() {
        this(16);
    }

    public CardPanel(int radius) {
        this.radius = radius;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Subtle ambient shadow
        g2.setColor(new Color(0, 0, 0, 10));
        g2.fill(new RoundRectangle2D.Float(2, 4, width - 4, height - 4, radius, radius));

        // Draw clean white card surface
        RoundRectangle2D cardShape = new RoundRectangle2D.Float(1, 1, width - 2, height - 3, radius, radius);
        g2.setColor(UITheme.CARD_BG);
        g2.fill(cardShape);

        // Draw crisp subtle border
        g2.setColor(UITheme.CARD_BORDER);
        g2.setStroke(new BasicStroke(1.2f));
        g2.draw(cardShape);

        g2.dispose();
        super.paintComponent(g);
    }
}
