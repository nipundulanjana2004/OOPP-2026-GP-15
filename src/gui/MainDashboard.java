package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainDashboard extends JFrame {

    // ================= COLORS =================
    private final Color NAVY = new Color(15, 31, 58);
    private final Color BLUE = new Color(37, 99, 235);
    private final Color LIGHT_BLUE = new Color(239, 246, 255);
    private final Color BG = new Color(246, 248, 252);
    private final Color WHITE = Color.WHITE;
    private final Color Black = new Color(0, 0, 0,0);
    private final Color TEXT = new Color(4, 5, 5);
    private final Color MUTED = new Color(100, 116, 139);
    private final Color BORDER = new Color(226, 232, 240);
    private final Color GREEN = new Color(22, 163, 74);
    private final Color ORANGE = new Color(234, 88, 12);

    private JPanel contentPanel;

    public MainDashboard() {

        setTitle("University Marks & Results Management System");
        setSize(1280, 760);
        setMinimumSize(new Dimension(1100, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createUI();
    }

    // =========================================================
    // MAIN UI
    // =========================================================

    private void createUI() {

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        root.add(createSidebar(), BorderLayout.WEST);
        root.add(createMainContent(), BorderLayout.CENTER);

        setContentPane(root);
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    private JPanel createSidebar() {

        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(240, 760));
        sidebar.setBackground(NAVY);

        // ---------- TOP ----------
        JPanel top = new JPanel();
        top.setBackground(NAVY);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(new EmptyBorder(30, 25, 20, 25));

        JPanel logoCircle = new JPanel(new GridBagLayout());
        logoCircle.setBackground(BLUE);
        logoCircle.setPreferredSize(new Dimension(48, 48));
        logoCircle.setMaximumSize(new Dimension(48, 48));
        logoCircle.setMinimumSize(new Dimension(48, 48));

        JLabel logoText = new JLabel("U");
        logoText.setForeground(WHITE);
        logoText.setFont(new Font("Arial", Font.BOLD, 24));

        logoCircle.add(logoText);

        JLabel university = new JLabel("UNIVERSITY");
        university.setForeground(WHITE);
        university.setFont(new Font("Arial", Font.BOLD, 17));

        JLabel system = new JLabel("Marks & Results");
        system.setForeground(new Color(148, 163, 184));
        system.setFont(new Font("Arial", Font.PLAIN, 12));

        top.add(logoCircle);
        top.add(Box.createVerticalStrut(15));
        top.add(university);
        top.add(Box.createVerticalStrut(3));
        top.add(system);

        // ---------- MENU ----------
        JPanel menu = new JPanel();
        menu.setBackground(NAVY);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(new EmptyBorder(10, 15, 10, 15));

        menu.add(createMenuButton("▣   Dashboard", true));
        menu.add(Box.createVerticalStrut(6));

        menu.add(createMenuButton("▤   CA Marks", false));
        menu.add(Box.createVerticalStrut(6));

        menu.add(createMenuButton("▥   Final Marks", false));
        menu.add(Box.createVerticalStrut(6));

        menu.add(createMenuButton("✓   Eligibility", false));
        menu.add(Box.createVerticalStrut(6));

        menu.add(createMenuButton("▦   Course Results", false));
        menu.add(Box.createVerticalStrut(6));

        menu.add(createMenuButton("★   SGPA / CGPA", false));

        // ---------- BOTTOM ----------
        JPanel bottom = new JPanel();
        bottom.setBackground(NAVY);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBorder(new EmptyBorder(10, 15, 25, 15));

        bottom.add(createMenuButton("⚙   Settings", false));
        bottom.add(Box.createVerticalStrut(6));
        bottom.add(createMenuButton("↪   Logout", false));

        sidebar.add(top, BorderLayout.NORTH);
        sidebar.add(menu, BorderLayout.CENTER);
        sidebar.add(bottom, BorderLayout.SOUTH);

        return sidebar;
    }

    // =========================================================
    // MENU BUTTON
    // =========================================================

    private JButton createMenuButton(String text, boolean active) {

        JButton button = new JButton(text);

        button.setMaximumSize(new Dimension(210, 46));
        button.setPreferredSize(new Dimension(210, 46));

        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.setHorizontalAlignment(SwingConstants.LEFT);

        button.setFont(new Font("Arial", Font.BOLD, 13));

        button.setForeground(WHITE);

        button.setBackground(
                active ? BLUE : NAVY
        );

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        10, 18, 10, 10
                )
        );

        button.setFocusPainted(false);

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        button.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {

                if (!active) {
                    button.setBackground(
                            new Color(30, 52, 88)
                    );
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {

                if (!active) {
                    button.setBackground(NAVY);
                }
            }
        });

        return button;
    }

    // =========================================================
    // MAIN CONTENT
    // =========================================================

    private JPanel createMainContent() {

        JPanel main = new JPanel(new BorderLayout());

        main.setBackground(BG);

        main.setBorder(
                new EmptyBorder(
                        28, 32, 28, 32
                )
        );

        // HEADER
        main.add(createHeader(), BorderLayout.NORTH);

        // CENTER
        contentPanel = new JPanel();

        contentPanel.setBackground(BG);

        contentPanel.setLayout(
                new BoxLayout(
                        contentPanel,
                        BoxLayout.Y_AXIS
                )
        );

        contentPanel.add(
                createStatistics()
        );

        contentPanel.add(
                Box.createVerticalStrut(22)
        );

        contentPanel.add(
                createBottomSection()
        );

        main.add(
                contentPanel,
                BorderLayout.CENTER
        );

        return main;
    }

    // =========================================================
    // HEADER
    // =========================================================

    private JPanel createHeader() {

        JPanel header = new JPanel(
                new BorderLayout()
        );

        header.setBackground(BG);

        // LEFT
        JPanel left = new JPanel();

        left.setBackground(BG);

        left.setLayout(
                new BoxLayout(
                        left,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel title =
                new JLabel(
                        "Welcome back, Student 👋"
                );

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        28
                )
        );

        title.setForeground(TEXT);

        JLabel subtitle =
                new JLabel(
                        "Here's an overview of your academic performance."
                );

        subtitle.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        subtitle.setForeground(MUTED);

        left.add(title);

        left.add(
                Box.createVerticalStrut(6)
        );

        left.add(subtitle);

        // RIGHT
        JPanel profile =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                0
                        )
                );

        profile.setBackground(BG);

        JPanel avatar =
                new JPanel(
                        new GridBagLayout()
                );

        avatar.setPreferredSize(
                new Dimension(48, 48)
        );

        avatar.setBackground(
                new Color(219, 234, 254)
        );

        JLabel avatarText =
                new JLabel("ND");

        avatarText.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        avatarText.setForeground(BLUE);

        avatar.add(avatarText);

        JPanel student =
                new JPanel();

        student.setBackground(BG);

        student.setLayout(
                new BoxLayout(
                        student,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel name =
                new JLabel("Nipun D.");

        name.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        JLabel id =
                new JLabel("TG/2124/XXXX");

        id.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        11
                )
        );

        id.setForeground(MUTED);

        student.add(name);
        student.add(id);

        profile.add(avatar);
        profile.add(student);

        header.add(
                left,
                BorderLayout.WEST
        );

        header.add(
                profile,
                BorderLayout.EAST
        );

        return header;
    }

    // =========================================================
    // STATISTICS
    // =========================================================

    private JPanel createStatistics() {

        JPanel stats =
                new JPanel(
                        new GridLayout(
                                1,
                                4,
                                18,
                                0
                        )
                );

        stats.setBackground(BG);

        stats.add(
                createStatCard(
                        "CA Average",
                        "78%",
                        "↑ 5.2%",
                        BLUE
                )
        );

        stats.add(
                createStatCard(
                        "Final Average",
                        "82%",
                        "↑ 8.1%",
                        GREEN
                )
        );

        stats.add(
                createStatCard(
                        "Attendance",
                        "92%",
                        "Good",
                        ORANGE
                )
        );

        stats.add(
                createStatCard(
                        "Current GPA",
                        "3.62",
                        "Excellent",
                        BLUE
                )
        );

        return stats;
    }

    // =========================================================
    // STAT CARD
    // =========================================================

    private JPanel createStatCard(
            String title,
            String value,
            String status,
            Color accent
    ) {

        JPanel card =
                new JPanel(
                        new BorderLayout()
                );

        card.setBackground(WHITE);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER
                        ),
                        new EmptyBorder(
                                18,
                                18,
                                18,
                                18
                        )
                )
        );

        JPanel text =
                new JPanel();

        text.setBackground(WHITE);

        text.setLayout(
                new BoxLayout(
                        text,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel titleLabel =
                new JLabel(title);

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        12
                )
        );

        titleLabel.setForeground(MUTED);

        JLabel valueLabel =
                new JLabel(value);

        valueLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        28
                )
        );

        valueLabel.setForeground(TEXT);

        JLabel statusLabel =
                new JLabel(status);

        statusLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        11
                )
        );

        statusLabel.setForeground(accent);

        text.add(titleLabel);
        text.add(
                Box.createVerticalStrut(7)
        );
        text.add(valueLabel);
        text.add(
                Box.createVerticalStrut(5)
        );
        text.add(statusLabel);

        // Accent bar
        JPanel accentBar =
                new JPanel();

        accentBar.setBackground(accent);

        accentBar.setPreferredSize(
                new Dimension(
                        5,
                        100
                )
        );

        card.add(
                accentBar,
                BorderLayout.WEST
        );

        card.add(
                text,
                BorderLayout.CENTER
        );

        return card;
    }

    // =========================================================
    // BOTTOM SECTION
    // =========================================================

    private JPanel createBottomSection() {

        JPanel bottom =
                new JPanel(
                        new BorderLayout(
                                18,
                                0
                        )
                );

        bottom.setBackground(BG);

        bottom.add(
                createResultsCard(),
                BorderLayout.CENTER
        );

        bottom.add(
                createPerformanceCard(),
                BorderLayout.EAST
        );

        return bottom;
    }

    // =========================================================
    // RESULTS TABLE
    // =========================================================

    private JPanel createResultsCard() {

        JPanel card =
                new JPanel(
                        new BorderLayout(
                                0,
                                12
                        )
                );

        card.setBackground(WHITE);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER
                        ),
                        new EmptyBorder(
                                20,
                                20,
                                20,
                                20
                        )
                )
        );

        JLabel title =
                new JLabel(
                        "Recent Course Results"
                );

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        18
                )
        );

        title.setForeground(TEXT);

        String[] columns = {
                "Course Code",
                "Course Unit",
                "CA",
                "Final",
                "Grade",
                "GPA"
        };

        Object[][] data = {

                {
                        "ICT2122",
                        "Object Oriented Programming",
                        "86",
                        "84",
                        "A",
                        "4.0"
                },

                {
                        "ICT2113",
                        "Data Structures",
                        "78",
                        "81",
                        "A-",
                        "3.7"
                },

                {
                        "ICT2132",
                        "Database Systems",
                        "82",
                        "79",
                        "A-",
                        "3.7"
                },

                {
                        "ICT2142",
                        "Business Economics",
                        "75",
                        "73",
                        "B+",
                        "3.3"
                },

                {
                        "ICT2152",
                        "Computer Networks",
                        "88",
                        "85",
                        "A",
                        "4.0"
                }
        };

        DefaultTableModel model =
                new DefaultTableModel(
                        data,
                        columns
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        JTable table =
                new JTable(model);

        table.setRowHeight(38);

        table.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        12
                )
        );

        table.setForeground(TEXT);

        table.setGridColor(BORDER);

        table.setSelectionBackground(
                LIGHT_BLUE
        );

        table.setSelectionForeground(
                TEXT
        );

        table.getTableHeader()
                .setFont(
                        new Font(
                                "Arial",
                                Font.BOLD,
                                12
                        )
                );

        table.getTableHeader()
                .setBackground(
                        LIGHT_BLUE
                );

        table.getTableHeader()
                .setForeground(TEXT);

        JScrollPane scroll =
                new JScrollPane(table);

        scroll.setBorder(
                BorderFactory.createEmptyBorder()
        );

        card.add(
                title,
                BorderLayout.NORTH
        );

        card.add(
                scroll,
                BorderLayout.CENTER
        );

        return card;
    }

    // =========================================================
    // PERFORMANCE CARD
    // =========================================================

    private JPanel createPerformanceCard() {

        JPanel container =
                new JPanel(
                        new BorderLayout()
                );

        container.setBackground(BG);

        container.setPreferredSize(
                new Dimension(
                        280,
                        350
                )
        );

        JPanel card =
                new JPanel();

        card.setBackground(WHITE);

        card.setLayout(
                new BoxLayout(
                        card,
                        BoxLayout.Y_AXIS
                )
        );

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER
                        ),
                        new EmptyBorder(
                                20,
                                20,
                                20,
                                20
                        )
                )
        );

        JLabel title =
                new JLabel(
                        "Academic Performance"
                );

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        17
                )
        );

        title.setForeground(TEXT);

        JLabel gpa =
                new JLabel(
                        "3.62"
                );

        gpa.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        42
                )
        );

        gpa.setForeground(BLUE);

        JLabel current =
                new JLabel(
                        "Current SGPA"
                );

        current.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        12
                )
        );

        current.setForeground(MUTED);

        JProgressBar progress =
                new JProgressBar(
                        0,
                        400
                );

        progress.setValue(362);

        progress.setString(
                "3.62 / 4.00"
        );

        progress.setStringPainted(true);

        progress.setForeground(BLUE);

        progress.setBackground(
                new Color(
                        226,
                        232,
                        240
                )
        );

        progress.setBorderPainted(false);

        JLabel eligibilityTitle =
                new JLabel(
                        "Semester Eligibility"
                );

        eligibilityTitle.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        eligibilityTitle.setForeground(TEXT);

        JLabel eligible =
                new JLabel(
                        "✓  Eligible"
                );

        eligible.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        20
                )
        );

        eligible.setForeground(GREEN);

        JLabel attendance =
                new JLabel(
                        "Attendance: 92%  •  Required: 80%"
                );

        attendance.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        11
                )
        );

        attendance.setForeground(MUTED);

        card.add(title);

        card.add(
                Box.createVerticalStrut(18)
        );

        card.add(gpa);

        card.add(current);

        card.add(
                Box.createVerticalStrut(18)
        );

        card.add(progress);

        card.add(
                Box.createVerticalStrut(25)
        );

        card.add(eligibilityTitle);

        card.add(
                Box.createVerticalStrut(8)
        );

        card.add(eligible);

        card.add(
                Box.createVerticalStrut(5)
        );

        card.add(attendance);

        container.add(
                card,
                BorderLayout.CENTER
        );

        return container;
    }

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(String[] args) {

        try {

            UIManager.setLookAndFeel(
                    UIManager
                            .getSystemLookAndFeelClassName()
            );

        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {

            MainDashboard dashboard =
                    new MainDashboard();

            dashboard.setVisible(true);
        });
    }
}