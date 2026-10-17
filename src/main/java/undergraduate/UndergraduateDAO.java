/*
 * ====================================================================
 *  PART    : M4 - UNDERGRADUATE
 *  PURPOSE : The undergraduate's OWN data: registrations and a session-by-session attendance list.
 *  PLUGS IN: Undergraduate class; MyCoursesPanel, MyAttendancePanel.
 *  OOP     : INHERITANCE: extends common.BaseDAO; JDBC PreparedStatements (every query is limited to ONE student id).
 * ====================================================================
 */
package undergraduate;

import common.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UndergraduateDAO extends BaseDAO {

    public List<CourseRegistration> registrationsOf(int undergraduateId) throws DatabaseException {
        List<CourseRegistration> list = new ArrayList<>();
        String sql = "SELECT r.academic_year, r.attempt_type, c.*, d.department_code, d.department_name FROM course_registration r " +
                "JOIN course c ON c.course_id=r.course_id JOIN department d ON d.department_id=c.department_id " +
                "WHERE r.undergraduate_id=? ORDER BY c.semester DESC, c.course_code";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, undergraduateId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Department d = new Department(rs.getInt("department_id"), rs.getString("department_code"), rs.getString("department_name"));
                    Course c = new Course(rs.getInt("course_id"), rs.getString("course_code"), rs.getString("course_name"),
                            rs.getInt("theory_credits"), rs.getInt("practical_credits"), rs.getInt("semester"), d);
                    list.add(new CourseRegistration(c, rs.getString("academic_year"), AttemptType.valueOf(rs.getString("attempt_type"))));
                }
            }
        } catch (SQLException e) {
            throw wrap("loading your courses", e);
        }
        return list;
    }

    /** Rows: type, session no, date, status, medical status. */
    public List<Object[]> sessionDetails(int undergraduateId, int courseId) throws DatabaseException {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT cs.session_type, cs.session_number, cs.session_date, a.status, m.approval_status FROM class_session cs " +
                "JOIN attendance a ON a.session_id=cs.session_id AND a.undergraduate_id=? LEFT JOIN medical m ON m.medical_id=a.medical_id " +
                "WHERE cs.course_id=? ORDER BY cs.session_type, cs.session_number";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, undergraduateId);
            ps.setInt(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Object[]{rs.getString(1), rs.getInt(2), rs.getDate(3), rs.getString(4), rs.getString(5) == null ? "-" : rs.getString(5)});
                }
            }
        } catch (SQLException e) {
            throw wrap("loading your attendance", e);
        }
        return rows;
    }

    public boolean isRegistered(int undergraduateId, int courseId) throws DatabaseException {
        try (PreparedStatement ps = conn().prepareStatement("SELECT 1 FROM course_registration WHERE undergraduate_id=? AND course_id=?")) {
            ps.setInt(1, undergraduateId);
            ps.setInt(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw wrap("checking your registration", e);
        }
    }
}
