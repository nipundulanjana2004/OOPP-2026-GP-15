/*
 * ====================================================================
 *  PART    : M3 - TECHNICAL OFFICER
 *  PURPOSE : Attendance summary of a whole batch or ONE undergraduate for a course: theory, practical and combined, with and without medicals (FR-25).
 *  PLUGS IN: OfficerDashboard -> 'Attendance Summary'. Uses common.ReportService + AttendanceCalculator (the rules of this part) and AttendanceDAO for the per-session detail.
 *  OOP     : Inheritance (extends OfficerCoursePanel), POLYMORPHISM (AttendanceRecord.describe in the detail table).
 * ====================================================================
 */
package officer;

import common.*;
import common.ui.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceSummaryPanel extends OfficerCoursePanel {
    private final JComboBox<Object> studentBox = new JComboBox<>();
    private final TablePanel summary = new TablePanel("Summary (effective % = presence + approved medicals)", "Reg. No", "Name",
            "Theory", "Practical", "Combined", "Combined (presence only)", "Medicals (approved/total)", "Eligible", "Scenario");
    private final TablePanel detail = new TablePanel("Session by session", "Type", "Session", "Date", "Status", "Medical");
    private boolean loadingStudents = false;

    public AttendanceSummaryPanel(TechnicalOfficer officer) {
        super(officer, "Attendance Summary", "Theory, practical and combined attendance with the 80% rule");
        studentBox.setPreferredSize(new Dimension(300, 34));
        bar.add(Theme.label("Show:"));
        bar.add(studentBox);
        JPanel two = Theme.transparent(new GridLayout(2, 1, 0, 14));
        two.add(summary);
        two.add(detail);
        body.add(two, BorderLayout.CENTER);
        studentBox.addActionListener(e -> { if (!loading && !loadingStudents) showTables(); });
    }

    @Override
    protected void refreshData() {
        loadingStudents = true;
        studentBox.removeAllItems();
        studentBox.addItem("All students (whole batch)");
        for (StudentInfo s : students) studentBox.addItem(s);
        loadingStudents = false;
        showTables();
    }

    private void showTables() {
        Course c = selectedCourse();
        List<Object[]> rows = new ArrayList<>();
        List<Object[]> det = new ArrayList<>();
        try {
            if (c != null) {
                Object sel = studentBox.getSelectedItem();
                List<AttendanceSummary> list = new ArrayList<>();
                if (sel instanceof StudentInfo) list.add(new ReportService().attendanceFor((StudentInfo) sel, c));
                else list = officer.attendanceSummary(c);
                for (AttendanceSummary s : list) {
                    rows.add(new Object[]{s.getStudent().getRegistrationNumber(), s.getStudent().getFullName(),
                            c.hasTheory() ? pct(s.effTheory()) : "-", c.hasPractical() ? pct(s.effPractical()) : "-", pct(s.effCombined()),
                            pct(s.rawCombined()), s.getApprovedMedicalCount() + " / " + s.getMedicalCount(),
                            s.isEligible() ? "YES" : "NO", s.scenario()});
                }
                if (sel instanceof StudentInfo) {
                    for (AttendanceRecord r : officer.attendance().findByStudent((StudentInfo) sel, c)) {
                        det.add(new Object[]{r.getSessionLabel(), r.getSession().getSessionNumber(), r.getSession().getSessionDate(),
                                r.getStatus().name(), r.getMedicalStatus().isEmpty() ? "-" : r.getMedicalStatus()});
                    }
                }
            }
        } catch (DatabaseException e) {
            Dialogs.handle(this, e);
        }
        summary.setRows(rows);
        detail.setRows(det);
    }
}
