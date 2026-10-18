/*
 * ====================================================================
 *  PART    : COMMON UI (shared by M1-M4)
 *  PURPOSE : A white rounded panel with a soft border - the building block of every page.
 *  PLUGS IN: Used by TablePanel, forms, dashboards (M1-M4).
 *  OOP     : Inheritance (extends JPanel), overriding paintComponent.
 * ====================================================================
 */
package common.ui;

import javax.swing.*;
import java.awt.*;

public class Card extends JPanel {
    private Color fill = Theme.CARD;

    public Card() {
        this(new BorderLayout());
    }

    public Card(LayoutManager layout) {
        super(layout);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
    }

    public Card(Color fill) {
        this();
        this.fill = fill;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(0, 0, 0, 10));
        g2.fillRoundRect(1, 3, getWidth() - 2, getHeight() - 3, 18, 18);   // soft shadow
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 3, 16, 16);
        g2.setColor(Theme.BORDER);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 3, 16, 16);
        g2.dispose();
        super.paintComponent(g);
    }
}
