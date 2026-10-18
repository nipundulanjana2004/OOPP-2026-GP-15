/*
 * ====================================================================
 *  PART    : M2 - LECTURER
 *  PURPOSE : Details of the undergraduates registered for a course: reg. no, name, batch, department, attempt, contact (FR-19).
 *  PLUGS IN: LecturerDashboard -> 'Undergraduates'. Uses Lecturer.viewUndergraduateDetails().
 *  OOP     : Inheritance (extends CoursePanel).
 * ====================================================================
 */
package lecturer;

import common.*;
import common.ui.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UndergraduateDetailsPanel extends CoursePanel {
    private final TablePanel table = new TablePanel("", "Reg. No", "Name", "Batch", "Department", "Attempt", "E-mail", "Contact");

    public UndergraduateDetailsPanel(Lecturer lecturer) {
        super(lecturer, "Undergraduates", "Students registered for your course (repeat and batch-missed students included)", false);
        body.add(table, BorderLayout.CENTER);
    }

    @Override
    protected void refreshData() {
        List<Object[]> rows = new ArrayList<>();
        try {
            if (selectedCourse() != null) {
                for (StudentInfo s : lecturer.viewUndergraduateDetails(selectedCourse())) {
                    rows.add(new Object[]{s.getRegistrationNumber(), s.getFullName(), s.getBatchName(), s.getDepartmentName(),
                            s.getAttemptType(), s.getEmail(), s.getContactNumber()});
                }
            }
        } catch (DatabaseException e) {
            Dialogs.handle(this, e);
        }
        table.setRows(rows);
    }
}
