/*
 * ====================================================================
 *  PART    : COMMON UI (shared by M1-M4)
 *  PURPOSE : Round profile picture (or coloured initials when no picture is set).
 *  PLUGS IN: Shown in the dashboard sidebar and in ProfilePanel (M4 undergraduates can change their photo).
 *  OOP     : Inheritance (extends JComponent), overriding paintComponent.
 * ====================================================================
 */
package common.ui;

import common.User;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;

public class AvatarLabel extends JComponent {
    private final int size;
    private String initials = "?";
    private BufferedImage image = null;

    public AvatarLabel(int size, User user) {
        this.size = size;
        setPreferredSize(new Dimension(size, size));
        setMinimumSize(new Dimension(size, size));
        update(user);
    }

    public void update(User user) {
        initials = user.getInitials();
        setPicture(user.getProfilePicture());
    }

    public void setPicture(String path) {
        image = null;
        try {
            if (path != null && !path.isEmpty() && new File(path).exists()) {
                image = javax.imageio.ImageIO.read(new File(path));
            }
        } catch (Exception e) {
            image = null;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        if (image != null) {
            g2.setClip(new Ellipse2D.Float(0, 0, size, size));
            int w = image.getWidth(), h = image.getHeight(), s = Math.min(w, h);
            g2.drawImage(image, 0, 0, size, size, (w - s) / 2, (h - s) / 2, (w + s) / 2, (h + s) / 2, null);
        } else {
            g2.setPaint(new GradientPaint(0, 0, Theme.ACCENT, size, size, new Color(0x7B5CFF)));
            g2.fillOval(0, 0, size, size);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, size / 3));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(initials, (size - fm.stringWidth(initials)) / 2, (size + fm.getAscent() - fm.getDescent()) / 2);
        }
        g2.dispose();
    }
}
