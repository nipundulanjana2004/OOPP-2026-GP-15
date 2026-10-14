/*
 * ====================================================================
 *  PART    : M2 - LECTURER
 *  PURPOSE : Who may sit the final exam? Attendance >= 80% (medicals counted) AND CA >= 40%. Whole batch or one student (FR-20).
 *  PLUGS IN: LecturerDashboard -> 'Eligibility'. Uses Lecturer.viewEligibility() -> common.ReportService -> AttendanceCalculator (M3 data) + CA rule.
 *  OOP     : Inheritance (extends CoursePanel).
 * ====================================================================
 */
package lecturer;

import common.*;
import common.ui.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EligibilityPanel extends CoursePanel {
    private final TablePanel table = new TablePanel("", "Reg. No", "Name", "Theory att.", "Practical att.", "Overall att.", "CA %",
            "Attendance", "CA", "Exam eligibility", "Reason");
    private final JLabel summary = Theme.muted("");

    public EligibilityPanel(Lecturer lecturer) {
        super(lecturer, "Exam Eligibility", "Attendance must be at least 80% (approved medicals count) and CA at least 40%", true);
        bar.add(summary);
        body.add(table, BorderLayout.CENTER);
        table.setColumnWidth(0, 110);
        table.setColumnWidth(8, 120);
        table.setColumnWidth(9, 230);
    }

    @Override
    protected void refreshData() {
        List<Object[]> rows = new ArrayList<>();
        int eligible = 0;
        try {
            Course c = selectedCourse();
//            if (c != null) {
//                List<CourseResult> list = new ArrayList<>();
//                if (selectedStudent() == null) list = lecturer.viewEligibility(c);
//                else list.add(new ReportService().resultFor(selectedStudent(), c));
//                for (CourseResult r : list) {
//                    if (r.isOverallEligible()) eligible++;
//                    rows.add(new Object[]{r.getStudent().getRegistrationNumber(), r.getStudent().getFullName(),
//                            c.hasTheory() ? pct(r.getAttendanceTheoryPercent()) : "-", c.hasPractical() ? pct(r.getAttendancePracticalPercent()) : "-",
//                            pct(r.getAttendanceOverallPercent()), pct(r.getCaMarks()),
//                            r.isAttendanceEligible() ? "YES" : "NO", r.isCaEligible() ? "YES" : "NO", r.eligibilityText(), r.eligibilityReason()});
//                }
                summary.setText("Eligible: " + eligible + " of " + rows.size());
            }
        } catch (DatabaseException e) {
            Dialogs.handle(this, e);
        }
        table.setRows(rows);
    }
}
