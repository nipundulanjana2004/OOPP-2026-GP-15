/*
 * ====================================================================
 *  PART    : M3 - TECHNICAL OFFICER
 *  PURPOSE : Record, edit, delete, APPROVE or REJECT medicals of the department's undergraduates (FR-24). An approved medical makes the covered absences count as attendance.
 *  PLUGS IN: OfficerDashboard -> 'Medicals'. Uses TechnicalOfficer.recordMedical()/verifyMedical() -> MedicalDAO.
 *  OOP     : Inheritance (extends BasePanel), validation, custom exception (InvalidMedicalException).
 * ====================================================================
 */
package officer;

import common.*;
import common.ui.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalPanel extends BasePanel {
    private final TechnicalOfficer officer;
    private final TablePanel table = new TablePanel("", "Reg. No", "Name", "From", "To", "Reason", "Status", "Verified by");
    private final JComboBox<StudentInfo> student = new JComboBox<>();
    private final JTextField from = Theme.text(18);
    private final JTextField to = Theme.text(18);
    private final JTextField reason = Theme.text(18);
    private final JTextField doc = Theme.text(18);
    private List<Medical> items = new ArrayList<>();

    public MedicalPanel(TechnicalOfficer officer) {
        super("Medicals", "Medical certificates of your department's undergraduates");
        this.officer = officer;
        FormPanel f = new FormPanel();
        f.addRow("Undergraduate", student);
        f.addRow("From (yyyy-MM-dd)", from);
        f.addRow("To (yyyy-MM-dd)", to);
        f.addRow("Reason", reason);
        JPanel docRow = Theme.transparent(new BorderLayout(6, 0));
        docRow.add(doc, BorderLayout.CENTER);
        RoundedButton browse = new RoundedButton("Browse", RoundedButton.Kind.SECONDARY);
        browse.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) doc.setText(fc.getSelectedFile().getPath());
        });
        docRow.add(browse, BorderLayout.EAST);
        f.addRow("Certificate file (optional)", docRow);
        RoundedButton add = new RoundedButton("Record");
        RoundedButton upd = new RoundedButton("Update", RoundedButton.Kind.SECONDARY);
        RoundedButton del = new RoundedButton("Delete", RoundedButton.Kind.DANGER);
        RoundedButton ok = new RoundedButton("Approve", RoundedButton.Kind.SUCCESS);
        RoundedButton no = new RoundedButton("Reject", RoundedButton.Kind.DANGER);
        add.addActionListener(e -> save(false));
        upd.addActionListener(e -> save(true));
        del.addActionListener(e -> delete());
        ok.addActionListener(e -> verify(true));
        no.addActionListener(e -> verify(false));
        f.addButtons(add, upd, del);
        f.addButtons(ok, no);
        f.finish();
        Card side = new Card(new BorderLayout(0, 8));
        side.add(Theme.heading("Medical details"), BorderLayout.NORTH);
        side.add(f, BorderLayout.CENTER);
        side.setPreferredSize(new Dimension(370, 100));
        body.add(table, BorderLayout.CENTER);
        body.add(side, BorderLayout.EAST);
        table.onSelect(() -> {
            int i = table.getSelectedModelRow();
            if (i < 0 || i >= items.size()) return;
            Medical m = items.get(i);
            from.setText(m.getDateFrom().toString());
            to.setText(m.getDateTo().toString());
            reason.setText(m.getReason());
            doc.setText(m.getDocumentPath());
            for (int k = 0; k < student.getItemCount(); k++) if (student.getItemAt(k).getUserId() == m.getStudent().getUserId()) student.setSelectedIndex(k);
        });
    }

    @Override
    public void onShow() {
        try {
            student.removeAllItems();
            for (StudentInfo s : officer.medicals().studentsOfDepartment(officer.getDepartmentId())) student.addItem(s);
            items = officer.medicals().findByDepartment(officer.getDepartmentId());
            List<Object[]> rows = new ArrayList<>();
            for (Medical m : items) {
                rows.add(new Object[]{m.getStudent().getRegistrationNumber(), m.getStudent().getFullName(), m.getDateFrom(), m.getDateTo(),
                        m.getReason(), m.getApprovalStatus().name(), m.getVerifiedBy()});
            }
            table.setRows(rows);
        } catch (DatabaseException e) {
            Dialogs.handle(this, e);
        }
    }

    private Medical readForm(Medical m) throws AppException {
        m.setStudent((StudentInfo) student.getSelectedItem());
        try {
            m.setDateFrom(java.sql.Date.valueOf(from.getText().trim()));
            m.setDateTo(java.sql.Date.valueOf(to.getText().trim()));
        } catch (IllegalArgumentException e) {
            throw new InvalidMedicalException("Dates must look like 2026-08-14.");
        }
        m.setReason(reason.getText().trim());
        m.setDocumentPath(doc.getText().trim());
        return m;
    }

    private void save(boolean update) {
        try {
            int linked;
            if (update) {
                int i = table.getSelectedModelRow();
                if (i < 0) throw new InvalidMedicalException("Select a medical in the table first.");
                linked = officer.medicals().update(readForm(items.get(i)));
            } else {
                linked = officer.recordMedical(readForm(new Medical()));
            }
            Dialogs.info(this, "Saved. " + linked + " absent session(s) were linked to this medical.\nIt counts as attendance only after you APPROVE it.");
            onShow();
        } catch (AppException e) {
            Dialogs.handle(this, e);
        }
    }

    private void verify(boolean approve) {
        int i = table.getSelectedModelRow();
        if (i < 0) { Dialogs.error(this, "Select a medical in the table first."); return; }
        try {
            officer.verifyMedical(items.get(i), approve);
            onShow();
        } catch (AppException e) {
            Dialogs.handle(this, e);
        }
    }

    private void delete() {
        int i = table.getSelectedModelRow();
        if (i < 0) { Dialogs.error(this, "Select a medical in the table first."); return; }
        if (!Dialogs.confirm(this, "Delete this medical? Linked absences will count as plain absences again.")) return;
        try {
            officer.medicals().delete(items.get(i).getMedicalId());
            onShow();
        } catch (AppException e) {
            Dialogs.handle(this, e);
        }
    }
}
