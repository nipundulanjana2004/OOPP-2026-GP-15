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

        JPanel row1 = Theme.transparent(new FlowLayout(FlowLayout.LEFT, 10, 0));
        course.setPreferredSize(new Dimension(320, 34));
        student.setPreferredSize(new Dimension(300, 34));
        row1.add(Theme.label("Course:"));
        row1.add(course);
        row1.add(Theme.label("Add undergraduate:"));
        row1.add(student);


        JPanel row2 = Theme.transparent(new FlowLayout(FlowLayout.LEFT, 10, 0));
        RoundedButton add = new RoundedButton("Register");
        RoundedButton remove = new RoundedButton("Remove selected", RoundedButton.Kind.DANGER);
        row2.add(Theme.label("Attempt:"));
        row2.add(attempt);
        row2.add(add);
        row2.add(remove);

        JPanel bar = Theme.transparent(new GridLayout(0, 1, 0, 8));
        bar.add(row1);
        bar.add(row2);

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

        if (c == null || s == null) {
            Dialogs.error(this, "Choose a course and an undergraduate.");
            return;
        }

        try {
            String year = new java.text.SimpleDateFormat("yyyy").format(new Date());

            admin.manageCourses().register(
                    s.getUserId(),
                    c.getCourseId(),
                    year + "/" + (Integer.parseInt(year) + 1),
                    (AttemptType) attempt.getSelectedItem()
            );
            JOptionPane.showMessageDialog(
                    this,
                    "Student successfully registered for the course!",
                    "Registration Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );

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