/*
 * ====================================================================
 *  PART    : M1 - ADMIN
 *  PURPOSE : Overview cards: how many users of each role, courses, notices, timetable slots.
 *  PLUGS IN: First page of AdminDashboard.
 *  OOP     : Inheritance (extends BasePanel).
 * ====================================================================
 */
package admin;

import common.*;
import common.ui.*;

import javax.swing.*;
import java.awt.*;

public class AdminHomePanel extends BasePanel {
    private final Admin admin;
    private final JPanel grid = Theme.transparent(new GridLayout(0, 3, 16, 16));

    public AdminHomePanel(Admin admin) {
        super("Welcome, " + admin.getFirstName(), "Faculty of Technology - system overview");
        this.admin = admin;
        JPanel wrap = Theme.transparent(new BorderLayout());
        wrap.add(grid, BorderLayout.NORTH);
        body.add(wrap, BorderLayout.CENTER);
    }

    private JComponent stat(String label, int value, Color color) {
        Card c = new Card(new BorderLayout(0, 4));
        JLabel n = new JLabel(String.valueOf(value));
        n.setFont(Theme.BIG_NUMBER);
        n.setForeground(color);
        c.add(n, BorderLayout.CENTER);
        c.add(Theme.muted(label), BorderLayout.SOUTH);
        c.setPreferredSize(new Dimension(200, 110));
        return c;
    }

    @Override
    public void onShow() {
        grid.removeAll();
        try {
            UserAdminDAO d = admin.manageUsers();
            grid.add(stat("Lecturers", d.count("lecturer"), Theme.ACCENT));
            grid.add(stat("Technical Officers", d.count("technical_officer"), Theme.SUCCESS));
            grid.add(stat("Undergraduates", d.count("undergraduate"), Theme.WARNING));
            grid.add(stat("Courses", d.count("course"), Theme.ACCENT_DARK));
            grid.add(stat("Notices posted", d.count("notice"), Theme.DANGER));
            grid.add(stat("Timetable slots", d.count("timetable"), Theme.TEXT));
        } catch (DatabaseException e) {
            Dialogs.handle(this, e);
        }
        grid.revalidate();
        grid.repaint();
    }
}
