/*
 * ====================================================================
 *  PART    : M1 - ADMIN
 *  PURPOSE : Register undergraduates for courses (normal, repeat or batch-missed attempt) so that attendance, marks and timetables work for them.
 *  PLUGS IN: AdminDashboard -> 'Registrations'. M3/M2 list 'the whole batch' from these registrations; M4 'My Courses' reads them.
 *  OOP     : INHERITANCE (extends BasePanel), uses CourseAdminDAO.register / unregister.
 * ====================================================================
 */
package admin;

import common.*;
import common.ui.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class RegistrationPanel extends BasePanel {
    private final Admin admin;
    private final JComboBox<Course> course = new JComboBox<>();
    private final JComboBox<StudentInfo> student = new JComboBox<>();
    private final JComboBox<AttemptType> attempt = Theme.combo(AttemptType.values());
    private final TablePanel table = new TablePanel("Registered undergraduates", "Reg. No", "Name", "Batch", "Dept", "Attempt");
    private List<StudentInfo> registered = new ArrayList<>();
    private boolean loading = false;

    public RegistrationPanel(Admin admin) {
        super("Course Registrations", "Choose a course, then add or remove undergraduates (repeat and batch-missed students included)");
        this.admin = admin;
        JPanel bar = Theme.transparent(new FlowLayout(FlowLayout.LEFT, 10, 0));
        course.setPreferredSize(new Dimension(320, 34));
        student.setPreferredSize(new Dimension(300, 34));
        bar.add(Theme.label("Course:"));
        bar.add(course);
        bar.add(Theme.label("Add undergraduate:"));
        bar.add(student);
        bar.add(attempt);
        RoundedButton add = new RoundedButton("Register");
        RoundedButton remove = new RoundedButton("Remove selected", RoundedButton.Kind.DANGER);
        bar.add(add);
        bar.add(remove);
        body.add(bar, BorderLayout.NORTH);
        body.add(table, BorderLayout.CENTER);
        course.addActionListener(e -> { if (!loading) loadRegistered(); });
        add.addActionListener(e -> register());
        remove.addActionListener(e -> unregister());
    }

    @Override
    public void onShow() {
        try {
            loading = true;
            course.removeAllItems();
            for (Course c : admin.manageCourses().findAll()) course.addItem(c);
            student.removeAllItems();
            for (StudentInfo s : new LookupDAO().allStudents()) student.addItem(s);
            loading = false;
            loadRegistered();
        } catch (AppException e) {
            loading = false;
            Dialogs.handle(this, e);
        }
    }

    private void loadRegistered() {
        Course c = (Course) course.getSelectedItem();
        List<Object[]> rows = new ArrayList<>();
        registered = new ArrayList<>();
        try {
            if (c != null) registered = new LookupDAO().studentsOfCourse(c.getCourseId());
        } catch (DatabaseException e) {
            Dialogs.handle(this, e);
        }
        for (StudentInfo s : registered) rows.add(new Object[]{s.getRegistrationNumber(), s.getFullName(), s.getBatchName(), s.getDepartmentName(), s.getAttemptType()});
        table.setRows(rows);
    }

    private void register() {
        Course c = (Course) course.getSelectedItem();
        StudentInfo s = (StudentInfo) student.getSelectedItem();
        if (c == null || s == null) { Dialogs.error(this, "Choose a course and an undergraduate."); return; }
        try {
            String year = new java.text.SimpleDateFormat("yyyy").format(new Date());
            admin.manageCourses().register(s.getUserId(), c.getCourseId(), year + "/" + (Integer.parseInt(year) + 1), (AttemptType) attempt.getSelectedItem());
            loadRegistered();
        } catch (AppException e) {
            Dialogs.handle(this, e);
        }
    }

    private void unregister() {
        int i = table.getSelectedModelRow();
        Course c = (Course) course.getSelectedItem();
        if (i < 0 || c == null) { Dialogs.error(this, "Select an undergraduate in the table first."); return; }
        if (!Dialogs.confirm(this, "Remove this registration? Existing attendance and marks stay in the database.")) return;
        try {
            admin.manageCourses().unregister(registered.get(i).getUserId(), c.getCourseId());
            loadRegistered();
        } catch (AppException e) {
            Dialogs.handle(this, e);
        }
    }
}
