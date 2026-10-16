/*
 * ====================================================================
 *  PART    : COMMON UI (shared by M1-M4)
 *  PURPOSE : Login window (FR-01). Checks the password through UserDAO and hands over to RoleRouter.
 *  PLUGS IN: First screen of the application - used by all four roles.
 *  OOP     : Inheritance (extends JFrame), exception handling (InvalidLoginException), uses UserDAO (database).
 * ====================================================================
 */
package common.ui;

import common.*;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final JTextField usernameField = Theme.text(20);
    private final JPasswordField passwordField = Theme.password(20);
    private final JLabel messageLabel = new JLabel(" ");
    private final RoundedButton loginButton = new RoundedButton("Sign in");

    public LoginFrame() {
        super("Faculty of Technology MIS - Sign in");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(980, 590);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2));
        add(buildBrandPanel());
        add(buildFormPanel());
        getRootPane().setDefaultButton(loginButton);
    }

    private JPanel buildBrandPanel() {
        JPanel p = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(0x172036), getWidth(), getHeight(), new Color(0x3A63E0)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 22));
                g2.fillOval(-80, getHeight() - 260, 380, 380);
                g2.fillOval(getWidth() - 200, -120, 320, 320);
                g2.setColor(new Color(255, 255, 255, 14));
                g2.fillOval(getWidth() - 330, getHeight() - 200, 260, 260);
                g2.dispose();
            }
        };
        JLabel a = new JLabel("Faculty of Technology");
        a.setFont(new Font("Segoe UI", Font.BOLD, 34));
        a.setForeground(Color.WHITE);
        a.setBounds(48, 150, 420, 44);
        JLabel b = new JLabel("Management System");
        b.setFont(new Font("Segoe UI", Font.PLAIN, 26));
        b.setForeground(new Color(0xC9D8FF));
        b.setBounds(48, 194, 420, 36);
        JLabel c = new JLabel("<html>Attendance, medicals, marks, eligibility,<br>grades and GPA - all in one place.</html>");
        c.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        c.setForeground(new Color(0xB5C3E6));
        c.setBounds(48, 250, 420, 50);
        JLabel d = new JLabel("University of Ruhuna | Faculty Of Technology");
        d.setFont(Theme.SMALL);
        d.setForeground(new Color(0x9FB0DA));
        d.setBounds(48, 520, 420, 20);
        p.add(a);
        p.add(b);
        p.add(c);
        p.add(d);
        return p;
    }

    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(Color.WHITE);
        FormPanel f = new FormPanel();
        f.setPreferredSize(new Dimension(340, 330));
        JLabel hello = Theme.title("Welcome back");
        JLabel sub = Theme.muted("Sign in with your university account");
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.anchor = GridBagConstraints.WEST;
        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        hello.setAlignmentX(0);
        sub.setAlignmentX(0);
        box.add(hello);
        box.add(sub);
        box.add(Box.createVerticalStrut(14));
        f.addRow("Username", usernameField);
        f.addRow("Password", passwordField);
        messageLabel.setFont(Theme.SMALL);
        messageLabel.setForeground(Theme.DANGER);
        f.addRow(" ", messageLabel);
        loginButton.setPreferredSize(new Dimension(340, 42));
        loginButton.addActionListener(e -> doLogin());
        f.addRow("", loginButton);
        JLabel hint = Theme.muted("<html>Demo logins:  admin / Admin@123<br>lecturer1 / Lecturer@123   officer1 / Officer@123<br>ug01 / Student@123</html>");
        f.addRow("", hint);
        f.finish();
        f.setAlignmentX(0);
        box.add(f);
        outer.add(box, g);
        return outer;
    }

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
