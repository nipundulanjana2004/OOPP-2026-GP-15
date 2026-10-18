/*
 * ====================================================================
 *  PART    : COMMON UI (shared by M1-M4)
 *  PURPOSE : Weekly timetable table. Three modes: ALL (Lecturer), DEPARTMENT (Technical Officer - own department only, FR-14), MINE (Undergraduate - own registered courses).
 *  PLUGS IN: M2 uses ALL, M3 uses DEPARTMENT, M4 uses MINE. Rows are written by M1's TimetableAdminDAO.
 *  OOP     : Inheritance (extends BasePanel); one class, three behaviours chosen at construction (polymorphism by mode).
 * ====================================================================
 */
package common.ui;

import common.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TimetablePanel extends BasePanel {
    public enum Mode { ALL, DEPARTMENT, MINE }

    private final User user;
    private final Mode mode;
    private final TablePanel table = new TablePanel("", "Day", "Time", "Course", "Type", "Venue", "Batch", "Department");

    public TimetablePanel(User user, Mode mode) {
        super("Timetable", mode == Mode.ALL ? "Full faculty timetable"
                : mode == Mode.DEPARTMENT ? "Timetable of your department" : "Timetable of the courses you are registered for");
        this.user = user;
        this.mode = mode;
        body.add(table, BorderLayout.CENTER);
        table.setColumnWidth(0, 90);
        table.setColumnWidth(1, 120);
        table.setColumnWidth(2, 260);
    }

    @Override
    public void onShow() {
        try {
            TimetableDAO dao = new TimetableDAO();
            List<Timetable> data = mode == Mode.ALL ? dao.findAll()
                    : mode == Mode.DEPARTMENT ? dao.findByDepartment(user.getDepartmentId()) : dao.findForUndergraduate(user.getUserId());
            List<Object[]> rows = new ArrayList<>();
            for (Timetable t : data) {
                String time = t.getStartTime().toString().substring(0, 5) + " - " + t.getEndTime().toString().substring(0, 5);
                rows.add(new Object[]{t.getDayOfWeek(), time, t.getCourse().toString(), t.getSessionType(), t.getVenue(),
                        t.getBatch().getBatchName(), t.getDepartment().getDepartmentCode()});
            }
            table.setRows(rows);
        } catch (DatabaseException e) {
            Dialogs.handle(this, e);
        }
    }
}
