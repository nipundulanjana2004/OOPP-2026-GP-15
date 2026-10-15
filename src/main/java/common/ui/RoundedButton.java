/*
 * ====================================================================
 *  PART    : COMMON UI (shared by M1-M4)
 *  PURPOSE : A flat, rounded, hover-aware button (PRIMARY / SECONDARY / DANGER / SUCCESS).
 *  PLUGS IN: Used by every form and toolbar of M1-M4.
 *  OOP     : INHERITANCE (extends JButton) + method overriding (paintComponent).
 * ====================================================================
 */
package common.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundedButton extends JButton {
    public enum Kind { PRIMARY, SECONDARY, DANGER, SUCCESS }

    private final Kind kind;
    private boolean hover = false;

    public RoundedButton(String text) {
        this(text, Kind.PRIMARY);
    }

    public RoundedButton(String text, Kind kind) {
        super(text);
        this.kind = kind;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setFont(Theme.BOLD);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { hover = true; repaint(); }

            @Override
            public void mouseExited(MouseEvent e) { hover = false; repaint(); }
        });
    }

    private Color base() {
        switch (kind) {
            case DANGER: return Theme.DANGER;
            case SUCCESS: return Theme.SUCCESS;
            case SECONDARY: return Color.WHITE;
            default: return Theme.ACCENT;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color c = base();
        if (!isEnabled()) {
            c = new Color(0xC9D0E0);
        } else if (hover || getModel().isPressed()) {
            c = kind == Kind.SECONDARY ? Theme.SELECT : c.darker();
        }
        g2.setColor(c);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        if (kind == Kind.SECONDARY) {
            g2.setColor(Theme.BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        }
        setForeground(kind == Kind.SECONDARY ? Theme.TEXT : Color.WHITE);
        g2.dispose();
        super.paintComponent(g);
    }
}
