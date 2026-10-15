/*
 * ====================================================================
 *  PART    : COMMON UI (shared by M1-M4)
 *  PURPOSE : Shows the notices that are relevant to the logged-in user as cards (FR-12). Same panel for Lecturer, Technical Officer and Undergraduate.
 *  PLUGS IN: M2, M3, M4 dashboards add it with: new NoticePanel(user). Data comes from NoticeDAO.findFor(user), which M1's Admin fills.
 *  OOP     : Inheritance (extends BasePanel), POLYMORPHISM (works for any User subclass).
 * ====================================================================
 */
package common.ui;

import common.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class NoticePanel extends BasePanel {
    private final User user;
    private final JPanel list = new JPanel();

    public NoticePanel(User user) {
        super("Notices", "Announcements posted by the Faculty for you");
        this.user = user;
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);
        JScrollPane sp = new JScrollPane(list);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        body.add(sp, BorderLayout.CENTER);
    }

    @Override
    public void onShow() {
        list.removeAll();
        try {
            List<Notice> notices = new NoticeDAO().findFor(user);
            if (notices.isEmpty()) {
                list.add(Theme.muted("No notices for you right now."));
            }
            for (Notice n : notices) {
                list.add(card(n));
                list.add(Box.createVerticalStrut(12));
            }
        } catch (DatabaseException e) {
            Dialogs.handle(this, e);
        }
        list.revalidate();
        list.repaint();
    }

    private JComponent card(Notice n) {
        Card c = new Card(new BorderLayout(0, 6));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel head = Theme.transparent(new BorderLayout());
        head.add(Theme.heading(n.getTitle()), BorderLayout.WEST);
        JLabel tag = Theme.muted(n.getCreatedDate() + "   |   " + n.audience() + "   |   by " + n.getPostedByName());
        head.add(tag, BorderLayout.EAST);
        c.add(head, BorderLayout.NORTH);
        JTextArea t = new JTextArea(n.getContent());
        t.setLineWrap(true);
        t.setWrapStyleWord(true);
        t.setEditable(false);
        t.setOpaque(false);
        t.setFont(Theme.BASE);
        t.setForeground(Theme.TEXT);
        c.add(t, BorderLayout.CENTER);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));
        return c;
    }
}
