
package common;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TimetableDAO extends BaseDAO {

    protected static final String SELECT_TT =
            "SELECT t.*, c.course_code, c.course_name, c.theory_credits, c.practical_credits, c.semester, " +
            "d.department_code, d.department_name, b.batch_name, b.academic_year FROM timetable t " +
            "JOIN course c ON c.course_id=t.course_id JOIN department d ON d.department_id=t.department_id " +
            "JOIN batch b ON b.batch_id=t.batch_id ";

    private static final String ORDER =
            " ORDER BY FIELD(t.day_of_week,'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'), t.start_time";

    protected Timetable mapTimetable(ResultSet rs) throws SQLException {
        Timetable t = new Timetable();
        Department d = new Department(rs.getInt("department_id"), rs.getString("department_code"), rs.getString("department_name"));
        t.setTimetableId(rs.getInt("timetable_id"));
        t.setCourse(new Course(rs.getInt("course_id"), rs.getString("course_code"), rs.getString("course_name"),
                rs.getInt("theory_credits"), rs.getInt("practical_credits"), rs.getInt("semester"), d));
        t.setSessionType(SessionType.valueOf(rs.getString("session_type")));
        t.setDayOfWeek(rs.getString("day_of_week"));
        t.setStartTime(rs.getTime("start_time"));
        t.setEndTime(rs.getTime("end_time"));
        t.setVenue(rs.getString("venue"));
        t.setBatch(new Batch(rs.getInt("batch_id"), rs.getString("batch_name"), rs.getString("academic_year")));
        t.setDepartment(d);
        return t;
    }

    private List<Timetable> query(String where, int param) throws DatabaseException {
        List<Timetable> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(SELECT_TT + where + ORDER)) {
            if (param > 0) ps.setInt(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapTimetable(rs));
            }
        } catch (SQLException e) {
            throw wrap("loading the timetable", e);
        }
        return list;
    }

    public List<Timetable> findAll() throws DatabaseException {
        return query("", 0);
    }

    public List<Timetable> findByDepartment(int departmentId) throws DatabaseException {
        return query("WHERE t.department_id=?", departmentId);
    }

    public List<Timetable> findForUndergraduate(int undergraduateId) throws DatabaseException {
        return query("WHERE t.course_id IN (SELECT course_id FROM course_registration WHERE undergraduate_id=?)", undergraduateId);
    }
}
