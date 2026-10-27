/*
 * ====================================================================
 *  PART    : M1 - ADMIN
 *  PURPOSE : Create / update / delete timetable slots with a venue-clash check (FR-13).
 *  PLUGS IN: AdminDashboard -> 'Timetable'. M2 sees the full timetable, M3 only their department, M4 only their registered courses.
 *  OOP     : INHERITANCE (extends ManagementPanel<Timetable>), validation (time format, clashes).
 * ====================================================================
 */
package admin;

import common.*;
import common.ui.*;

import javax.swing.*;
import java.sql.Time;
import java.util.*;

public class TimetableManagementPanel extends ManagementPanel<Timetable> {
    private final Admin admin;
    private final JComboBox<Course> course = new JComboBox<>();
    private final JComboBox<SessionType> type = Theme.combo(SessionType.values());
    private final JComboBox<String> day = Theme.combo(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"});
    private final JTextField start = Theme.text(18);
    private final JTextField end = Theme.text(18);
    private final JTextField venue = Theme.text(18);
    private final JComboBox<Batch> batch = new JComboBox<>();

    public TimetableManagementPanel(Admin admin) {
        super("Timetables", "Weekly timetable of lectures and practicals", "Timetable slot",
                "Day", "Time", "Course", "Type", "Venue", "Batch", "Dept");
        this.admin = admin;
        start.setText("08:00");
        end.setText("10:00");
        init();
    }

    @Override
    protected void buildForm(FormPanel f) {
        f.addRow("Course", course);
        f.addRow("Session type", type);
        f.addRow("Day", day);
        f.addRow("Start time (HH:mm)", start);
        f.addRow("End time (HH:mm)", end);
        f.addRow("Venue", venue);
        f.addRow("Batch", batch);
    }

    @Override
    protected java.util.List<Timetable> loadItems() throws AppException {
        LookupDAO lookup = new LookupDAO();
        course.removeAllItems();
        for (Course c : admin.manageCourses().findAll()) course.addItem(c);
        batch.removeAllItems();
        for (Batch b : lookup.batches()) batch.addItem(b);
        return admin.manageTimetables().findAll();
    }

    @Override
    protected Object[] toRow(Timetable t) {
        return new Object[]{t.getDayOfWeek(), t.getStartTime().toString().substring(0, 5) + " - " + t.getEndTime().toString().substring(0, 5),
                t.getCourse().toString(), t.getSessionType(), t.getVenue(), t.getBatch().getBatchName(), t.getDepartment().getDepartmentCode()};
    }

    @Override
    protected void fillForm(Timetable t) {
        for (int i = 0; i < course.getItemCount(); i++) if (course.getItemAt(i).getCourseId() == t.getCourse().getCourseId()) course.setSelectedIndex(i);
        for (int i = 0; i < batch.getItemCount(); i++) if (batch.getItemAt(i).getBatchId() == t.getBatch().getBatchId()) batch.setSelectedIndex(i);
        type.setSelectedItem(t.getSessionType());
        day.setSelectedItem(t.getDayOfWeek());
        start.setText(t.getStartTime().toString().substring(0, 5));
        end.setText(t.getEndTime().toString().substring(0, 5));
        venue.setText(t.getVenue());
    }

    @Override
    protected void clearForm() {
        venue.setText("");
        start.setText("08:00");
        end.setText("10:00");
    }

    private Time parseTime(String s, String field) throws ValidationException {
        if (!s.trim().matches("^([01]?\\d|2[0-3]):[0-5]\\d$")) throw new ValidationException(field + " must look like 08:30 (24-hour time).");
        String t = s.trim().length() == 4 ? "0" + s.trim() : s.trim();
        return Time.valueOf(t + ":00");
    }

    private Timetable readForm(Timetable t) throws AppException {
        if (course.getSelectedItem() == null || batch.getSelectedItem() == null) throw new ValidationException("Course and batch are required.");
        Course c = (Course) course.getSelectedItem();
        SessionType st = (SessionType) type.getSelectedItem();
        if ((st == SessionType.THEORY && !c.hasTheory()) || (st == SessionType.PRACTICAL && !c.hasPractical())) {
            throw new ValidationException(c.getCourseCode() + " has no " + st.name().toLowerCase() + " component.");
        }
        t.setCourse(c);
        t.setSessionType(st);
        t.setDayOfWeek((String) day.getSelectedItem());
        t.setStartTime(parseTime(start.getText(), "Start time"));
        t.setEndTime(parseTime(end.getText(), "End time"));
        t.setVenue(need(venue.getText(), "Venue"));
        t.setBatch((Batch) batch.getSelectedItem());
        t.setDepartment(c.getDepartment());
        return t;
    }

    @Override
    protected void doAdd() throws AppException {
        admin.manageTimetables().insert(readForm(new Timetable()), admin.getUserId());
    }

    @Override
    protected void doUpdate(Timetable t) throws AppException {
        admin.manageTimetables().update(readForm(t));
    }

    @Override
    protected void doDelete(Timetable t) throws AppException {
        admin.manageTimetables().delete(t.getTimetableId());
    }
}
