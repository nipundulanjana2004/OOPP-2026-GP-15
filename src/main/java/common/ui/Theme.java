/*
 * ====================================================================
 *  PART    : COMMON UI (shared by M1-M4)
 *  PURPOSE : ONE place for colours, fonts and small component factories so every screen looks the same.
 *  PLUGS IN: Every panel/dashboard of M1, M2, M3, M4 calls Theme.* instead of styling by hand.
 *  OOP     : Encapsulation: static utility class, private constructor.
 * ====================================================================
 */
package common.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class Theme {
    public static final Color SIDEBAR = new Color(0x172036);
    public static final Color SIDEBAR_HOVER = new Color(0x24304F);
    public static final Color ACCENT = new Color(0x4F7CFF);
    public static final Color ACCENT_DARK = new Color(0x3A63E0);
    public static final Color BG = new Color(0xF2F5FB);
    public static final Color CARD = Color.WHITE;
    public static final Color TEXT = new Color(0x1F2A44);
    public static final Color MUTED = new Color(0x6B7691);
    public static final Color BORDER = new Color(0xDFE5F1);
    public static final Color SUCCESS = new Color(0x15A06B);
    public static final Color DANGER = new Color(0xE5484D);
    public static final Color WARNING = new Color(0xE69A0B);
    public static final Color ZEBRA = new Color(0xF7F9FD);
    public static final Color SELECT = new Color(0xDCE6FF);

    public static final Font BASE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font H1 = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font H2 = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font BIG_NUMBER = new Font("Segoe UI", Font.BOLD, 30);

    private Theme() {
    }

    /** Call once at start-up (Main). */
    public static void install() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        try {
            javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(new javax.swing.plaf.metal.DefaultMetalTheme() {
                @Override public javax.swing.plaf.ColorUIResource getPrimary1() { return new javax.swing.plaf.ColorUIResource(0xC5CEE3); }
                @Override public javax.swing.plaf.ColorUIResource getPrimary2() { return new javax.swing.plaf.ColorUIResource(0xDCE6FF); }
                @Override public javax.swing.plaf.ColorUIResource getPrimary3() { return new javax.swing.plaf.ColorUIResource(0xDCE6FF); }
                @Override public javax.swing.plaf.ColorUIResource getSecondary1() { return new javax.swing.plaf.ColorUIResource(0xC5CEE3); }
                @Override public javax.swing.plaf.ColorUIResource getSecondary2() { return new javax.swing.plaf.ColorUIResource(0xDFE5F1); }
                @Override public javax.swing.plaf.ColorUIResource getSecondary3() { return new javax.swing.plaf.ColorUIResource(0xF2F5FB); }
                @Override public javax.swing.plaf.FontUIResource getControlTextFont() { return new javax.swing.plaf.FontUIResource(BASE); }
                @Override public javax.swing.plaf.FontUIResource getUserTextFont() { return new javax.swing.plaf.FontUIResource(BASE); }
                @Override public javax.swing.plaf.FontUIResource getMenuTextFont() { return new javax.swing.plaf.FontUIResource(BASE); }
                @Override public javax.swing.plaf.FontUIResource getSystemTextFont() { return new javax.swing.plaf.FontUIResource(BASE); }
                @Override public javax.swing.plaf.FontUIResource getWindowTitleFont() { return new javax.swing.plaf.FontUIResource(BOLD); }
            });
            UIManager.setLookAndFeel(new javax.swing.plaf.metal.MetalLookAndFeel());
        } catch (Exception e) {
            // keep the default look and feel
        }
        UIManager.put("ScrollBarUI", "common.ui.SlimScrollBarUI");
        UIManager.put("ScrollBar.width", 12);
        UIManager.put("ComboBox.background", Color.WHITE);
        UIManager.put("ComboBox.selectionBackground", SELECT);
        UIManager.put("ComboBox.selectionForeground", TEXT);
        UIManager.put("Panel.background", BG);
        UIManager.put("TabbedPane.contentAreaColor", BG);
        UIManager.put("TabbedPane.selected", Color.WHITE);
        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("Spinner.arrowButtonBorder", BorderFactory.createEmptyBorder());
        for (String key : new String[]{"Label", "Button", "TextField", "PasswordField", "TextArea", "ComboBox", "CheckBox",
                "RadioButton", "Table", "TableHeader", "TabbedPane", "List", "Spinner", "FormattedTextField", "ToolTip"}) {
            UIManager.put(key + ".font", BASE);
        }
        UIManager.put("OptionPane.messageFont", BASE);
        UIManager.put("OptionPane.buttonFont", BASE);
    }

    /** Green / red / amber colour for well-known status words, or null. */
    public static Color statusColor(String text) {
        if (text == null) return null;
        switch (text.trim().toUpperCase()) {
            case "ELIGIBLE": case "APPROVED": case "PRESENT": case "YES": case "ACTIVE": case "PASS":
                return SUCCESS;
            case "NOT ELIGIBLE": case "REJECTED": case "ABSENT": case "NO": case "INACTIVE": case "FAIL": case "<80% WITHOUT MEDICALS":
            case "<80% EVEN WITH MEDICALS": case "NOT ELIGIBLE (ATTENDANCE)": case "NOT ELIGIBLE (CA)":
                return DANGER;
            case "PENDING": case "EXACTLY 80%":
                return WARNING;
            default:
                return null;
        }
    }

    // ------------------------------ component factories ------------------------------
    public static Border fieldBorder() {
        return BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10));
    }

    public static JTextField text(int columns) {
        JTextField f = new JTextField(columns);
        styleField(f);
        return f;
    }

    public static JPasswordField password(int columns) {
        JPasswordField f = new JPasswordField(columns);
        styleField(f);
        return f;
    }

    public static JTextArea area(int rows, int columns) {
        JTextArea a = new JTextArea(rows, columns);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        styleField(a);
        return a;
    }

    public static void styleField(JTextComponent f) {
        f.setFont(BASE);
        f.setForeground(TEXT);
        f.setBackground(Color.WHITE);
        f.setBorder(fieldBorder());
        f.setCaretColor(ACCENT);
    }

    public static <T> JComboBox<T> combo(java.util.List<T> items) {
        JComboBox<T> c = new JComboBox<>();
        for (T t : items) c.addItem(t);
        c.setFont(BASE);
        c.setPreferredSize(new Dimension(220, 34));
        return c;
    }

    public static <T> JComboBox<T> combo(T[] items) {
        return combo(java.util.Arrays.asList(items));
    }

    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(BASE);
        l.setForeground(TEXT);
        return l;
    }

    public static JLabel muted(String text) {
        JLabel l = new JLabel(text);
        l.setFont(SMALL);
        l.setForeground(MUTED);
        return l;
    }

    public static JLabel title(String text) {
        JLabel l = new JLabel(text);
        l.setFont(H1);
        l.setForeground(TEXT);
        return l;
    }

    public static JLabel heading(String text) {
        JLabel l = new JLabel(text);
        l.setFont(H2);
        l.setForeground(TEXT);
        return l;
    }

    public static JPanel transparent(LayoutManager lm) {
        JPanel p = new JPanel(lm);
        p.setOpaque(false);
        return p;
    }
}
