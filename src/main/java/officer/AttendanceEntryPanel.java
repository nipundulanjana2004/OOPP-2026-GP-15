/*
 * ====================================================================
 *  PART    : M3 - TECHNICAL OFFICER
 *  PURPOSE : Record attendance of a whole class for one session: tick PRESENT, untick absentees, Save (FR-23). Can generate the 15 sessions of a course. Absences covered by a medical are linked automatically.
 *            Unsaved ticks are remembered; leaving the page / logging out asks "Save now?" so nothing is lost.
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AttendanceEntryPanel extends OfficerCoursePanel {
    private final JComboBox<SessionType> typeBox = new JComboBox<>(SessionType.values());
    private final JComboBox<Session> sessionBox = new JComboBox<>();
    private final TablePanel table = new TablePanel("", "Reg. No", "Name", "Attempt", "Present", "Medical");
    private final JLabel status = Theme.muted(" ");
    private List<AttendanceRecord> rows = new ArrayList<>();
    /** Ticks changed but NOT yet saved: sessionId -> ready-to-save records. */
    private final Map<Integer, List<AttendanceRecord>> unsaved = new LinkedHashMap<>();
    private Session shownSession = null;      // session whose rows are in the table
    private boolean capturing = false;        // guard against re-entrant table events
    private boolean populating = false;       // true while the table is being filled by code (not by the officer)
    private int keepSessionId = 0;
    private SessionType keepType = null;

    public AttendanceEntryPanel(TechnicalOfficer officer) {
        super(officer, "Record Attendance", "Choose course, type and session. Tick = present. Press SAVE ATTENDANCE (bottom right) to store it in the database.");
        typeBox.setPreferredSize(new Dimension(120, 34));
        sessionBox.setPreferredSize(new Dimension(260, 34));
        bar.add(typeBox);
        bar.add(sessionBox);

        RoundedButton gen = new RoundedButton("Generate 15 sessions", RoundedButton.Kind.SECONDARY);
        RoundedButton all = new RoundedButton("All present", RoundedButton.Kind.SECONDARY);
        RoundedButton none = new RoundedButton("All absent", RoundedButton.Kind.SECONDARY);
        RoundedButton save = new RoundedButton("Save attendance", RoundedButton.Kind.SUCCESS);
        save.setPreferredSize(new Dimension(190, 40));
        gen.addActionListener(e -> generate());
        all.addActionListener(e -> setAll(true));
        none.addActionListener(e -> setAll(false));
        save.addActionListener(e -> saveCurrent());

        JPanel bottom = Theme.transparent(new BorderLayout(10, 0));
        JPanel leftBtns = Theme.transparent(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftBtns.add(gen);
        leftBtns.add(all);
        leftBtns.add(none);
        JPanel rightBtns = Theme.transparent(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightBtns.add(status);
        rightBtns.add(save);
        bottom.add(leftBtns, BorderLayout.WEST);
        bottom.add(rightBtns, BorderLayout.EAST);

        table.setEditableColumns(3);
        table.setColumnWidth(0, 120);
        body.add(table, BorderLayout.CENTER);
        body.add(bottom, BorderLayout.SOUTH);

        // any tick the officer changes marks this session as "not saved yet"
        table.getModel().addTableModelListener(e -> {
            if (!populating && !capturing && e.getColumn() == 3 && shownSession != null) {
                capturing = true;
                try { markUnsaved(); } finally { capturing = false; }
            }
        });
        typeBox.addActionListener(e -> { if (!loading) { rememberTicks(); keepSessionId = 0; loadSessions(); } });
        sessionBox.addActionListener(e -> { if (!loading) { rememberTicks(); loadRows(); } });
    }

    // ------------------------------ unsaved-work handling ------------------------------
    private void markUnsaved() {
        captureTicks();                 // NO stopEditing() here: we are already inside the cell editor's own event
        status.setForeground(Theme.WARNING);
        status.setText("Not saved yet");
    }

    /** Copies the ticks on screen into 'unsaved' (only when the officer changed something on this session). */
    private void rememberTicks() {
        table.stopEditing();
        captureTicks();
    }

    private void captureTicks() {
        if (shownSession == null || rows.isEmpty() || table.getModel().getRowCount() != rows.size()) return;
        if (populating) return;
        List<AttendanceRecord> out = buildRecords(shownSession);
        // only keep it if it differs from what is stored, or was already marked unsaved
        if (unsaved.containsKey(shownSession.getSessionId()) || changedFromStored(out)) unsaved.put(shownSession.getSessionId(), out);
    }

    private boolean changedFromStored(List<AttendanceRecord> now) {
        for (int i = 0; i < now.size(); i++) if (now.get(i).getStatus() != rows.get(i).getStatus()) return true;
        return false;
    }

    private List<AttendanceRecord> buildRecords(Session s) {
        List<AttendanceRecord> out = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            boolean present = Boolean.TRUE.equals(table.getModel().getValueAt(i, 3));
            out.add(AttendanceRecord.create(rows.get(i).getStudent(), s, present ? AttendanceStatus.PRESENT : AttendanceStatus.ABSENT));
        }
        return out;
    }

    /** Called by the dashboard before another page opens, on log out and on window close. */
    @Override
    public boolean confirmLeave() {
        rememberTicks();
        if (unsaved.isEmpty()) return true;
        int ans = JOptionPane.showConfirmDialog(this,
                "You have attendance for " + unsaved.size() + " session(s) that is NOT saved yet.\nSave it to the database now?",
                "Unsaved attendance", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ans == JOptionPane.CANCEL_OPTION || ans == JOptionPane.CLOSED_OPTION) return false;
        if (ans == JOptionPane.NO_OPTION) {
            unsaved.clear();
            return true;
        }
        try {
            for (List<AttendanceRecord> list : new ArrayList<>(unsaved.values())) {
                officer.recordAttendance(list);
                unsaved.remove(list.get(0).getSession().getSessionId());
            }
            return true;
        } catch (AppException e) {
            Dialogs.handle(this, e);
            return false;
        }
    }

    // ------------------------------ loading ------------------------------
    @Override
    protected void refreshData() {
        Session cur = (Session) sessionBox.getSelectedItem();
        if (cur != null && selectedCourse() != null && cur.getCourse().getCourseId() == selectedCourse().getCourseId()) {
            rememberTicks();
            keepSessionId = cur.getSessionId();
            keepType = cur.getSessionType();
        } else {
            keepSessionId = 0;
            keepType = null;
        }
        loading = true;
        if (keepType != null) typeBox.setSelectedItem(keepType);
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
            for (Session s : officer.sessions().find(c, (SessionType) typeBox.getSelectedItem())) {
                sessionBox.addItem(s);
                if (s.getSessionId() == keepSessionId) sessionBox.setSelectedItem(s);
            }
        } catch (DatabaseException e) {
            Dialogs.handle(this, e);
        }
    }

    private void loadRows() {
        rows = new ArrayList<>();
        List<Object[]> data = new ArrayList<>();
        Session s = (Session) sessionBox.getSelectedItem();
        shownSession = s;
        try {
            if (s != null) {
                Map<Integer, AttendanceRecord> saved = officer.attendance().findBySession(s, students);
                Map<Integer, AttendanceStatus> draft = new HashMap<>();
                List<AttendanceRecord> d = unsaved.get(s.getSessionId());
                if (d != null) for (AttendanceRecord r : d) draft.put(r.getStudent().getUserId(), r.getStatus());
                for (StudentInfo st : students) {
                    AttendanceRecord r = saved.get(st.getUserId());
                    if (r == null) r = AttendanceRecord.create(st, s, AttendanceStatus.PRESENT);   // default: present
                    rows.add(r);
                    AttendanceStatus shown = draft.containsKey(st.getUserId()) ? draft.get(st.getUserId()) : r.getStatus();
                    data.add(new Object[]{st.getRegistrationNumber(), st.getFullName(), st.getAttemptType(),
                            shown == AttendanceStatus.PRESENT, r.getMedicalStatus().isEmpty() ? "" : r.getMedicalStatus()});
                }
            }
        } catch (DatabaseException e) {
            Dialogs.handle(this, e);
        }
        populating = true;
        table.setRows(data);
        populating = false;
        boolean pending = s != null && unsaved.containsKey(s.getSessionId());
        status.setForeground(pending ? Theme.WARNING : Theme.MUTED);
        status.setText(pending ? "Not saved yet" : " ");
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

    // ------------------------------ save ------------------------------
    private void saveCurrent() {
        table.stopEditing();
        Session s = (Session) sessionBox.getSelectedItem();
        if (s == null) { Dialogs.error(this, "Choose a session first (or generate the sessions)."); return; }
        if (rows.isEmpty()) { Dialogs.error(this, "This course has no registered undergraduates."); return; }
        if (s.getSessionDate().after(new java.sql.Date(System.currentTimeMillis()))
                && !Dialogs.confirm(this, "This session is in the future (" + s.getSessionDate() + "). Record attendance anyway?")) return;
        try {
            List<AttendanceRecord> out = buildRecords(s);
            officer.recordAttendance(out);          // INSERT ... ON DUPLICATE KEY UPDATE, committed in one transaction
            unsaved.remove(s.getSessionId());
            loadRows();                             // re-read from the database = proof it was stored
            status.setForeground(Theme.SUCCESS);
            status.setText("Saved " + java.time.LocalTime.now().withNano(0));
            Dialogs.info(this, "Attendance saved to the database for " + out.size() + " undergraduates.");
        } catch (AppException e) {
            Dialogs.handle(this, e);
        }
    }
}
