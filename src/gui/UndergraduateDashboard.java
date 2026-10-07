package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UndergraduateDashboard extends JFrame {

    // =========================================================
    // COLORS
    // =========================================================

    private static final Color NAVY = new Color(18, 38, 63);
    private static final Color BLUE = new Color(37, 99, 235);
    private static final Color LIGHT_BLUE = new Color(239, 246, 255);
    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLACK = Color.BLACK;
    private static final Color BORDER = new Color(229, 231, 235);


    // =========================================================
    // MAIN PANELS
    // =========================================================

    private JPanel contentPanel;
    private CardLayout cardLayout;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public UndergraduateDashboard() {

        setTitle("University Of Ruhuna Faculty Of Technology Learning Management System");
        setSize(1280, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        getContentPane().setBackground(BACKGROUND);

        JPanel sidebar = createSidebar();
        JPanel mainContent = createMainContent();

        add(sidebar, BorderLayout.WEST);
        add(mainContent, BorderLayout.CENTER);
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    private JPanel createSidebar() {

        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(235, 750));
        sidebar.setBackground(NAVY);
        sidebar.setLayout(new BorderLayout());

        // -----------------------------------------------------
        // LOGO
        // -----------------------------------------------------

        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(NAVY);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBorder(new EmptyBorder(25, 20, 20, 20));

        JLabel campusName = new JLabel("University Of Ruhuna");
        campusName.setForeground(WHITE);
        campusName.setFont(new Font("Arial", Font.BOLD, 14));
        campusName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel systemSubName = new JLabel("Faculty Of Technology");
        systemSubName.setForeground(new Color(147, 197, 253));
        systemSubName.setFont(new Font("Arial", Font.PLAIN, 14));
        systemSubName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roleLabel = new JLabel("Undergraduate");
        roleLabel.setForeground(new Color(191, 219, 254));
        roleLabel.setFont(new Font("Arial", Font.BOLD, 11));
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        logoPanel.add(campusName);
        logoPanel.add(Box.createVerticalStrut(2));
        logoPanel.add(systemSubName);
        logoPanel.add(Box.createVerticalStrut(12));
        logoPanel.add(roleLabel);

        sidebar.add(logoPanel, BorderLayout.NORTH);

        // -----------------------------------------------------
        // MENU
        // -----------------------------------------------------

        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(NAVY);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(new EmptyBorder(5, 12, 10, 12));

        addMenuButton(menuPanel, "Dashboard", "dashboard");
        addMenuButton(menuPanel, "Profile", "profile");
        addMenuButton(menuPanel, "My Courses", "courses");
        addMenuButton(menuPanel, "Attendance", "attendance");
        addMenuButton(menuPanel, "Medical", "medical");
        addMenuButton(menuPanel, "My Marks", "marks");
        addMenuButton(menuPanel, "Grades", "grades");
        addMenuButton(menuPanel, "SGPA / CGPA", "gpa");
        addMenuButton(menuPanel, "Timetable", "timetable");
        addMenuButton(menuPanel, "Notices", "notices");

        sidebar.add(menuPanel, BorderLayout.CENTER);

        // -----------------------------------------------------
        // LOGOUT
        // -----------------------------------------------------

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(NAVY);
        bottomPanel.setBorder(new EmptyBorder(10, 12, 20, 12));

        JButton logoutButton = createMenuButton("Logout");

        logoutButton.addActionListener(e -> {

            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION
            );

            if (result == JOptionPane.YES_OPTION) {

                dispose();

                JOptionPane.showMessageDialog(
                        null,
                        "Logged out successfully."
                );
            }
        });

        bottomPanel.add(logoutButton, BorderLayout.CENTER);

        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    // =========================================================
    // ADD MENU BUTTON
    // =========================================================

    private void addMenuButton(
            JPanel menuPanel,
            String text,
            String cardName
    ) {

        JButton button = createMenuButton(text);

        button.addActionListener(e ->
                cardLayout.show(contentPanel, cardName)
        );

        menuPanel.add(button);
        menuPanel.add(Box.createVerticalStrut(5));
    }

    // =========================================================
    // MENU BUTTON DESIGN
    // BLACK TEXT + WHITE BACKGROUND + 5PX RADIUS
    // =========================================================

    private JButton createMenuButton(String text) {

        JButton button = new JButton(text) {

            @Override
            protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                // Rounded rectangle background
                g2.setColor(getBackground());

                g2.fillRoundRect(
                        0,
                        0,
                        getWidth(),
                        getHeight(),
                        5,
                        5
                );

                g2.dispose();

                super.paintComponent(g);
            }
        };

        button.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 42)
        );

        button.setPreferredSize(
                new Dimension(205, 42)
        );

        button.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        button.setFont(
                new Font("Arial", Font.PLAIN, 14)
        );

        // =====================================================
        // BLACK TEXT
        // =====================================================

        button.setForeground(BLACK);

        // White normal background
        button.setBackground(WHITE);

        // =====================================================
        // REMOVE DEFAULT BUTTON STYLE
        // =====================================================

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        15,
                        0,
                        10
                )
        );

        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        // =====================================================
        // HOVER EFFECT
        // =====================================================

        button.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent e
                    ) {

                        button.setBackground(
                                LIGHT_BLUE
                        );

                        button.repaint();
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e
                    ) {

                        button.setBackground(
                                WHITE
                        );

                        button.repaint();
                    }
                }
        );

        return button;
    }

    // =========================================================
    // MAIN CONTENT
    // =========================================================

    private JPanel createMainContent() {

        JPanel mainPanel = new JPanel(
                new BorderLayout()
        );

        mainPanel.setBackground(BACKGROUND);

        JPanel header = createHeader();

        cardLayout = new CardLayout();

        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BACKGROUND);

        contentPanel.add(
                createDashboardPanel(),
                "dashboard"
        );

        contentPanel.add(
                createProfilePanel(),
                "profile"
        );

        contentPanel.add(
                createCoursesPanel(),
                "courses"
        );

        contentPanel.add(
                createAttendancePanel(),
                "attendance"
        );

        contentPanel.add(
                createMedicalPanel(),
                "medical"
        );

        contentPanel.add(
                createMarksPanel(),
                "marks"
        );

        contentPanel.add(
                createGradesPanel(),
                "grades"
        );

        contentPanel.add(
                createGPAPanel(),
                "gpa"
        );

        contentPanel.add(
                createTimetablePanel(),
                "timetable"
        );

        contentPanel.add(
                createNoticesPanel(),
                "notices"
        );

        mainPanel.add(
                header,
                BorderLayout.NORTH
        );

        mainPanel.add(
                contentPanel,
                BorderLayout.CENTER
        );

        return mainPanel;
    }

    // =========================================================
    // HEADER
    // =========================================================

    private JPanel createHeader() {

        JPanel header = new JPanel(
                new BorderLayout()
        );

        header.setBackground(WHITE);

        header.setBorder(
                BorderFactory.createMatteBorder(
                        0,
                        0,
                        1,
                        0,
                        BORDER
                )
        );

        JLabel title = new JLabel(
                "Undergraduate Dashboard"
        );

        title.setFont(
                new Font("Arial", Font.BOLD, 22)
        );

        title.setForeground(BLACK);

        title.setBorder(
                new EmptyBorder(
                        20,
                        25,
                        20,
                        20
                )
        );

        JLabel student = new JLabel(
                "TG/2024/2094"
        );

        student.setFont(
                new Font("Arial", Font.PLAIN, 13)
        );

        student.setForeground(BLACK);

        student.setBorder(
                new EmptyBorder(
                        20,
                        20,
                        20,
                        25
                )
        );

        header.add(
                title,
                BorderLayout.WEST
        );

        header.add(
                student,
                BorderLayout.EAST
        );

        return header;
    }

    // =========================================================
    // DASHBOARD
    // =========================================================

    private JPanel createDashboardPanel() {

        JPanel panel = basePanel();

        JLabel welcome = new JLabel(
                "Welcome, Undergraduate"
        );

        welcome.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        welcome.setForeground(BLACK);

        JLabel subtitle = new JLabel(
                "View your academic information, results and university activities."
        );

        subtitle.setFont(
                new Font("Arial", Font.PLAIN, 14)
        );

        subtitle.setForeground(BLACK);

        JPanel titlePanel = new JPanel();

        titlePanel.setBackground(BACKGROUND);

        titlePanel.setLayout(
                new BoxLayout(
                        titlePanel,
                        BoxLayout.Y_AXIS
                )
        );

        titlePanel.add(welcome);

        titlePanel.add(
                Box.createVerticalStrut(5)
        );

        titlePanel.add(subtitle);

        panel.add(
                titlePanel,
                BorderLayout.NORTH
        );

        // -----------------------------------------------------
        // STAT CARDS
        // -----------------------------------------------------

        JPanel cards = new JPanel(
                new GridLayout(
                        1,
                        4,
                        15,
                        15
                )
        );

        cards.setBackground(BACKGROUND);

        cards.setBorder(
                new EmptyBorder(
                        25,
                        0,
                        20,
                        0
                )
        );

        cards.add(
                createStatCard(
                        "My Courses",
                        "06"
                )
        );

        cards.add(
                createStatCard(
                        "Attendance",
                        "87%"
                )
        );

        cards.add(
                createStatCard(
                        "Current SGPA",
                        "3.42"
                )
        );

        cards.add(
                createStatCard(
                        "Current Semester",
                        "04"
                )
        );

        panel.add(
                cards,
                BorderLayout.CENTER
        );

        return panel;
    }

    // =========================================================
    // STAT CARD
    // =========================================================

    private JPanel createStatCard(
            String title,
            String value
    ) {

        JPanel card = new JPanel();

        card.setBackground(WHITE);

        card.setLayout(
                new BorderLayout()
        );

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(
                                18,
                                18,
                                18,
                                18
                        )
                )
        );

        JLabel titleLabel = new JLabel(title);

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        titleLabel.setForeground(BLACK);

        JLabel valueLabel = new JLabel(value);

        valueLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        28
                )
        );

        valueLabel.setForeground(BLACK);

        card.add(
                titleLabel,
                BorderLayout.NORTH
        );

        card.add(
                valueLabel,
                BorderLayout.CENTER
        );

        return card;
    }

    // =========================================================
    // PROFILE
    // =========================================================

    private JPanel createProfilePanel() {

        JPanel panel = basePanel();

        panel.add(
                createPageTitle(
                        "My Profile",
                        "View and update your personal information."
                ),
                BorderLayout.NORTH
        );

        JPanel form = new JPanel(
                new GridLayout(
                        6,
                        2,
                        15,
                        15
                )
        );

        form.setBackground(WHITE);

        form.setBorder(
                new EmptyBorder(
                        25,
                        25,
                        25,
                        25
                )
        );

        addFormField(
                form,
                "Student ID",
                "TG/2024/2094",
                false
        );

        addFormField(
                form,
                "Full Name",
                "Student Name",
                true
        );

        addFormField(
                form,
                "Email",
                "student@university.lk",
                true
        );

        addFormField(
                form,
                "Phone",
                "07XXXXXXXX",
                true
        );

        addFormField(
                form,
                "Degree",
                "BICT",
                false
        );

        addFormField(
                form,
                "Semester",
                "04",
                false
        );

        panel.add(
                form,
                BorderLayout.CENTER
        );

        return panel;
    }

    // =========================================================
    // FORM FIELD
    // =========================================================

    private void addFormField(
            JPanel panel,
            String label,
            String value,
            boolean editable
    ) {

        JLabel lbl = new JLabel(label);

        lbl.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        lbl.setForeground(BLACK);

        JTextField field = new JTextField(value);

        field.setForeground(BLACK);

        field.setEditable(editable);

        if (!editable) {

            field.setBackground(
                    new Color(243, 244, 246)
            );
        }

        panel.add(lbl);
        panel.add(field);
    }

    // =========================================================
    // COURSES
    // =========================================================

    private JPanel createCoursesPanel() {

        JPanel panel = basePanel();

        panel.add(
                createPageTitle(
                        "My Courses",
                        "Courses registered for the current semester."
                ),
                BorderLayout.NORTH
        );

        String[] columns = {
                "Course Code",
                "Course Name",
                "Credits",
                "Semester"
        };

        Object[][] data = {
                {"ICT2113", "Data Structures & Algorithms", "3", "04"},
                {"ICT2122", "Object Oriented Programming", "3", "04"},
                {"ICT2132", "Software Engineering", "3", "04"},
                {"ICT2142", "Database Management Systems", "3", "04"},
                {"ICT2152", "Web Technologies", "2", "04"}
        };

        JTable table = createTable(
                data,
                columns
        );

        panel.add(
                new JScrollPane(table),
                BorderLayout.CENTER
        );

        return panel;
    }

    // =========================================================
    // ATTENDANCE
    // =========================================================

    private JPanel createAttendancePanel() {

        JPanel panel = basePanel();

        panel.add(
                createPageTitle(
                        "My Attendance",
                        "View your theory and practical attendance."
                ),
                BorderLayout.NORTH
        );

        String[] columns = {
                "Course Code",
                "Course",
                "Theory %",
                "Practical %",
                "Overall %"
        };

        Object[][] data = {
                {"ICT2113", "DSA", "85%", "90%", "87%"},
                {"ICT2122", "OOP", "90%", "88%", "89%"},
                {"ICT2132", "SE", "82%", "85%", "83%"},
                {"ICT2142", "DBMS", "88%", "91%", "89%"}
        };

        JTable table = createTable(
                data,
                columns
        );

        panel.add(
                new JScrollPane(table),
                BorderLayout.CENTER
        );

        return panel;
    }

    // =========================================================
    // MEDICAL
    // =========================================================

    private JPanel createMedicalPanel() {

        JPanel panel = basePanel();

        panel.add(
                createPageTitle(
                        "Medical Status",
                        "View submitted medical records and their status."
                ),
                BorderLayout.NORTH
        );

        String[] columns = {
                "Date",
                "Course Code",
                "Reason",
                "Status"
        };

        Object[][] data = {
                {
                        "2026-09-10",
                        "ICT2113",
                        "Medical Leave",
                        "Approved"
                },
                {
                        "2026-09-18",
                        "ICT2122",
                        "Medical Leave",
                        "Pending"
                }
        };

        JTable table = createTable(
                data,
                columns
        );

        panel.add(
                new JScrollPane(table),
                BorderLayout.CENTER
        );

        return panel;
    }

    // =========================================================
    // MARKS
    // =========================================================

    private JPanel createMarksPanel() {

        JPanel panel = basePanel();

        panel.add(
                createPageTitle(
                        "My Marks",
                        "View your CA and final examination marks."
                ),
                BorderLayout.NORTH
        );

        String[] columns = {
                "Course Code",
                "Course",
                "CA Marks",
                "Final Marks",
                "Overall"
        };

        Object[][] data = {
                {"ICT2113", "DSA", "42", "38", "80"},
                {"ICT2122", "OOP", "45", "40", "85"},
                {"ICT2132", "SE", "40", "35", "75"},
                {"ICT2142", "DBMS", "43", "42", "85"}
        };

        JTable table = createTable(
                data,
                columns
        );

        panel.add(
                new JScrollPane(table),
                BorderLayout.CENTER
        );

        return panel;
    }

    // =========================================================
    // GRADES
    // =========================================================

    private JPanel createGradesPanel() {

        JPanel panel = basePanel();

        panel.add(
                createPageTitle(
                        "My Grades",
                        "View course grades and grade points."
                ),
                BorderLayout.NORTH
        );

        String[] columns = {
                "Course Code",
                "Course",
                "Overall",
                "Grade",
                "Grade Point"
        };

        Object[][] data = {
                {"ICT2113", "DSA", "80", "A-", "3.70"},
                {"ICT2122", "OOP", "85", "A", "4.00"},
                {"ICT2132", "SE", "75", "B+", "3.30"},
                {"ICT2142", "DBMS", "85", "A", "4.00"}
        };

        JTable table = createTable(
                data,
                columns
        );

        panel.add(
                new JScrollPane(table),
                BorderLayout.CENTER
        );

        return panel;
    }

    // =========================================================
    // GPA
    // =========================================================

    private JPanel createGPAPanel() {

        JPanel panel = basePanel();

        panel.add(
                createPageTitle(
                        "SGPA / CGPA",
                        "View your semester GPA and overall CGPA."
                ),
                BorderLayout.NORTH
        );

        JPanel gpaPanel = new JPanel(
                new GridLayout(
                        1,
                        2,
                        20,
                        20
                )
        );

        gpaPanel.setBackground(BACKGROUND);

        gpaPanel.setBorder(
                new EmptyBorder(
                        30,
                        30,
                        30,
                        30
                )
        );

        gpaPanel.add(
                createStatCard(
                        "Semester 04 SGPA",
                        "3.42"
                )
        );

        gpaPanel.add(
                createStatCard(
                        "Overall CGPA",
                        "3.35"
                )
        );

        panel.add(
                gpaPanel,
                BorderLayout.CENTER
        );

        return panel;
    }

    // =========================================================
    // TIMETABLE
    // =========================================================

    private JPanel createTimetablePanel() {

        JPanel panel = basePanel();

        panel.add(
                createPageTitle(
                        "Timetable",
                        "View your weekly academic timetable."
                ),
                BorderLayout.NORTH
        );

        String[] columns = {
                "Day",
                "Time",
                "Course",
                "Type",
                "Venue"
        };

        Object[][] data = {
                {
                        "Monday",
                        "08:00 - 10:00",
                        "ICT2122",
                        "Lecture",
                        "Lab 01"
                },
                {
                        "Tuesday",
                        "10:00 - 12:00",
                        "ICT2113",
                        "Lecture",
                        "Hall 02"
                },
                {
                        "Wednesday",
                        "08:00 - 10:00",
                        "ICT2142",
                        "Practical",
                        "Lab 03"
                },
                {
                        "Thursday",
                        "13:00 - 15:00",
                        "ICT2132",
                        "Lecture",
                        "Hall 01"
                },
                {
                        "Friday",
                        "09:00 - 11:00",
                        "ICT2122",
                        "Practical",
                        "Lab 02"
                }
        };

        JTable table = createTable(
                data,
                columns
        );

        panel.add(
                new JScrollPane(table),
                BorderLayout.CENTER
        );

        return panel;
    }

    // =========================================================
    // NOTICES
    // =========================================================

    private JPanel createNoticesPanel() {

        JPanel panel = basePanel();

        panel.add(
                createPageTitle(
                        "University Notices",
                        "Latest notices and announcements."
                ),
                BorderLayout.NORTH
        );

        JPanel noticeList = new JPanel();

        noticeList.setBackground(BACKGROUND);

        noticeList.setLayout(
                new BoxLayout(
                        noticeList,
                        BoxLayout.Y_AXIS
                )
        );

        noticeList.add(
                createNotice(
                        "Semester Examination Timetable",
                        "The final examination timetable has been released.",
                        "2026-10-05"
                )
        );

        noticeList.add(
                Box.createVerticalStrut(10)
        );

        noticeList.add(
                createNotice(
                        "Registration Notice",
                        "Course registration for the next semester is now open.",
                        "2026-10-02"
                )
        );

        noticeList.add(
                Box.createVerticalStrut(10)
        );

        noticeList.add(
                createNotice(
                        "Library Notice",
                        "Library opening hours have been updated.",
                        "2026-09-28"
                )
        );

        panel.add(
                new JScrollPane(noticeList),
                BorderLayout.CENTER
        );

        return panel;
    }

    // =========================================================
    // NOTICE
    // =========================================================

    private JPanel createNotice(
            String title,
            String description,
            String date
    ) {

        JPanel notice = new JPanel(
                new BorderLayout()
        );

        notice.setBackground(WHITE);

        notice.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(
                                15,
                                15,
                                15,
                                15
                        )
                )
        );

        JLabel titleLabel = new JLabel(title);

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        15
                )
        );

        titleLabel.setForeground(BLACK);

        JLabel descriptionLabel = new JLabel(
                description
        );

        descriptionLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        descriptionLabel.setForeground(BLACK);

        JLabel dateLabel = new JLabel(date);

        dateLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        12
                )
        );

        dateLabel.setForeground(BLACK);

        JPanel center = new JPanel();

        center.setBackground(WHITE);

        center.setLayout(
                new BoxLayout(
                        center,
                        BoxLayout.Y_AXIS
                )
        );

        center.add(titleLabel);

        center.add(
                Box.createVerticalStrut(5)
        );

        center.add(descriptionLabel);

        notice.add(
                center,
                BorderLayout.CENTER
        );

        notice.add(
                dateLabel,
                BorderLayout.EAST
        );

        notice.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        80
                )
        );

        return notice;
    }

    // =========================================================
    // PAGE TITLE
    // =========================================================

    private JPanel createPageTitle(
            String title,
            String subtitle
    ) {

        JPanel panel = new JPanel();

        panel.setBackground(BACKGROUND);

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        panel.setBorder(
                new EmptyBorder(
                        5,
                        0,
                        20,
                        0
                )
        );

        JLabel titleLabel = new JLabel(title);

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        23
                )
        );

        titleLabel.setForeground(BLACK);

        JLabel subtitleLabel = new JLabel(subtitle);

        subtitleLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        subtitleLabel.setForeground(BLACK);

        panel.add(titleLabel);

        panel.add(
                Box.createVerticalStrut(5)
        );

        panel.add(subtitleLabel);

        return panel;
    }

    // =========================================================
    // BASE PANEL
    // =========================================================

    private JPanel basePanel() {

        JPanel panel = new JPanel(
                new BorderLayout()
        );

        panel.setBackground(BACKGROUND);

        panel.setBorder(
                new EmptyBorder(
                        25,
                        25,
                        25,
                        25
                )
        );

        return panel;
    }

    // =========================================================
    // TABLE
    // =========================================================

    private JTable createTable(
            Object[][] data,
            String[] columns
    ) {

        JTable table = new JTable(
                data,
                columns
        );

        table.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        table.setForeground(BLACK);

        table.setRowHeight(35);

        table.getTableHeader().setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        table.getTableHeader().setBackground(NAVY);
        table.getTableHeader().setForeground(WHITE);

        table.setGridColor(BORDER);

        table.setSelectionBackground(
                LIGHT_BLUE
        );

        table.setSelectionForeground(BLACK);

        return table;
    }

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {

                UIManager.setLookAndFeel(
                        UIManager
                                .getSystemLookAndFeelClassName()
                );

            } catch (Exception e) {

                e.printStackTrace();
            }

            UndergraduateDashboard dashboard =
                    new UndergraduateDashboard();

            dashboard.setVisible(true);
        });
    }
}