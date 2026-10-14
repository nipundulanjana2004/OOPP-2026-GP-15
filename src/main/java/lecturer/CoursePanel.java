/*
 * ====================================================================
 *  PART    : M2 - LECTURER
 *  PURPOSE : ABSTRACT base of the lecturer pages that work on 'one of my courses' (+ optionally 'one student or the whole batch').
 *  PLUGS IN: MarkEntryPanel, EvaluationSetupPanel, UndergraduateDetailsPanel, EligibilityPanel, ResultsPanel, AttendanceMedicalPanel extend it.
 *  OOP     : ABSTRACTION (abstract refreshData), INHERITANCE, POLYMORPHISM, TEMPLATE-METHOD: combo loading written once.
 * ====================================================================
 */
package lecturer;

import common.*;
import common.ui.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class CoursePanel extends BasePanel {
    protected static final String WHOLE_BATCH = "All students (whole batch)";
    protected final Lecturer lecturer;
    protected final JComboBox<Course> courseBox = new JComboBox<>();
    protected final JComboBox<Object> studentBox = new JComboBox<>();
    protected final JPanel bar = Theme.transparent(new FlowLayout(FlowLayout.LEFT, 10, 0));
    protected List<StudentInfo> students = new ArrayList<>();
    private final boolean withStudent;
    private boolean loading = false;

    protected CoursePanel(Lecturer lecturer, String title, String subtitle, boolean withStudent) {
        super(title, subtitle);
        this.lecturer = lecturer;
        this.withStudent = withStudent;
        courseBox.setPreferredSize(new Dimension(330, 34));
        studentBox.setPreferredSize(new Dimension(300, 34));
        bar.add(Theme.label("Course:"));
        bar.add(courseBox);
        if (withStudent) {
            bar.add(Theme.label("Show:"));
            bar.add(studentBox);
        }
        body.add(bar, BorderLayout.NORTH);
        courseBox.addActionListener(e -> { if (!loading) { loadStudents(); refreshData(); } });
        studentBox.addActionListener(e -> { if (!loading) refreshData(); });
    }

    /** Called whenever the course / student choice changes or the page is opened. */
    protected abstract void refreshData();

    protected Course selectedCourse() {
        return (Course) courseBox.getSelectedItem();
    }

    /** null = whole batch. */
    protected StudentInfo selectedStudent() {
        Object o = studentBox.getSelectedItem();
        return o instanceof StudentInfo ? (StudentInfo) o : null;
    }

    protected static String pct(double v) {
        return String.format("%.1f%%", v);
    }

    private void loadStudents() {
        students = new ArrayList<>();
        studentBox.removeAllItems();
        studentBox.addItem(WHOLE_BATCH);
        Course c = selectedCourse();
//        if (c == null) return;
//        try {
//            students = new LookupDAO().studentsOfCourse(c.getCourseId());
//        } catch (DatabaseException e) {
//            Dialogs.handle(this, e);
//        }
//        if (withStudent) for (StudentInfo s : students) studentBox.addItem(s);
    }

    @Override
    public void onShow() {
        try {
            loading = true;
            Course keep = selectedCourse();
            courseBox.removeAllItems();
            for (Course c : lecturer.myCourses()) {
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
