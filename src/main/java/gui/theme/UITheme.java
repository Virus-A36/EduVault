package gui.theme;

import java.awt.*;

public final class UITheme {

    private UITheme() {}

    // Pure White Clean Theme
    public static final Color BG_PRIMARY = Color.WHITE;                  // #FFFFFF Pure White
    public static final Color BG_SECONDARY = new Color(248, 250, 252);   // Slate 50
    public static final Color CARD_BG = Color.WHITE;                     // #FFFFFF
    public static final Color CARD_BORDER = new Color(226, 232, 240);    // Slate 200

    public static final Color TEXT_PRIMARY = new Color(15, 23, 42);      // Slate 900
    public static final Color TEXT_SECONDARY = new Color(100, 116, 139); // Slate 500
    public static final Color TEXT_MUTED = new Color(148, 163, 184);     // Slate 400

    // Modern Professional Accents
    public static final Color PRIMARY = new Color(37, 99, 235);          // Vibrant Royal Blue
    public static final Color PRIMARY_BG = new Color(37, 99, 235);       // Royal Blue Selection Background
    public static final Color PRIMARY_HOVER = new Color(29, 78, 216);    // Blue 700
    public static final Color PRIMARY_PRESSED = new Color(30, 64, 175);  // Blue 800

    public static final Color SUCCESS = new Color(16, 185, 129);         // Emerald Green
    public static final Color SUCCESS_HOVER = new Color(5, 150, 105);
    public static final Color SUCCESS_BG = new Color(240, 253, 244);     // Light Emerald

    public static final Color DANGER = new Color(239, 68, 68);           // Crimson Red
    public static final Color DANGER_HOVER = new Color(220, 38, 38);
    public static final Color DANGER_BG = new Color(254, 242, 242);      // Light Red

    public static final Color WARNING = new Color(245, 158, 11);         // Amber
    public static final Color WARNING_BG = new Color(254, 252, 232);

    // Inputs & Forms
    public static final Color INPUT_BG = Color.WHITE;
    public static final Color INPUT_BORDER = new Color(209, 213, 219);   // Gray 300
    public static final Color INPUT_FOCUS = new Color(37, 99, 235);      // Blue 600
    public static final Color INPUT_FOCUS_GLOW = new Color(37, 99, 235, 45); // Soft Blue Focus Glow

    // Tables & Lists
    public static final Color TABLE_BG = Color.WHITE;                    // Pure White Table Background
    public static final Color TABLE_HEADER_BG = new Color(248, 250, 252); // Soft Light Header Background

    // Typography
    public static final String FONT_FAMILY = "Segoe UI";
    public static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, 24);
    public static final Font FONT_HEADER = new Font(FONT_FAMILY, Font.BOLD, 18);
    public static final Font FONT_SECTION = new Font(FONT_FAMILY, Font.BOLD, 15);
    public static final Font FONT_BODY = new Font(FONT_FAMILY, Font.PLAIN, 14);
    public static final Font FONT_BODY_BOLD = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font FONT_SMALL = new Font(FONT_FAMILY, Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font(FONT_FAMILY, Font.BOLD, 13);
}
