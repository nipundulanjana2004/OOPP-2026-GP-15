
package undergraduate;

import common.*;
import common.ui.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MyGradesPanel extends UndergraduateViewPanel {
    private final TablePanel grades = new TablePanel("Course results", "Semester", "Course", "Credits", "CA", "Final", "Total", "Grade", "Grade point", "Exam eligibility");
    private final TablePanel gpa = new TablePanel("SGPA / CGPA", "Semester", "Counted credits", "SGPA", "CGPA");
    private final JLabel cgpaLabel = new JLabel("-");
    private List<CourseResult> results = new ArrayList<>();

    public MyGradesPanel(Undergraduate student) {
        super(student, "My Grades", "Grades follow the faculty grade scale. Courses with no final result yet are not counted.");
        Card cg = new Card(new BorderLayout(0, 2));
        cgpaLabel.setFont(Theme.BIG_NUMBER);
        cgpaLabel.setForeground(Theme.ACCENT);
        cg.add(cgpaLabel, BorderLayout.CENTER);
        cg.add(Theme.muted("Current CGPA"), BorderLayout.SOUTH);
        cg.setPreferredSize(new Dimension(180, 100));
        RoundedButton export = new RoundedButton("Export transcript (CSV)", RoundedButton.Kind.SECONDARY);
        export.addActionListener(e -> export());
        JPanel top = Theme.transparent(new BorderLayout(16, 0));
        top.add(cg, BorderLayout.WEST);
        gpa.setPreferredSize(new Dimension(100, 205));
        top.add(gpa, BorderLayout.CENTER);
        grades.addToolbarComponent(export);
        body.add(top, BorderLayout.NORTH);
        body.add(grades, BorderLayout.CENTER);
    }

    @Override
    protected void loadData() throws AppException {
        results = student.viewGrades();
        List<Object[]> rows = new ArrayList<>();
        for (CourseResult r : results) {
            Course c = r.getCourse();
            rows.add(new Object[]{c.getSemester(), c.toString(), c.getTotalCredits(), String.format("%.1f", r.getCaMarks()),
                    r.isFinalPresent() ? String.format("%.0f", r.getFinalExamMarks()) : "-",
                    r.isFinalPresent() && r.isOverallEligible() ? String.format("%.1f", r.getTotalMarks()) : "-",
                    r.gradeText(), r.getGrade() == null ? "-" : r.getGrade().getGradePoint(), r.eligibilityText()});
        }
        grades.setRows(rows);
        List<Object[]> g = new ArrayList<>();
        double cgpa = 0;
        for (SemesterResult s : student.viewGpa()) {
            g.add(new Object[]{s.getSemester(), (int) s.getCredits(), String.format("%.2f", s.getSgpa()), String.format("%.2f", s.getCgpa())});
            cgpa = s.getCgpa();
        }
        gpa.setRows(g);
        cgpaLabel.setText(String.format("%.2f", cgpa));
    }

    private void export() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("transcript_" + student.getRegistrationNumber().replace("/", "_") + ".csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try (PrintWriter w = new PrintWriter(fc.getSelectedFile(), "UTF-8")) {
            w.println("Semester,Course,Credits,CA,Final,Total,Grade,Grade point");
            for (CourseResult r : results) {
                w.println(r.getCourse().getSemester() + ",\"" + r.getCourse() + "\"," + r.getCourse().getTotalCredits() + "," + String.format("%.1f", r.getCaMarks()) + ","
                        + (r.isFinalPresent() ? String.format("%.0f", r.getFinalExamMarks()) : "") + ","
                        + (r.isFinalPresent() && r.isOverallEligible() ? String.format("%.1f", r.getTotalMarks()) : "") + "," + r.gradeText() + ","
                        + (r.getGrade() == null ? "" : r.getGrade().getGradePoint()));
            }
            Dialogs.info(this, "Transcript saved.");
        } catch (java.io.IOException e) {
            Dialogs.error(this, "Could not write the file. Choose another folder.");
        }
    }
}
