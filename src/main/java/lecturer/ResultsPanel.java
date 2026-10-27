/*
 * ====================================================================
 *  PART    : M2 - LECTURER
 *  PURPOSE : CA marks, final marks, total + grade, and SGPA / CGPA for the whole batch or one undergraduate (FR-22). Can save a results snapshot into course_result / semester_result.
 *  PLUGS IN: LecturerDashboard -> 'Results & GPA'. Uses common.ReportService + GradeCalculator (UGC grade scale from MySQL) and ResultDAO.
 *  OOP     : Inheritance (extends CoursePanel); composition (JTabbedPane of TablePanels).
 * ====================================================================
 */
package lecturer;

import common.*;
import common.ui.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResultsPanel extends CoursePanel {
    private final ReportService reports = new ReportService();
    private final EvaluationDAO evaluationDAO = new EvaluationDAO();
    private final MarkDAO markDAO = new MarkDAO();
    private final JTabbedPane tabs = new JTabbedPane();
    private final JPanel caHolder = Theme.transparent(new BorderLayout());
    private final JPanel finalHolder = Theme.transparent(new BorderLayout());
    private final TablePanel overall = new TablePanel("", "Reg. No", "Name", "CA (of 100)", "Final (of 100)", "Total", "Grade", "Grade point", "Exam eligibility");
    private final TablePanel gpa = new TablePanel("SGPA / CGPA", "Reg. No", "Name", "Semester", "SGPA", "CGPA", "Counted credits");
    private final TablePanel gpaCourses = new TablePanel("Courses and grades", "Reg. No", "Course", "Semester", "Credits", "Grade", "Grade point");

    public ResultsPanel(Lecturer lecturer) {
        super(lecturer, "Results & GPA", "Marks, grades, SGPA and CGPA - whole batch or one student", true);
        RoundedButton publish = new RoundedButton("Save results snapshot", RoundedButton.Kind.SUCCESS);
        publish.addActionListener(e -> publish());
        bar.add(publish);
        JPanel gpaTab = Theme.transparent(new GridLayout(2, 1, 0, 12));
        gpaTab.add(gpa);
        gpaTab.add(gpaCourses);
        tabs.addTab("CA marks", caHolder);
        tabs.addTab("Final marks", finalHolder);
        tabs.addTab("Total & grade", overall);
        tabs.addTab("SGPA / CGPA", gpaTab);
        tabs.setFont(Theme.BOLD);
        body.add(tabs, BorderLayout.CENTER);
    }

    private static String num(Double d) {
        return d == null ? "-" : (d == Math.rint(d) ? String.valueOf(d.intValue()) : String.format("%.1f", d));
    }

    /** One table per component group (CA or FINAL): one column per component. */
    private TablePanel componentTable(Course c, ComponentType type, List<CourseResult> list) throws DatabaseException {
        List<EvaluationComponent> comps = new ArrayList<>();
        for (EvaluationComponent x : evaluationDAO.findByCourse(c.getCourseId())) if (x.getComponentType() == type) comps.add(x);
        String[] cols = new String[comps.size() + 3];
        cols[0] = "Reg. No";
        cols[1] = "Name";
        for (int i = 0; i < comps.size(); i++) cols[i + 2] = comps.get(i).toString();
        cols[cols.length - 1] = type == ComponentType.CA ? "CA (of 100)" : "Final (of 100)";
        TablePanel t = new TablePanel("", cols);
        List<Map<Integer, Double>> marks = new ArrayList<>();
        for (EvaluationComponent x : comps) marks.add(markDAO.findByComponent(x.getComponentId()));
        List<Object[]> rows = new ArrayList<>();
        for (CourseResult r : list) {
            Object[] row = new Object[cols.length];
            row[0] = r.getStudent().getRegistrationNumber();
            row[1] = r.getStudent().getFullName();
            for (int i = 0; i < comps.size(); i++) row[i + 2] = num(marks.get(i).get(r.getStudent().getUserId()));
            row[cols.length - 1] = type == ComponentType.CA ? String.format("%.1f", r.getCaMarks()) : (r.isFinalPresent() ? num(r.getFinalExamMarks()) : "-");
            rows.add(row);
        }
        t.setRows(rows);
        return t;
    }

    private List<CourseResult> currentResults() throws DatabaseException {
        Course c = selectedCourse();
        List<CourseResult> list = new ArrayList<>();
        if (c == null) return list;
        if (selectedStudent() == null) list = reports.resultsForBatch(c);
        else list.add(reports.resultFor(selectedStudent(), c));
        return list;
    }

    @Override
    protected void refreshData() {
        Course c = selectedCourse();
        try {
            List<CourseResult> list = currentResults();
            caHolder.removeAll();
            finalHolder.removeAll();
            if (c != null) {
                caHolder.add(componentTable(c, ComponentType.CA, list), BorderLayout.CENTER);
                finalHolder.add(componentTable(c, ComponentType.FINAL, list), BorderLayout.CENTER);
            }
            caHolder.revalidate();
            finalHolder.revalidate();
            caHolder.repaint();
            finalHolder.repaint();

            List<Object[]> o = new ArrayList<>();
            List<Object[]> g = new ArrayList<>();
            List<Object[]> gc = new ArrayList<>();
            for (CourseResult r : list) {
                o.add(new Object[]{r.getStudent().getRegistrationNumber(), r.getStudent().getFullName(), String.format("%.1f", r.getCaMarks()),
                        r.isFinalPresent() ? num(r.getFinalExamMarks()) : "-", r.isFinalPresent() && r.isOverallEligible() ? String.format("%.1f", r.getTotalMarks()) : "-",
                        r.gradeText(), r.getGrade() == null ? "-" : r.getGrade().getGradePoint(), r.eligibilityText()});
                List<SemesterResult> sems = reports.semesterResults(r.getStudent());
                for (SemesterResult s : sems) {
                    if (selectedStudent() == null && s.getSemester() != c.getSemester()) continue;
                    g.add(new Object[]{s.getStudent().getRegistrationNumber(), s.getStudent().getFullName(), s.getSemester(),
                            String.format("%.2f", s.getSgpa()), String.format("%.2f", s.getCgpa()), (int) s.getCredits()});
                }
                for (CourseResult x : reports.resultsForStudent(r.getStudent())) {
                    gc.add(new Object[]{x.getStudent().getRegistrationNumber(), x.getCourse().toString(), x.getCourse().getSemester(),
                            x.getCourse().getTotalCredits(), x.gradeText(), x.getGrade() == null ? "-" : x.getGrade().getGradePoint()});
                }
            }
            overall.setRows(o);
            gpa.setRows(g);
            gpaCourses.setRows(gc);
        } catch (DatabaseException e) {
            Dialogs.handle(this, e);
        }
    }

    private void publish() {
        try {
            Course c = selectedCourse();
            if (c == null) return;
            int n = new ResultDAO().publish(reports.resultsForBatch(c), reports);
            Dialogs.info(this, "Results of " + n + " undergraduates for " + c.getCourseCode() + " were saved (course_result + semester_result).");
        } catch (AppException e) {
            Dialogs.handle(this, e);
        }
    }
}
