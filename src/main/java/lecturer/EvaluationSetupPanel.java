/*
 * ====================================================================
 *  PART    : M2 - LECTURER
 *  PURPOSE : View / add / delete the evaluation components of a course (e.g. Quiz 10%, Mid 20%, Final 60%). Total weightage may not pass 100%.
 *  PLUGS IN: LecturerDashboard -> 'Evaluation Setup'. MarkEntryPanel and common.ReportService use these rows.
 *  OOP     : Inheritance (extends CoursePanel), validation + exception handling.
 * ====================================================================
 */
package lecturer;

import common.*;
import common.ui.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EvaluationSetupPanel extends CoursePanel {
    private final EvaluationDAO dao = new EvaluationDAO();
    private final TablePanel table = new TablePanel("Marking scheme", "Component", "Type", "Weightage %");
    private final JTextField name = Theme.text(14);
    private final JComboBox<ComponentType> type = Theme.combo(ComponentType.values());
    private final JTextField weight = Theme.text(5);
    private final JLabel total = Theme.muted("");
    private List<EvaluationComponent> comps = new ArrayList<>();

    public EvaluationSetupPanel(Lecturer lecturer) {
        super(lecturer, "Evaluation Setup", "How the final mark of a course is built (all marks are entered out of 100)", false);
        body.add(table, BorderLayout.CENTER);
        JPanel add = Theme.transparent(new FlowLayout(FlowLayout.LEFT, 8, 0));
        add.add(Theme.label("Name"));
        add.add(name);
        add.add(type);
        add.add(Theme.label("Weight %"));
        add.add(weight);
        RoundedButton a = new RoundedButton("Add component");
        RoundedButton d = new RoundedButton("Delete selected", RoundedButton.Kind.DANGER);
        a.addActionListener(e -> add());
        d.addActionListener(e -> delete());
        add.add(a);
        add.add(d);
        add.add(total);
        body.add(add, BorderLayout.SOUTH);
    }

    @Override
    protected void refreshData() {
        Course c = selectedCourse();
        List<Object[]> rows = new ArrayList<>();
        comps = new ArrayList<>();
        double sum = 0;
        try {
            if (c != null) comps = dao.findByCourse(c.getCourseId());
        } catch (DatabaseException e) {
            Dialogs.handle(this, e);
        }
        for (EvaluationComponent x : comps) {
            rows.add(new Object[]{x.getComponentName(), x.getComponentType(), x.getWeightage()});
            sum += x.getWeightage();
        }
        table.setRows(rows);
        total.setText("Total weightage: " + sum + "%");
    }

    private void add() {
        try {
            Course c = selectedCourse();
            if (c == null) throw new ValidationException("Choose a course first.");
            if (name.getText().trim().isEmpty()) throw new ValidationException("Component name is required.");
            double w;
            try {
                w = Double.parseDouble(weight.getText().trim());
            } catch (NumberFormatException ex) {
                throw new ValidationException("Weightage must be a number.");
            }
            double sum = 0;
            for (EvaluationComponent x : comps) sum += x.getWeightage();
            if (w <= 0 || sum + w > 100) throw new ValidationException("Weightage must be > 0 and the total may not exceed 100% (now " + sum + "%).");
            dao.insert(c.getCourseId(), name.getText().trim(), (ComponentType) type.getSelectedItem(), w);
            name.setText("");
            weight.setText("");
            refreshData();
        } catch (AppException e) {
            Dialogs.handle(this, e);
        }
    }

    private void delete() {
        int i = table.getSelectedModelRow();
        if (i < 0) { Dialogs.error(this, "Select a component first."); return; }
        if (!Dialogs.confirm(this, "Delete this component and all marks entered for it?")) return;
        try {
            dao.delete(comps.get(i).getComponentId());
            refreshData();
        } catch (AppException e) {
            Dialogs.handle(this, e);
        }
    }
}
