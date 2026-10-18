
package common;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO extends BaseDAO {

    protected static final String SELECT_COURSE =
            "SELECT c.*, d.department_code, d.department_name FROM course c JOIN department d ON d.department_id=c.department_id ";

    protected Course mapCourse(ResultSet rs) throws SQLException {
        Department d = new Department(rs.getInt("department_id"), rs.getString("department_code"), rs.getString("department_name"));
        return new Course(rs.getInt("course_id"), rs.getString("course_code"), rs.getString("course_name"),
                rs.getInt("theory_credits"), rs.getInt("practical_credits"), rs.getInt("semester"), d);
    }

    private List<Course> query(String where, int param) throws DatabaseException {
        List<Course> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(SELECT_COURSE + where + " ORDER BY c.semester DESC, c.course_code")) {
            if (param > 0) ps.setInt(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapCourse(rs));
            }
        } catch (SQLException e) {
            throw wrap("loading courses", e);
        }
        return list;
    }

    public List<Course> findAll() throws DatabaseException {
        return query("", 0);
    }

    public Course findById(int id) throws DatabaseException {
        List<Course> l = query("WHERE c.course_id=?", id);
        return l.isEmpty() ? null : l.get(0);
    }

    public List<Course> findByDepartment(int departmentId) throws DatabaseException {
        return query("WHERE c.department_id=?", departmentId);
    }

    public List<Course> findByLecturer(int lecturerId) throws DatabaseException {
        return query("WHERE c.course_id IN (SELECT course_id FROM course_lecturer WHERE lecturer_id=?)", lecturerId);
    }

    public List<Course> findByUndergraduate(int undergraduateId) throws DatabaseException {
        return query("WHERE c.course_id IN (SELECT course_id FROM course_registration WHERE undergraduate_id=?)", undergraduateId);
    }

    public String lecturerNames(int courseId) throws DatabaseException {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT CONCAT(u.first_name,' ',u.last_name) AS n FROM course_lecturer cl JOIN app_user u ON u.user_id=cl.lecturer_id WHERE cl.course_id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(rs.getString("n"));
                }
            }
        } catch (SQLException e) {
            throw wrap("loading lecturers", e);
        }
        return sb.toString();
    }
}
