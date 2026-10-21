
package undergraduate;

import common.*;
import common.ui.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MyAttendancePanel extends UndergraduateViewPanel {
    private final JRadioButton combined = new JRadioButton("Combined", true);
    private final JRadioButton theory = new JRadioButton("Theory");
    private final JRadioButton practical = new JRadioButton("Practical");
    private final TablePanel table = new TablePanel("Attendance by course", "Course", "Presence only", "With approved medicals", "Hours attended / total", "Exam eligible (>=80%)");
    private final TablePanel detail = new TablePanel("Selected course - session by session", "Type", "Session", "Date", "Status", "Medical");
    private List<Course> courses = new ArrayList<>();

    public MyAttendancePanel(Undergraduate student) {
        super(student, "My Attendance", "You need at least 80% attendance (approved medicals count) to sit the final exam");
        ButtonGroup g = new ButtonGroup();
        JPanel bar = Theme.transparent(new FlowLayout(FlowLayout.LEFT, 14, 0));
        bar.add(Theme.label("View:"));
        for (JRadioButton b : new JRadioButton[]{combined, theory, practical}) {
            g.add(b);
            b.setOpaque(false);
            b.setFont(Theme.BASE);
            b.addActionListener(e -> onShow());
            bar.add(b);
        }
        JPanel two = Theme.transparent(new GridLayout(2, 1, 0, 14));
        two.add(table);
        two.add(detail);
        body.add(bar, BorderLayout.NORTH);
        body.add(two, BorderLayout.CENTER);
        table.onSelect(this::showDetail);
    }

    @Override
    protected void loadData() throws AppException {
        List<Object[]> rows = new ArrayList<>();
        courses = new ArrayList<>();
        for (CourseRegistration r : student.viewCourses()) {
            Course c = r.getCourse();
            AttendanceSummary s = student.viewAttendance(c);
            if (!s.hasAnySessions()) continue;                      // older semesters have no attendance records
            AttendanceTally t = theory.isSelected() ? s.getTheory() : practical.isSelected() ? s.getPractical() : s.getCombined();
            if (t.isEmpty()) continue;
            courses.add(c);
            rows.add(new Object[]{c.toString(), pct(AttendanceCalculator.rawPercent(t)), pct(AttendanceCalculator.effectivePercent(t)),
                    (int) (t.getPresentHours() + t.getExcusedHours()) + " / " + (int) t.getTotalHours() + " h",
                    s.isEligible() ? "YES" : "NO"});
        }
        table.setRows(rows);
        detail.setRows(new ArrayList<>());
    }

    private void showDetail() {
        int i = table.getSelectedModelRow();
        if (i < 0 || i >= courses.size()) return;
        try {
            List<Object[]> rows = new ArrayList<>();
            for (Object[] r : student.data().sessionDetails(student.getUserId(), courses.get(i).getCourseId())) {
                boolean isTheory = "THEORY".equals(r[0]);
                if ((theory.isSelected() && !isTheory) || (practical.isSelected() && isTheory)) continue;
                rows.add(r);
            }
            detail.setRows(rows);
        } catch (DatabaseException e) {
            Dialogs.handle(this, e);
        }
    }
}
