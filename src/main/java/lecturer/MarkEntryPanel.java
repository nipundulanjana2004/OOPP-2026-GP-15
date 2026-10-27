/*
 * ====================================================================
 *  PART    : M2 - LECTURER
 *  PURPOSE : Upload marks for ANY component (quiz, mid, assignment, final ...) for the whole batch in one table (FR-18). Final marks are only accepted for ELIGIBLE students.
 *  PLUGS IN: LecturerDashboard -> 'Upload Marks'. Builds CAMark / FinalExamMark objects, calls Lecturer.uploadMark(), eligibility from common.ReportService (rules of M3 attendance + CA).
 *  OOP     : POLYMORPHISM (CAMark / FinalExamMark treated as AssessmentMark), custom exceptions (InvalidMarksException, IneligibleForExamException), inheritance.
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

public class MarkEntryPanel extends CoursePanel {
    private final EvaluationDAO evaluationDAO = new EvaluationDAO();
    private final MarkDAO markDAO = new MarkDAO();
    private final ReportService reports = new ReportService();
    private final JComboBox<EvaluationComponent> componentBox = new JComboBox<>();
    private final TablePanel table = new TablePanel("", "Reg. No", "Name", "Attempt", "Attendance", "CA", "Eligible", "Mark (0-100)");
    private List<CourseResult> results = new ArrayList<>();
    private boolean loading = false;

    public MarkEntryPanel(Lecturer lecturer) {
        super(lecturer, "Upload Marks", "Type marks into the last column, then press Save marks. Final exam marks need eligibility.", false);
        componentBox.setPreferredSize(new Dimension(240, 34));
        bar.add(Theme.label("Component:"));
        bar.add(componentBox);
        RoundedButton save = new RoundedButton("Save marks", RoundedButton.Kind.SUCCESS);
        save.addActionListener(e -> saveMarks());
        bar.add(save);
        table.setEditableColumns(6);
        body.add(table, BorderLayout.CENTER);
        componentBox.addActionListener(e -> { if (!loading) loadRows(); });
    }

    @Override
    protected void refreshData() {
        Course c = selectedCourse();
        loading = true;
        componentBox.removeAllItems();
        try {
            if (c != null) for (EvaluationComponent x : evaluationDAO.findByCourse(c.getCourseId())) componentBox.addItem(x);
            results = c == null ? new ArrayList<>() : reports.resultsForBatch(c);
        } catch (DatabaseException e) {
            Dialogs.handle(this, e);
        }
        loading = false;
        loadRows();
    }

    private void loadRows() {
        EvaluationComponent comp = (EvaluationComponent) componentBox.getSelectedItem();
        List<Object[]> rows = new ArrayList<>();
        try {
            Map<Integer, Double> existing = comp == null ? Map.of() : markDAO.findByComponent(comp.getComponentId());
            for (CourseResult r : results) {
                Double m = existing.get(r.getStudent().getUserId());
                rows.add(new Object[]{r.getStudent().getRegistrationNumber(), r.getStudent().getFullName(), r.getStudent().getAttemptType(),
                        pct(r.getAttendanceOverallPercent()), pct(r.getCaMarks()), r.eligibilityText(),
                        m == null ? "" : (m == Math.rint(m) ? String.valueOf(m.intValue()) : String.valueOf(m))});
            }
        } catch (DatabaseException e) {
            Dialogs.handle(this, e);
        }
        table.setRows(rows);
    }

    private void saveMarks() {
        table.stopEditing();
        EvaluationComponent comp = (EvaluationComponent) componentBox.getSelectedItem();
        if (comp == null) { Dialogs.error(this, "This course has no evaluation components yet. Add them in 'Evaluation Setup'."); return; }
        int saved = 0;
        StringBuilder problems = new StringBuilder();
        for (int i = 0; i < results.size(); i++) {
            Object cell = table.getModel().getValueAt(i, 6);
            String text = cell == null ? "" : cell.toString().trim();
            if (text.isEmpty()) continue;
            CourseResult r = results.get(i);
            try {
                double value;
                try {
                    value = Double.parseDouble(text);
                } catch (NumberFormatException ex) {
                    throw new InvalidMarksException("'" + text + "' is not a number (" + r.getStudent().getRegistrationNumber() + ").");
                }
                AssessmentMark mark;                                    // POLYMORPHISM: parent type, child objects
                if (comp.getComponentType() == ComponentType.FINAL) {
                    FinalExamMark fm = new FinalExamMark(r.getStudent(), comp, value, lecturer.getUserId(), r.isOverallEligible(), r.eligibilityReason());
                    fm.checkEligibility();
                    mark = fm;
                } else {
                    mark = new CAMark(r.getStudent(), comp, value, lecturer.getUserId());
                }
                lecturer.uploadMark(mark);                              // validate() then save
                saved++;
            } catch (AppException e) {
                problems.append("- ").append(e.handle()).append('\n');
            }
        }
        String msg = saved + " mark(s) saved.";
        if (problems.length() > 0) msg += "\n\nNot saved:\n" + problems;
        if (problems.length() > 0) Dialogs.error(this, msg); else Dialogs.info(this, msg);
        refreshData();
    }
}
