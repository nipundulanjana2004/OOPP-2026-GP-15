/*
 * ====================================================================
 *  PART    : M3 - TECHNICAL OFFICER
 *  PURPOSE : Record attendance of a whole class for one session: tick PRESENT, untick absentees, Save (FR-23). Can generate the 15 sessions of a course. Absences covered by a medical are linked automatically.
 *  PLUGS IN: OfficerDashboard -> 'Record Attendance'. Builds TheoryAttendance / PracticalAttendance objects, calls TechnicalOfficer.recordAttendance() -> AttendanceDAO.
 *  OOP     : POLYMORPHISM (AttendanceRecord.create picks the child), inheritance (extends OfficerCoursePanel), exceptions.
 * ====================================================================
 */
package officer;

import common.*;
import common.ui.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttendanceEntryPanel extends OfficerCoursePanel {
    private final JComboBox<SessionType> typeBox = new JComboBox<>(SessionType.values());
    private final JComboBox<Session> sessionBox = new JComboBox<>();
    private final TablePanel table = new TablePanel("", "Reg. No", "Name", "Attempt", "Present", "Medical");
    private List<AttendanceRecord> rows = new ArrayList<>();

    public AttendanceEntryPanel(TechnicalOfficer officer) {
        super(officer, "Record Attendance", "Choose course, type and session. Tick = present. Absent students with a medical are linked automatically.");
        typeBox.setPreferredSize(new Dimension(120, 34));
        sessionBox.setPreferredSize(new Dimension(260, 34));
        bar.add(typeBox);
        bar.add(sessionBox);
        RoundedButton gen = new RoundedButton("Generate 15 sessions", RoundedButton.Kind.SECONDARY);
        RoundedButton all = new RoundedButton("All present", RoundedButton.Kind.SECONDARY);
        RoundedButton save = new RoundedButton("Save attendance", RoundedButton.Kind.SUCCESS);
        gen.addActionListener(e -> generate());
        all.addActionListener(e -> setAll(true));
        save.addActionListener(e -> save());
        bar.add(gen);
        bar.add(all);
        bar.add(save);
        table.setEditableColumns(3);
        table.setColumnWidth(0, 120);
        body.add(table, BorderLayout.CENTER);
        typeBox.addActionListener(e -> { if (!loading) loadSessions(); });
        sessionBox.addActionListener(e -> { if (!loading) loadRows(); });
    }

    @Override
    protected void refreshData() {
        loading = true;
        loadSessions0();
        loading = false;
        loadRows();
    }

    private void loadSessions() {
        loading = true;
        loadSessions0();
        loading = false;
        loadRows();
    }

    private void loadSessions0() {
        sessionBox.removeAllItems();
        Course c = selectedCourse();
        if (c == null) return;
        try {
            for (Session s : officer.sessions().find(c, (SessionType) typeBox.getSelectedItem())) sessionBox.addItem(s);
        } catch (DatabaseException e) {
            Dialogs.handle(this, e);
        }
    }

    private void loadRows() {
        rows = new ArrayList<>();
        List<Object[]> data = new ArrayList<>();
        Session s = (Session) sessionBox.getSelectedItem();
        try {
            if (s != null) {
                Map<Integer, AttendanceRecord> saved = officer.attendance().findBySession(s, students);
                for (StudentInfo st : students) {
                    AttendanceRecord r = saved.get(st.getUserId());
                    if (r == null) r = AttendanceRecord.create(st, s, AttendanceStatus.PRESENT);   // default: present
                    rows.add(r);
                    data.add(new Object[]{st.getRegistrationNumber(), st.getFullName(), st.getAttemptType(),
                            r.getStatus() == AttendanceStatus.PRESENT, r.getMedicalStatus().isEmpty() ? "" : r.getMedicalStatus()});
                }
            }
        } catch (DatabaseException e) {
            Dialogs.handle(this, e);
        }
        table.setRows(data);
    }

    private void setAll(boolean present) {
        table.stopEditing();
        for (int i = 0; i < table.getModel().getRowCount(); i++) table.getModel().setValueAt(present, i, 3);
    }

    private void generate() {
        Course c = selectedCourse();
        if (c == null) return;
        String in = JOptionPane.showInputDialog(this, "Date of session 1 (yyyy-MM-dd). The other 14 follow weekly:", LocalDate.now().toString());
        if (in == null) return;
        try {
            LocalDate d;
            try {
                d = LocalDate.parse(in.trim());
            } catch (Exception ex) {
                throw new InvalidSessionException("Please type the date like 2026-07-06.");
            }
            officer.sessions().generate(c, (SessionType) typeBox.getSelectedItem(), d);
            loadSessions();
        } catch (AppException e) {
            Dialogs.handle(this, e);
        }
    }

    private void save() {
        table.stopEditing();
        Session s = (Session) sessionBox.getSelectedItem();
        if (s == null) { Dialogs.error(this, "Choose a session first (or generate the sessions)."); return; }
        if (s.getSessionDate().after(new java.sql.Date(System.currentTimeMillis()))
                && !Dialogs.confirm(this, "This session is in the future (" + s.getSessionDate() + "). Record attendance anyway?")) return;
        try {
            List<AttendanceRecord> out = new ArrayList<>();
            for (int i = 0; i < rows.size(); i++) {
                boolean present = Boolean.TRUE.equals(table.getModel().getValueAt(i, 3));
                out.add(AttendanceRecord.create(rows.get(i).getStudent(), s, present ? AttendanceStatus.PRESENT : AttendanceStatus.ABSENT));
            }
            officer.recordAttendance(out);
            Dialogs.info(this, "Attendance saved for " + out.size() + " undergraduates.");
            loadRows();
        } catch (AppException e) {
            Dialogs.handle(this, e);
        }
    }
}
