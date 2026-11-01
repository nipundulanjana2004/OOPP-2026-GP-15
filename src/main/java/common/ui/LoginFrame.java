
package common.ui;

import common.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Arc2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class LoginFrame extends JFrame {
    private static final Color MAROON = new Color(0x8A0B24);
    private static final Color MAROON_DARK = new Color(0x6A0A25);
    private static final Color PAGE_BG = new Color(0xF1F2F4);
    private static final Color PHOTO_BG = new Color(0xE8EAEE);
    private static final Color INK = new Color(0x161B2E);
    private static final Color GREY = new Color(0x5E6577);
    private static final Color FIELD_BORDER = new Color(0xD9DCE3);
    private static final String FONT = "Segoe UI";

    private final PlaceholderField usernameField = new PlaceholderField("e.g., TG/202X/XXX");
    private final JPasswordField passwordField = new JPasswordField();
    private final JLabel messageLabel = new JLabel(" ", SwingConstants.CENTER);
    private final LoginButton loginButton = new LoginButton("Log In");

    public LoginFrame() {
        super("Faculty of Technology - TECLMS Sign in");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 740);
        setMinimumSize(new Dimension(980, 640));
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2));
        add(buildFormSide());
        add(buildPhotoSide());
        getRootPane().setDefaultButton(loginButton);
    }

    // ------------------------------ left side : form ------------------------------
    private JPanel buildFormSide() {
        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(PAGE_BG);

        // header (top-left)
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(40, 56, 0, 0));
        JLabel faculty = new JLabel("FACULTY OF TECHNOLOGY");
        faculty.setFont(new Font(FONT, Font.BOLD, 26));
        faculty.setForeground(MAROON_DARK);
        JLabel uni = new JLabel("UNIVERSITY OF RUHUNA, SRI LANKA");
        uni.setFont(new Font(FONT, Font.PLAIN, 14));
        uni.setForeground(INK);
        header.add(faculty);
        header.add(uni);
        left.add(header, BorderLayout.NORTH);

        // centre form
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setPreferredSize(new Dimension(430, 470));
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTH;

        JLabel welcome = new JLabel("Welcome Back!", SwingConstants.CENTER);
        welcome.setFont(new Font(FONT, Font.BOLD, 36));
        welcome.setForeground(INK);
        g.gridy = 0;
        g.insets = new Insets(0, 0, 10, 0);
        form.add(welcome, g);

        JLabel sub = new JLabel("<html><div style='text-align:center'>Sign in to TECLMS (Virtual Learning Management System)<br>"
                + "Access your courses, grades, and digital learning resources</div></html>", SwingConstants.CENTER);
        sub.setFont(new Font(FONT, Font.PLAIN, 14));
        sub.setForeground(GREY);
        g.gridy = 1;
        g.insets = new Insets(0, 0, 34, 0);
        form.add(sub, g);

        g.gridy = 2;
        g.insets = new Insets(0, 0, 6, 0);
        form.add(fieldLabel("User Name / Index Number"), g);

        FieldBox userBox = new FieldBox(new UserIcon(), usernameField, null);
        g.gridy = 3;
        g.insets = new Insets(0, 0, 18, 0);
        form.add(userBox, g);

        g.gridy = 4;
        g.insets = new Insets(0, 0, 6, 0);
        form.add(fieldLabel("Password"), g);

        passwordField.setEchoChar('\u2022');
        EyeIcon eye = new EyeIcon();
        eye.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boolean show = passwordField.getEchoChar() != (char) 0;
                passwordField.setEchoChar(show ? (char) 0 : '\u2022');
                eye.setCrossed(!show);
            }
        });
        FieldBox passBox = new FieldBox(null, passwordField, eye);
        g.gridy = 5;
        g.insets = new Insets(0, 0, 18, 0);
        form.add(passBox, g);

        loginButton.addActionListener(e -> doLogin());
        g.gridy = 6;
        g.insets = new Insets(0, 0, 10, 0);
        form.add(loginButton, g);

        messageLabel.setFont(new Font(FONT, Font.PLAIN, 12));
        messageLabel.setForeground(Theme.DANGER);
        g.gridy = 7;
        g.insets = new Insets(0, 0, 0, 0);
        form.add(messageLabel, g);

        JPanel centre = new JPanel(new GridBagLayout());
        centre.setOpaque(false);
        centre.add(form);
        left.add(centre, BorderLayout.CENTER);


        return left;
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel("<html>" + text + " <font color='#D6232A'>*</font></html>");
        l.setFont(new Font(FONT, Font.PLAIN, 15));
        l.setForeground(INK);
        return l;
    }

    // ------------------------------ right side : photo ------------------------------
    private JPanel buildPhotoSide() {
        BufferedImage img = null;
        try {
            URL url = LoginFrame.class.getResource("/login_side.jpg");
            if (url != null) img = ImageIO.read(url);
        } catch (IOException ignored) {
            // falls back to the plain maroon panel below
        }
        return new PhotoPanel(img);
    }

    private static class PhotoPanel extends JPanel {
        private final BufferedImage img;

        PhotoPanel(BufferedImage img) {
            this.img = img;
            setBackground(PHOTO_BG);
            setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            Insets in = getInsets();
            int x = in.left, y = in.top, w = getWidth() - in.left - in.right, h = getHeight() - in.top - in.bottom;
            g.setClip(new RoundRectangle2D.Float(x, y, w, h, 40, 40));
            if (img == null) {
                g.setPaint(new GradientPaint(x, y, MAROON_DARK, x + w, y + h, MAROON));
                g.fillRect(x, y, w, h);
            } else {
                // "cover": fill the rounded box, keep the aspect ratio, crop the overflow
                double s = Math.max(w / (double) img.getWidth(), h / (double) img.getHeight());
                int dw = (int) Math.round(img.getWidth() * s), dh = (int) Math.round(img.getHeight() * s);
                g.drawImage(img, x + (w - dw) / 2, y + (h - dh) / 2, dw, dh, null);
            }
            g.dispose();
        }
    }

    // ------------------------------ small custom components ------------------------------
    /** Text field that shows grey hint text while it is empty. */
    private static class PlaceholderField extends JTextField {
        private final String hint;

        PlaceholderField(String hint) {
            this.hint = hint;
        }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            if (getText().isEmpty()) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g.setColor(new Color(0x8A91A3));
                g.setFont(getFont());
                Insets in = getInsets();
                g.drawString(hint, in.left, (getHeight() - g.getFontMetrics().getHeight()) / 2 + g.getFontMetrics().getAscent());
                g.dispose();
            }
        }
    }

    /** Rounded white box around a text field, optional icon on the left / right; border turns maroon on focus. */
    private static class FieldBox extends JPanel {
        private boolean focused = false;

        FieldBox(JComponent leftIcon, JTextField field, JComponent rightIcon) {
            super(new BorderLayout(8, 0));
            setOpaque(false);
            setPreferredSize(new Dimension(100, 50));
            setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
            field.setBorder(BorderFactory.createEmptyBorder());
            field.setOpaque(false);
            field.setFont(new Font(FONT, Font.PLAIN, 15));
            field.setForeground(INK);
            field.setCaretColor(MAROON);
            field.addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { focused = true; repaint(); }
                @Override public void focusLost(FocusEvent e) { focused = false; repaint(); }
            });
            if (leftIcon != null) add(leftIcon, BorderLayout.WEST);
            add(field, BorderLayout.CENTER);
            if (rightIcon != null) add(rightIcon, BorderLayout.EAST);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.WHITE);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g.setColor(focused ? MAROON : FIELD_BORDER);
            g.setStroke(new BasicStroke(focused ? 1.6f : 1f));
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g.dispose();
        }
    }

    /** Maroon rounded "Log In" button (hover = darker, disabled = faded). */
    private static class LoginButton extends JButton {
        private boolean hover = false;

        LoginButton(String text) {
            super(text);
            setFont(new Font(FONT, Font.PLAIN, 17));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(100, 52));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(!isEnabled() ? new Color(0xB98A95) : (hover ? MAROON_DARK : MAROON));
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g.dispose();
            super.paintComponent(g0);
        }
    }

    /** Little person icon drawn with shapes (no image file needed). */
    private static class UserIcon extends JComponent {
        UserIcon() {
            setPreferredSize(new Dimension(24, 24));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(GREY);
            g.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int cx = getWidth() / 2, cy = getHeight() / 2;
            g.draw(new Ellipse2D.Float(cx - 5, cy - 10, 10, 10));
            g.draw(new Arc2D.Float(cx - 9, cy + 1, 18, 16, 0, 180, Arc2D.OPEN));
            g.dispose();
        }
    }

    /** Show / hide password eye (crossed = password hidden). */
    private static class EyeIcon extends JComponent {
        private boolean crossed = true;

        EyeIcon() {
            setPreferredSize(new Dimension(28, 24));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText("Show / hide password");
        }

        void setCrossed(boolean c) {
            crossed = c;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(GREY);
            g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int cx = getWidth() / 2, cy = getHeight() / 2;
            g.draw(new Arc2D.Float(cx - 11, cy - 9, 22, 18, 0, 180, Arc2D.OPEN));
            g.draw(new Arc2D.Float(cx - 11, cy - 9, 22, 18, 180, 180, Arc2D.OPEN));
            g.fill(new Ellipse2D.Float(cx - 3, cy - 3, 6, 6));
            if (crossed) g.drawLine(cx - 10, cy + 10, cx + 10, cy - 10);
            g.dispose();
        }
    }

    // ------------------------------ login logic (unchanged behaviour) ------------------------------
    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        messageLabel.setText(" ");
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter your username and password.");
            return;
        }
        loginButton.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            User user = new UserDAO().authenticate(username, password);
            LoginContext.setCurrentUser(user);
            JFrame dashboard = RoleRouter.openDashboard(user);
            dispose();
            dashboard.setVisible(true);
        } catch (InvalidLoginException e) {
            messageLabel.setText(e.getMessage());
            passwordField.setText("");
        } catch (AppException e) {
            Dialogs.handle(this, e);
        } finally {
            loginButton.setEnabled(true);
            setCursor(Cursor.getDefaultCursor());
        }
    }
}
