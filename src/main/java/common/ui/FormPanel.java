/*
 * ====================================================================
 *  PART    : COMMON UI (shared by M1-M4)
 *  PURPOSE : Stacked label + field form helper (GridBagLayout) so every form in the app looks alike.
 *  PLUGS IN: M1 management forms, M2 marks/material forms, M3 medical form, common ProfilePanel.
 *  OOP     : Encapsulation of layout code; inheritance (extends JPanel).
 * ====================================================================
 */
package common.ui;

import javax.swing.*;
import java.awt.*;

public class FormPanel extends JPanel implements Scrollable {
    private int row = 0;

    public FormPanel() {
        super(new GridBagLayout());
        setOpaque(false);
    }

    /** Adds "label above field" spanning the full width. */
    public void addRow(String label, JComponent field) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = row++;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(8, 0, 2, 0);
        add(Theme.muted(label.toUpperCase()), c);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = row++;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 4, 0);
        add(field, c);
    }

    public void addGap(int px) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = row++;
        c.insets = new Insets(px, 0, 0, 0);
        add(Box.createVerticalStrut(1), c);
    }

    /** Puts buttons (or any component) in one row. */
    public void addButtons(JComponent... buttons) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.setOpaque(false);
        for (JComponent b : buttons) p.add(b);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = row++;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(12, 0, 0, 0);
        add(p, c);
    }

    /** Pushes everything to the top. Call last. */
    public void finish() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = row++;
        c.weighty = 1;
        add(Box.createGlue(), c);
    }

    // ---- Scrollable: the form always fits the width of the scroll pane (no horizontal scrollbar) ----
    @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
    @Override public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 16; }
    @Override public int getScrollableBlockIncrement(Rectangle r, int o, int d) { return 64; }
    @Override public boolean getScrollableTracksViewportWidth() { return true; }
    @Override public boolean getScrollableTracksViewportHeight() { return false; }
}
