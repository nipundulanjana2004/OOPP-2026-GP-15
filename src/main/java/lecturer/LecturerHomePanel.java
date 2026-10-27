/*
 * ====================================================================
 *  PART    : M2 - LECTURER
 *  PURPOSE : 'My Courses' overview: the courses assigned to this lecturer (by the Admin, M1) with credits and number of registered undergraduates.
 *  PLUGS IN: First page of LecturerDashboard.
 *  OOP     : Inheritance (extends BasePanel).
 * ====================================================================
 */
package lecturer;

import common.*;
import common.ui.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LecturerHomePanel extends BasePanel {
    private final Lecturer lecturer;
    private final TablePanel table = new TablePanel("My courses", "Code", "Course", "Theory", "Practical", "Semester", "Department", "Undergraduates");

    public LecturerHomePanel(Lecturer lecturer) {
        super("Welcome, " + lecturer.getFirstName(), lecturer.getRoleSummary());
        this.lecturer = lecturer;
        body.add(table, BorderLayout.CENTER);
    }

    @Override
    public void onShow() {
        try {
            List<Object[]> rows = new ArrayList<>();
            LookupDAO lookup = new LookupDAO();
            for (Course c : lecturer.myCourses()) {
                rows.add(new Object[]{c.getCourseCode(), c.getCourseName(), c.getTheoryCredits(), c.getPracticalCredits(), c.getSemester(),
                        c.getDepartment().getDepartmentCode(), lookup.studentsOfCourse(c.getCourseId()).size()});
            }
            table.setRows(rows);
        } catch (DatabaseException e) {
            Dialogs.handle(this, e);
        }
    }
}
