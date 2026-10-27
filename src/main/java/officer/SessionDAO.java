/*
 * ====================================================================
 *  PART    : M3 - TECHNICAL OFFICER
 *  PURPOSE : Lists sessions of a course and generates the 15 weekly sessions of a type (1 credit = 2 hours per session).
 *  PLUGS IN: AttendanceEntryPanel ('Generate 15 sessions' button). common.ReportService reads the same class_session table.
 *  OOP     : INHERITANCE: extends common.BaseDAO.
 * ====================================================================
 */
package officer;

import common.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SessionDAO extends BaseDAO {
    public static final int SESSIONS_PER_TYPE = 15;

    public List<Session> find(Course course, SessionType type) throws DatabaseException {
        List<Session> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement("SELECT * FROM class_session WHERE course_id=? AND session_type=? ORDER BY session_number")) {
            ps.setInt(1, course.getCourseId());
            ps.setString(2, type.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Session(rs.getInt("session_id"), course, type, rs.getInt("session_number"), rs.getDate("session_date"), rs.getDouble("duration_hours")));
                }
            }
        } catch (SQLException e) {
            throw wrap("loading sessions", e);
        }
        return list;
    }

    public void generate(Course course, SessionType type, LocalDate firstDate) throws AppException {
        double hours = course.sessionHours(type);
        if (hours <= 0) throw new InvalidSessionException(course.getCourseCode() + " has no " + type.name().toLowerCase() + " credits.");
        if (!find(course, type).isEmpty()) throw new InvalidSessionException("Sessions for this course and type already exist.");
        String sql = "INSERT INTO class_session (course_id,session_type,session_number,session_date,duration_hours) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            for (int i = 0; i < SESSIONS_PER_TYPE; i++) {
                ps.setInt(1, course.getCourseId());
                ps.setString(2, type.name());
                ps.setInt(3, i + 1);
                ps.setDate(4, java.sql.Date.valueOf(firstDate.plusWeeks(i)));
                ps.setDouble(5, hours);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw wrap("generating sessions", e);
        }
    }
}
