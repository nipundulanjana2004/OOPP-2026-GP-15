/*
 * ====================================================================
 *  PART    : M4 - UNDERGRADUATE
 *  PURPOSE : Dashboard: CGPA, registered courses, attendance alerts (courses under 80%) and medical counts.
 *  PLUGS IN: First page of UndergraduateDashboard.
 *  OOP     : Inheritance (extends UndergraduateViewPanel).
 * ====================================================================
 */
package undergraduate;

import common.*;
import common.ui.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UndergraduateHomePanel extends UndergraduateViewPanel {
    private final JPanel cards = Theme.transparent(new GridLayout(1, 4, 16, 0));
    private final TablePanel alerts = new TablePanel("Attendance alerts (below 80%)", "Course", "Attendance (with medicals)", "Presence only", "Status");

    public UndergraduateHomePanel(Undergraduate student) {
        super(student, "Welcome, " + student.getFirstName(), student.getRoleSummary());
        body.add(cards, BorderLayout.NORTH);
        body.add(alerts, BorderLayout.CENTER);
    }

    private Card stat(String label, String value, Color color) {
        Card c = new Card(new BorderLayout(0, 4));
        JLabel n = new JLabel(value);
        n.setFont(Theme.BIG_NUMBER);
        n.setForeground(color);
        c.add(n, BorderLayout.CENTER);
        c.add(Theme.muted(label), BorderLayout.SOUTH);
        c.setPreferredSize(new Dimension(180, 100));
        return c;
    }

    @Override
    protected void loadData() throws AppException {
        List<Object[]> rows = new ArrayList<>();
        int courses = 0;
        for (CourseRegistration r : student.viewCourses()) {
            courses++;
            AttendanceSummary s = student.viewAttendance(r.getCourse());
            if (s.hasAnySessions() && !s.isEligible()) {
                rows.add(new Object[]{r.getCourse().toString(), pct(s.effCombined()), pct(s.rawCombined()), "NOT ELIGIBLE"});
            }
        }
        alerts.setRows(rows);
        double cgpa = 0;
        for (SemesterResult s : student.viewGpa()) cgpa = s.getCgpa();
        int pending = 0;
        for (MedicalInfo m : student.viewMedicals()) if ("PENDING".equals(m.getStatus())) pending++;
        cards.removeAll();
        cards.add(stat("CGPA", String.format("%.2f", cgpa), Theme.ACCENT));
        cards.add(stat("Registered courses", String.valueOf(courses), Theme.SUCCESS));
        cards.add(stat("Attendance alerts", String.valueOf(rows.size()), rows.isEmpty() ? Theme.SUCCESS : Theme.DANGER));
        cards.add(stat("Pending medicals", String.valueOf(pending), Theme.WARNING));
        cards.revalidate();
        cards.repaint();
    }
}
