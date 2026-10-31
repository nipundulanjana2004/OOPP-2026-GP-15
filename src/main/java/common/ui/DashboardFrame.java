
package common.ui;

import common.DBConnection;
import common.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class DashboardFrame extends JFrame {
    private final User user;
    private final JPanel nav = new JPanel();
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final Map<String, BasePanel> pages = new LinkedHashMap<>();
    private final Map<String, NavButton> buttons = new LinkedHashMap<>();
    private String firstPage = null;

    public DashboardFrame(User user, String roleTitle) {
        super("Faculty of Technology MIS  -  " + roleTitle);
        this.user = user;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(Math.min(1380, screen.width - 30), Math.min(820, screen.height - 60));
        setMinimumSize(new Dimension(1000, 620));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(buildSidebar(roleTitle), BorderLayout.WEST);
        content.setBackground(Theme.BG);
        add(content, BorderLayout.CENTER);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                DBConnection.closeConnection();
            }
        });
    }

    public User getUser() {
        return user;
    }

    private JPanel buildSidebar(String roleTitle) {
        JPanel side = new JPanel(new BorderLayout());
        side.setBackground(Theme.SIDEBAR);
        side.setPreferredSize(new Dimension(250, 100));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(BorderFactory.createEmptyBorder(22, 20, 10, 20));
        JLabel brand = new JLabel("FoT  MIS");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 22));
        brand.setForeground(Color.WHITE);
        JLabel sub = new JLabel("University of Ruhuna");
        sub.setFont(Theme.SMALL);
        sub.setForeground(new Color(0x8D9BC0));
        top.add(brand);
        top.add(sub);
        top.add(Box.createVerticalStrut(20));

        JPanel me = new JPanel(new BorderLayout(12, 0));
        me.setOpaque(false);
        me.setAlignmentX(Component.LEFT_ALIGNMENT);
        me.add(new AvatarLabel(46, user), BorderLayout.WEST);
        JPanel who = new JPanel(new GridLayout(2, 1));
        who.setOpaque(false);
        JLabel name = new JLabel(user.getFullName());
        name.setFont(Theme.BOLD);
        name.setForeground(Color.WHITE);
        JLabel role = new JLabel(roleTitle);
        role.setFont(Theme.SMALL);
        role.setForeground(new Color(0x8D9BC0));
        who.add(name);
        who.add(role);
        me.add(who, BorderLayout.CENTER);
        top.add(me);
        top.add(Box.createVerticalStrut(14));
        side.add(top, BorderLayout.NORTH);

        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        JScrollPane scrollPane = new JScrollPane(nav);

        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        side.add(scrollPane, BorderLayout.CENTER);


        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 16, 18, 16));
        RoundedButton out = new RoundedButton("Log out", RoundedButton.Kind.SECONDARY);
        out.addActionListener(e -> logout());
        bottom.add(out, BorderLayout.CENTER);
        side.add(bottom, BorderLayout.SOUTH);
        return side;
    }


    protected void addSection(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(new Color(0x6F7EA6));
        l.setBorder(BorderFactory.createEmptyBorder(14, 10, 4, 0));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        nav.add(l);
    }

    protected void addPage(String name, BasePanel page) {
        pages.put(name, page);
        content.add(page, name);
        NavButton b = new NavButton(name);
        b.addActionListener(e -> showPage(name));
        buttons.put(name, b);
        nav.add(b);
        nav.add(Box.createVerticalStrut(3));
        if (firstPage == null) firstPage = name;
    }

    public void showPage(String name) {
        for (Map.Entry<String, NavButton> e : buttons.entrySet()) {
            e.getValue().setSelectedItem(e.getKey().equals(name));
        }
        BasePanel p = pages.get(name);
        if (p != null) p.onShow();
        cards.show(content, name);
    }

    protected void start() {
        nav.add(Box.createVerticalGlue());
        if (firstPage != null) showPage(firstPage);
        setVisible(true);
    }

    private void logout() {
        user.logout();
        dispose();
        new LoginFrame().setVisible(true);
    }

    private static class NavButton extends JButton {
        private boolean selected = false;
        private boolean hover = false;

        NavButton(String text) {
            super(text);
            setHorizontalAlignment(LEFT);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setFont(Theme.BASE);
            setForeground(new Color(0xC9D2EA));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 10));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        void setSelectedItem(boolean s) {
            selected = s;
            setForeground(s ? Color.WHITE : new Color(0xC9D2EA));
            setFont(s ? Theme.BOLD : Theme.BASE);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (selected || hover) {
                g2.setColor(selected ? new Color(0x2C3C66) : Theme.SIDEBAR_HOVER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
            if (selected) {
                g2.setColor(Theme.ACCENT);
                g2.fillRoundRect(0, 8, 4, getHeight() - 16, 4, 4);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
