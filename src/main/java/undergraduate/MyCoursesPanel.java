
package undergraduate;

import common.*;
import common.ui.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MyCoursesPanel extends UndergraduateViewPanel {
    private final TablePanel table = new TablePanel("", "Code", "Course", "Theory", "Practical", "Credits", "Semester", "Year", "Attempt", "Lecturer(s)");

    public MyCoursesPanel(Undergraduate student) {
        super(student, "My Courses", "Modules you are registered for");
        body.add(table, BorderLayout.CENTER);
        table.setColumnWidth(1, 260);
    }

    @Override
    protected void loadData() throws AppException {
        List<Object[]> rows = new ArrayList<>();
        CourseDAO courses = new CourseDAO();
        for (CourseRegistration r : student.viewCourses()) {
            Course c = r.getCourse();
            rows.add(new Object[]{c.getCourseCode(), c.getCourseName(), c.getTheoryCredits(), c.getPracticalCredits(), c.getTotalCredits(),
                    c.getSemester(), r.getAcademicYear(), r.getAttemptType().name(), courses.lecturerNames(c.getCourseId())});
        }
        table.setRows(rows);
    }
}
