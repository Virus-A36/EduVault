package gui.components;

import gui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class ModernButton extends JButton {

    public enum Style {
        PRIMARY, SECONDARY, DANGER, SUCCESS, OUTLINE
    }

    private final Style style;
    private final int radius;
    private boolean isHovered = false;
    private boolean isPressed = false;

    // Animation variables
    private float rippleAlpha = 0f;
    private float rippleRadius = 0f;
    private Point clickPoint = new Point(0, 0);
    private Timer rippleTimer;

    public ModernButton(String text) {
        this(text, Style.PRIMARY, 10);
    }

    public ModernButton(String text, Style style) {
        this(text, style, 10);
    }

    public ModernButton(String text, Style style, int radius) {
        super(text);
        this.style = style;
        this.radius = radius;

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(UITheme.FONT_BUTTON);

        // Standard padding
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        initAnimation();
    }

    private void initAnimation() {
        // Smooth click ripple animation timer
        rippleTimer = new Timer(15, e -> {
            rippleRadius += 6f;
            rippleAlpha -= 0.04f;
            if (rippleAlpha <= 0f) {
                rippleAlpha = 0f;
                rippleTimer.stop();
            }
            repaint();
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                clickPoint = e.getPoint();
                rippleRadius = 5f;
                rippleAlpha = 0.45f;
                if (rippleTimer.isRunning()) {
                    rippleTimer.stop();
                }
                rippleTimer.start();
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
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

        // Slight 1px downward shift on press for tactile mechanical click effect
        int offsetY = isPressed ? 1 : 0;

        Color bg;
        Color fg;
        Color border = null;

        switch (style) {
            case PRIMARY:
                bg = isPressed ? UITheme.PRIMARY_PRESSED : (isHovered ? UITheme.PRIMARY_HOVER : UITheme.PRIMARY);
                fg = Color.WHITE;
                break;
            case DANGER:
                bg = isPressed ? UITheme.DANGER_HOVER.darker() : (isHovered ? UITheme.DANGER_HOVER : UITheme.DANGER);
                fg = Color.WHITE;
                break;
            case SUCCESS:
                bg = isPressed ? UITheme.SUCCESS_HOVER.darker() : (isHovered ? UITheme.SUCCESS_HOVER : UITheme.SUCCESS);
                fg = Color.WHITE;
                break;
            case OUTLINE:
                bg = isHovered ? UITheme.CARD_BORDER : UITheme.CARD_BG;
                fg = UITheme.PRIMARY;
                border = isHovered ? UITheme.PRIMARY : UITheme.CARD_BORDER;
                break;
            case SECONDARY:
            default:
                bg = isPressed ? UITheme.CARD_BORDER.darker() : (isHovered ? UITheme.CARD_BORDER : UITheme.BG_SECONDARY);
                fg = UITheme.TEXT_PRIMARY;
                border = UITheme.CARD_BORDER;
                break;
        }

        RoundRectangle2D shape = new RoundRectangle2D.Float(1, 1 + offsetY, width - 2, height - 2, radius, radius);

        // Fill background
        g2.setColor(bg);
        g2.fill(shape);

        // Draw border if needed
        if (border != null) {
            g2.setColor(border);
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(shape);
        }

        // Draw ripple effect
        if (rippleAlpha > 0f) {
            g2.setClip(shape);
            g2.setColor(new Color(1f, 1f, 1f, rippleAlpha));
            g2.fillOval(
                    (int) (clickPoint.x - rippleRadius),
                    (int) (clickPoint.y - rippleRadius),
                    (int) (rippleRadius * 2),
                    (int) (rippleRadius * 2)
            );
            g2.setClip(null);
        }

        // Draw text
        g2.setColor(fg);
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int textX = (width - fm.stringWidth(getText())) / 2;
        int textY = (height - fm.getHeight()) / 2 + fm.getAscent() + offsetY;
        g2.drawString(getText(), textX, textY);

        g2.dispose();
    }
}
