package common;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LookupDAO extends BaseDAO {

    public List<Department> departments() throws DatabaseException {
        List<Department> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement("SELECT * FROM department ORDER BY department_code");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Department(rs.getInt("department_id"), rs.getString("department_code"), rs.getString("department_name")));
            }
        } catch (SQLException e) {
            throw wrap("loading departments", e);
        }
        return list;
    }

    public List<Batch> batches() throws DatabaseException {
        List<Batch> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement("SELECT * FROM batch ORDER BY batch_name");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Batch(rs.getInt("batch_id"), rs.getString("batch_name"), rs.getString("academic_year")));
            }
        } catch (SQLException e) {
            throw wrap("loading batches", e);
        }
        return list;
    }

    private static final String STUDENT_SQL =
            "SELECT u.user_id, u.first_name, u.last_name, u.email, u.contact_number, g.registration_number, b.batch_name, " +
            "d.department_name, %s AS attempt FROM undergraduate g JOIN app_user u ON u.user_id=g.user_id " +
            "JOIN batch b ON b.batch_id=g.batch_id JOIN department d ON d.department_id=g.department_id %s " +
            "ORDER BY g.registration_number";

    private StudentInfo map(ResultSet rs) throws SQLException {
        StudentInfo s = new StudentInfo(rs.getInt("user_id"), rs.getString("registration_number"),
                rs.getString("first_name") + " " + rs.getString("last_name"));
        s.setBatchName(rs.getString("batch_name"));
        s.setDepartmentName(rs.getString("department_name"));
        s.setAttemptType(rs.getString("attempt"));
        s.setEmail(rs.getString("email"));
        s.setContactNumber(rs.getString("contact_number"));
        return s;
    }

    public List<StudentInfo> allStudents() throws DatabaseException {
        List<StudentInfo> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(String.format(STUDENT_SQL, "g.academic_status", ""));
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw wrap("loading undergraduates", e);
        }
        return list;
    }

    public List<StudentInfo> studentsOfCourse(int courseId) throws DatabaseException {
        List<StudentInfo> list = new ArrayList<>();
        String sql = String.format(STUDENT_SQL, "r.attempt_type", "JOIN course_registration r ON r.undergraduate_id=g.user_id AND r.course_id=?");
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw wrap("loading the students of a course", e);
        }
        return list;
    }

    public StudentInfo studentById(int userId) throws DatabaseException {
        for (StudentInfo s : allStudents()) {
            if (s.getUserId() == userId) return s;
        }
        return null;
    }
}
