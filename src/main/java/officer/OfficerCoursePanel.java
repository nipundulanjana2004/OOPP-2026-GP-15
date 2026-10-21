/*
 * ====================================================================
 *  PART    : M3 - TECHNICAL OFFICER
 *  PURPOSE : ABSTRACT base of the officer pages that start with 'choose a course of my department'.
 *  PLUGS IN: AttendanceEntryPanel and AttendanceSummaryPanel extend it.
 *  OOP     : ABSTRACTION (abstract refreshData), INHERITANCE, POLYMORPHISM, TEMPLATE-METHOD.
 * ====================================================================
 */
package officer;

import common.*;
import common.ui.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class OfficerCoursePanel extends BasePanel {
    protected final TechnicalOfficer officer;
    protected final JComboBox<Course> courseBox = new JComboBox<>();
    protected final JPanel bar = Theme.transparent(new FlowLayout(FlowLayout.LEFT, 10, 0));
    protected boolean loading = false;
    protected List<StudentInfo> students = new ArrayList<>();

    protected OfficerCoursePanel(TechnicalOfficer officer, String title, String subtitle) {
        super(title, subtitle);
        this.officer = officer;
        courseBox.setPreferredSize(new Dimension(330, 34));
        bar.add(Theme.label("Course:"));
        bar.add(courseBox);
        body.add(bar, BorderLayout.NORTH);
        courseBox.addActionListener(e -> { if (!loading) { loadStudents(); refreshData(); } });
    }

    protected abstract void refreshData();

    protected Course selectedCourse() {
        return (Course) courseBox.getSelectedItem();
    }

    protected static String pct(double v) {
        return String.format("%.1f%%", v);
    }

    protected void loadStudents() {
        students = new ArrayList<>();
        try {
            if (selectedCourse() != null) students = new LookupDAO().studentsOfCourse(selectedCourse().getCourseId());
        } catch (DatabaseException e) {
            Dialogs.handle(this, e);
        }
    }

    @Override
    public void onShow() {
        try {
            loading = true;
            Course keep = selectedCourse();
            courseBox.removeAllItems();
            for (Course c : officer.departmentCourses()) {
                courseBox.addItem(c);
                if (keep != null && keep.getCourseId() == c.getCourseId()) courseBox.setSelectedItem(c);
            }
            loadStudents();
            loading = false;
            refreshData();
        } catch (DatabaseException e) {
            loading = false;
            Dialogs.handle(this, e);
        }
    }
}
