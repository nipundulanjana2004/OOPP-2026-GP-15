/*
 * ====================================================================
 *  PART    : M2 - LECTURER
 *  PURPOSE : READ-ONLY attendance (theory / practical / combined) and medical records of the students of a course (FR-21). The data is entered by the Technical Officer (M3).
 *  PLUGS IN: LecturerDashboard -> 'Attendance & Medicals'. Uses Lecturer.viewAttendance() and common.ReportService.medicalsOfCourse().
 *  OOP     : Inheritance (extends CoursePanel); reads data written by another member's module.
 * ====================================================================
 */
package lecturer;

import common.*;
import common.ui.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceMedicalPanel extends CoursePanel {
    private final JComboBox<String> view = Theme.combo(new String[]{"Combined", "Theory", "Practical"});
    private final TablePanel attendance = new TablePanel("Attendance", "Reg. No", "Name", "Presence only", "With approved medicals", "Eligible (>=80%)", "Scenario");
    private final TablePanel medicals = new TablePanel("Medicals", "Reg. No", "Name", "From", "To", "Reason", "Status", "Verified by");

    public AttendanceMedicalPanel(Lecturer lecturer) {
        super(lecturer, "Attendance & Medicals", "Read-only view of what the Technical Officer recorded", true);
        bar.add(Theme.label("Attendance type:"));
        bar.add(view);
        view.addActionListener(e -> refreshData());
        JPanel two = Theme.transparent(new GridLayout(2, 1, 0, 14));
        two.add(attendance);
        two.add(medicals);
        body.add(two, BorderLayout.CENTER);
    }

    @Override
    protected void refreshData() {
        Course c = selectedCourse();
        List<Object[]> a = new ArrayList<>();
        List<Object[]> m = new ArrayList<>();
        try {
            if (c != null) {
                List<AttendanceSummary> list = new ArrayList<>();
                if (selectedStudent() == null) list = lecturer.viewAttendance(c);
                else list.add(new ReportService().attendanceFor(selectedStudent(), c));
                String v = (String) view.getSelectedItem();
                for (AttendanceSummary s : list) {
                    double raw = "Theory".equals(v) ? s.rawTheory() : "Practical".equals(v) ? s.rawPractical() : s.rawCombined();
                    double eff = "Theory".equals(v) ? s.effTheory() : "Practical".equals(v) ? s.effPractical() : s.effCombined();
                    a.add(new Object[]{s.getStudent().getRegistrationNumber(), s.getStudent().getFullName(), pct(raw), pct(eff),
                            AttendanceCalculator.isEligible(eff, s.hasAnySessions()) ? "YES" : "NO", s.scenario()});
                }
                for (MedicalInfo x : new ReportService().medicalsOfCourse(c.getCourseId())) {
                    if (selectedStudent() != null && !x.getRegistrationNumber().equals(selectedStudent().getRegistrationNumber())) continue;
                    m.add(new Object[]{x.getRegistrationNumber(), x.getStudentName(), x.getDateFrom(), x.getDateTo(), x.getReason(), x.getStatus(), x.getVerifiedBy()});
                }
            }
        } catch (DatabaseException e) {
            Dialogs.handle(this, e);
        }
        attendance.setRows(a);
        medicals.setRows(m);
    }
}
