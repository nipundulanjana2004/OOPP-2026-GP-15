/*
 * ====================================================================
 *  PART    : M1 - ADMIN
 *  PURPOSE : Create / update / delete courses (FR-09) and assign lecturers to them (CourseAssignment).
 *  PLUGS IN: AdminDashboard -> 'Courses'. Lecturers (M2) later see only their assigned courses; undergraduates (M4) see registered ones.
 *  OOP     : INHERITANCE (extends ManagementPanel<Course>), composition (extra assignment table), validation.
 * ====================================================================
 */
package admin;

import common.*;
import common.ui.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class CourseManagementPanel extends ManagementPanel<Course> {
    private final Admin admin;
    private final JTextField code = Theme.text(18);
    private final JTextField name = Theme.text(18);
    private final JSpinner theory = new JSpinner(new SpinnerNumberModel(2, 0, 6, 1));
    private final JSpinner practical = new JSpinner(new SpinnerNumberModel(0, 0, 6, 1));
    private final JSpinner semester = new JSpinner(new SpinnerNumberModel(3, 1, 8, 1));
    private final JComboBox<Department> department = new JComboBox<>();
    private final JComboBox<User> lecturer = new JComboBox<>();
    private final JComboBox<String> roleInCourse = Theme.combo(new String[]{"Coordinator", "Lecturer"});
    private final TablePanel assignTable = new TablePanel("Lecturers of the selected course", "Lecturer", "Role in course");
    private List<CourseAssignment> assignments = new ArrayList<>();

    public CourseManagementPanel(Admin admin) {
        super("Courses", "Create and maintain course modules and their lecturers", "Course details",
                "Code", "Course name", "Theory", "Practical", "Semester", "Department", "Lecturers");
        this.admin = admin;
        init();
    }

    @Override
    protected void buildForm(FormPanel f) {
        f.addRow("Course code", code);
        f.addRow("Course name", name);
        f.addRow("Theory credits", theory);
        f.addRow("Practical credits", practical);
        f.addRow("Semester (3 = Level II Sem I)", semester);
        f.addRow("Department", department);
    }

    @Override
    protected JComponent createExtras() {
        JPanel p = Theme.transparent(new BorderLayout(0, 8));
        assignTable.setPreferredSize(new Dimension(100, 170));
        p.add(assignTable, BorderLayout.CENTER);
        JPanel bar = Theme.transparent(new FlowLayout(FlowLayout.LEFT, 8, 0));
        lecturer.setPreferredSize(new Dimension(230, 34));
        bar.add(lecturer);
        bar.add(roleInCourse);
        RoundedButton assign = new RoundedButton("Assign");
        assign.addActionListener(e -> run(this::assignLecturer));
        RoundedButton remove = new RoundedButton("Remove", RoundedButton.Kind.DANGER);
        remove.addActionListener(e -> run(this::removeLecturer));
        bar.add(assign);
        bar.add(remove);
        p.add(bar, BorderLayout.SOUTH);
        return p;
    }

    @Override
    protected List<Course> loadItems() throws AppException {
        LookupDAO lookup = new LookupDAO();
        department.removeAllItems();
        for (Department d : lookup.departments()) department.addItem(d);
        lecturer.removeAllItems();
        for (User u : admin.manageUsers().findByRole(Role.LECTURER)) lecturer.addItem(u);
        return admin.manageCourses().findAll();
    }

    @Override
    protected Object[] toRow(Course c) {
        String lecs = "";
        try { lecs = admin.manageCourses().lecturerNames(c.getCourseId()); } catch (DatabaseException e) { lecs = "?"; }
        return new Object[]{c.getCourseCode(), c.getCourseName(), c.getTheoryCredits(), c.getPracticalCredits(), c.getSemester(),
                c.getDepartment().getDepartmentCode(), lecs};
    }

    @Override
    protected void fillForm(Course c) {
        code.setText(c.getCourseCode());
        name.setText(c.getCourseName());
        theory.setValue(c.getTheoryCredits());
        practical.setValue(c.getPracticalCredits());
        semester.setValue(c.getSemester());
        for (int i = 0; i < department.getItemCount(); i++) {
            if (department.getItemAt(i).getDepartmentId() == c.getDepartment().getDepartmentId()) department.setSelectedIndex(i);
        }
    }

    @Override
    protected void onSelectionChanged(Course c) {
        loadAssignments(c);
    }

    private void loadAssignments(Course c) {
        List<Object[]> rows = new ArrayList<>();
        assignments = new ArrayList<>();
        if (c != null) {
            try {
                assignments = admin.manageCourses().findAssignments(c);
            } catch (DatabaseException e) {
                Dialogs.handle(this, e);
            }
        }
        for (CourseAssignment a : assignments) rows.add(new Object[]{a.getLecturerName(), a.getRoleInCourse()});
        assignTable.setRows(rows);
    }

    @Override
    protected void clearForm() {
        code.setText("");
        name.setText("");
        theory.setValue(2);
        practical.setValue(0);
        semester.setValue(3);
    }

    private Course readForm(Course c) throws AppException {
        c.setCourseCode(need(code.getText(), "Course code").toUpperCase());
        c.setCourseName(need(name.getText(), "Course name"));
        c.setTheoryCredits(intIn(theory));
        c.setPracticalCredits(intIn(practical));
        c.setSemester(intIn(semester));
        if (c.getTheoryCredits() + c.getPracticalCredits() == 0) throw new ValidationException("A course needs at least one credit.");
        if (department.getSelectedItem() == null) throw new ValidationException("Please choose a department.");
        c.setDepartment((Department) department.getSelectedItem());
        return c;
    }

    @Override
    protected void doAdd() throws AppException {
        admin.manageCourses().insert(readForm(new Course()));
    }

    @Override
    protected void doUpdate(Course c) throws AppException {
        admin.manageCourses().update(readForm(c));
    }

    @Override
    protected void doDelete(Course c) throws AppException {
        admin.manageCourses().delete(c.getCourseId());
    }

    private void assignLecturer() throws AppException {
        if (selected == null) throw new ValidationException("Select a course first.");
        User l = (User) lecturer.getSelectedItem();
        if (l == null) throw new ValidationException("There are no lecturers to assign.");
        admin.manageCourses().assign(selected.getCourseId(), l.getUserId(), (String) roleInCourse.getSelectedItem());
        loadAssignments(selected);
        reloadKeepSelection();
    }

    private void removeLecturer() throws AppException {
        int i = assignTable.getSelectedModelRow();
        if (selected == null || i < 0) throw new ValidationException("Select a lecturer in the small table first.");
        admin.manageCourses().unassign(assignments.get(i).getAssignmentId());
        loadAssignments(selected);
        reloadKeepSelection();
    }

    private void reloadKeepSelection() throws AppException {
        Course keep = selected;
        reload();
        selected = keep;
    }
}
