
package undergraduate;

import common.*;
import common.ui.*;
import officer.InvalidMedicalException;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MyMedicalsPanel extends UndergraduateViewPanel {
    private final TablePanel table = new TablePanel("", "Course", "From", "To", "Reason", "Status", "Verified by");
    private final JLabel summary = Theme.muted("");
    private final JComboBox<Course> course = new JComboBox<>();
    private final JTextField from = Theme.text(18);
    private final JTextField to = Theme.text(18);
    private final JTextField reason = Theme.text(18);

    public MyMedicalsPanel(Undergraduate student) {
        super(student, "My Medicals", "Request a medical here and hand the paper certificate to your Technical Officer. Approved medicals count towards attendance.");
        FormPanel f = new FormPanel();
        f.addRow("Course", course);
        f.addRow("From (yyyy-MM-dd)", from);
        f.addRow("To (yyyy-MM-dd)", to);
        f.addRow("Reason", reason);
        JLabel note = Theme.muted("<html>The medical certificate itself is NOT uploaded here. Give the<br>original paper medical to the Technical Officer.<br>Your request stays PENDING until the officer<br>checks it and approves or rejects it.</html>");
        f.addRow("Note:", note);
        RoundedButton submit = new RoundedButton("Submit request");
        submit.addActionListener(e -> submitRequest());
        f.addButtons(submit);
        f.finish();

        Card side = new Card(new BorderLayout(0, 8));
        side.add(Theme.heading("New medical request"), BorderLayout.NORTH);
        side.add(f, BorderLayout.CENTER);
        side.setPreferredSize(new Dimension(370, 100));

        JPanel main = Theme.transparent(new BorderLayout(0, 10));
        main.add(summary, BorderLayout.NORTH);
        main.add(table, BorderLayout.CENTER);
        body.add(main, BorderLayout.CENTER);
        body.add(side, BorderLayout.EAST);
        resetForm();
    }

    private void resetForm() {
        String today = LocalDate.now().toString();
        from.setText(today);
        to.setText(today);
        reason.setText("");
    }

    private void submitRequest() {
        try {
            java.sql.Date d1;
            java.sql.Date d2;
            try {
                d1 = java.sql.Date.valueOf(from.getText().trim());
                d2 = java.sql.Date.valueOf(to.getText().trim());
            } catch (IllegalArgumentException e) {
                throw new InvalidMedicalException("Dates must look like 2026-08-14.");
            }
            student.submitMedicalRequest((Course) course.getSelectedItem(), d1, d2, reason.getText());
            Dialogs.info(this, "Your medical request was sent to the Technical Officer.\n"
                    + "Please hand your original paper medical certificate to the Technical Officer.\n"
                    + "It counts towards attendance only after the officer approves it.");
            resetForm();
            onShow();
        } catch (AppException e) {
            Dialogs.handle(this, e);
        }
    }

    @Override
    protected void loadData() throws AppException {
        Course chosen = (Course) course.getSelectedItem();
        course.removeAllItems();
        for (CourseRegistration r : student.viewCourses()) {
            course.addItem(r.getCourse());
            if (chosen != null && chosen.getCourseId() == r.getCourse().getCourseId()) course.setSelectedIndex(course.getItemCount() - 1);
        }
        List<Object[]> rows = new ArrayList<>();
        int ok = 0, pending = 0, rejected = 0;
        for (MedicalInfo m : student.viewMedicals()) {
            rows.add(new Object[]{m.getCourseCode(), m.getDateFrom(), m.getDateTo(), m.getReason(), m.getStatus(), m.getVerifiedBy()});
            if ("APPROVED".equals(m.getStatus())) ok++; else if ("PENDING".equals(m.getStatus())) pending++; else rejected++;
        }
        summary.setText("Approved: " + ok + "     Pending: " + pending + "     Rejected: " + rejected);
        table.setRows(rows);
    }
}
