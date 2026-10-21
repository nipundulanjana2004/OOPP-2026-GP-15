/*
 * ====================================================================
 *  PART    : M1 - ADMIN
 *  PURPOSE : Admin course operations: create/update/delete courses, assign lecturers, register undergraduates.
 *  PLUGS IN: Admin.manageCourses(); CourseManagementPanel and RegistrationPanel.
 *  OOP     : INHERITANCE: extends common.CourseDAO.
 * ====================================================================
 */
package admin;

import common.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseAdminDAO extends CourseDAO {

    public int insert(Course c) throws DatabaseException {
        String sql = "INSERT INTO course (course_code,course_name,theory_credits,practical_credits,semester,department_id) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, c);
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) {
                return k.next() ? k.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw wrap("saving the course", e);
        }
    }

    public void update(Course c) throws DatabaseException {
        String sql = "UPDATE course SET course_code=?, course_name=?, theory_credits=?, practical_credits=?, semester=?, department_id=? WHERE course_id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            bind(ps, c);
            ps.setInt(7, c.getCourseId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("updating the course", e);
        }
    }

    private void bind(PreparedStatement ps, Course c) throws SQLException {
        ps.setString(1, c.getCourseCode());
        ps.setString(2, c.getCourseName());
        ps.setInt(3, c.getTheoryCredits());
        ps.setInt(4, c.getPracticalCredits());
        ps.setInt(5, c.getSemester());
        ps.setInt(6, c.getDepartment().getDepartmentId());
    }

    public void delete(int courseId) throws DatabaseException {
        try (PreparedStatement ps = conn().prepareStatement("DELETE FROM course WHERE course_id=?")) {
            ps.setInt(1, courseId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("deleting the course", e);
        }
    }

    // ---------------- lecturer assignment ----------------
    public List<CourseAssignment> findAssignments(Course course) throws DatabaseException {
        List<CourseAssignment> list = new ArrayList<>();
        String sql = "SELECT cl.id, cl.lecturer_id, cl.role_in_course, CONCAT(u.first_name,' ',u.last_name) AS n FROM course_lecturer cl " +
                "JOIN app_user u ON u.user_id=cl.lecturer_id WHERE cl.course_id=? ORDER BY cl.id";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, course.getCourseId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new CourseAssignment(rs.getInt("id"), course, rs.getInt("lecturer_id"), rs.getString("n"), rs.getString("role_in_course")));
                }
            }
        } catch (SQLException e) {
            throw wrap("loading lecturers of the course", e);
        }
        return list;
    }

    public void assign(int courseId, int lecturerId, String role) throws DatabaseException {
        String sql = "INSERT INTO course_lecturer (course_id,lecturer_id,role_in_course) VALUES (?,?,?) ON DUPLICATE KEY UPDATE role_in_course=VALUES(role_in_course)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.setInt(2, lecturerId);
            ps.setString(3, role);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("assigning the lecturer", e);
        }
    }

    public void unassign(int assignmentId) throws DatabaseException {
        try (PreparedStatement ps = conn().prepareStatement("DELETE FROM course_lecturer WHERE id=?")) {
            ps.setInt(1, assignmentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("removing the lecturer", e);
        }
    }

    // ---------------- undergraduate registration ----------------
    public void register(int undergraduateId, int courseId, String academicYear, AttemptType attempt) throws DatabaseException {
        String sql = "INSERT INTO course_registration (undergraduate_id,course_id,academic_year,attempt_type) VALUES (?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE attempt_type=VALUES(attempt_type), academic_year=VALUES(academic_year)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, undergraduateId);
            ps.setInt(2, courseId);
            ps.setString(3, academicYear);
            ps.setString(4, attempt.name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("registering the undergraduate", e);
        }
    }

    public void unregister(int undergraduateId, int courseId) throws DatabaseException {
        try (PreparedStatement ps = conn().prepareStatement("DELETE FROM course_registration WHERE undergraduate_id=? AND course_id=?")) {
            ps.setInt(1, undergraduateId);
            ps.setInt(2, courseId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("removing the registration", e);
        }
    }
}
