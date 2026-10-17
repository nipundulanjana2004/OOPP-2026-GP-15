/*
 * ====================================================================
 *  PART    : M3 - TECHNICAL OFFICER
 *  PURPOSE : Saves and loads attendance. When a student is ABSENT, the system automatically links a medical that covers that date (approved medicals count as attendance).
 *  PLUGS IN: TechnicalOfficer.recordAttendance(); AttendanceEntryPanel; AttendanceSummaryPanel (session detail).
 *  OOP     : INHERITANCE: extends common.BaseDAO; POLYMORPHISM: works with any AttendanceRecord; transaction.
 * ====================================================================
 */
package officer;

import common.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceDAO extends BaseDAO {

    /** studentId -> already recorded attendance of one session. */
    public Map<Integer, AttendanceRecord> findBySession(Session session, List<StudentInfo> students) throws DatabaseException {
        Map<Integer, StudentInfo> byId = new HashMap<>();
        for (StudentInfo s : students) byId.put(s.getUserId(), s);
        Map<Integer, AttendanceRecord> map = new HashMap<>();
        String sql = "SELECT a.*, m.approval_status FROM attendance a LEFT JOIN medical m ON m.medical_id=a.medical_id WHERE a.session_id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, session.getSessionId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StudentInfo s = byId.get(rs.getInt("undergraduate_id"));
                    if (s == null) continue;
                    AttendanceRecord r = AttendanceRecord.create(s, session, AttendanceStatus.valueOf(rs.getString("status")));
                    r.setAttendanceId(rs.getInt("attendance_id"));
                    r.setMedicalId(rs.getInt("medical_id"));
                    r.setMedicalStatus(rs.getString("approval_status"));
                    map.put(s.getUserId(), r);
                }
            }
        } catch (SQLException e) {
            throw wrap("loading attendance", e);
        }
        return map;
    }

    /** All sessions of a course (theory + practical) with ONE student's attendance. */
    public List<AttendanceRecord> findByStudent(StudentInfo student, Course course) throws DatabaseException {
        List<AttendanceRecord> list = new ArrayList<>();
        String sql = "SELECT cs.*, a.attendance_id, a.status, a.medical_id, m.approval_status FROM class_session cs " +
                "JOIN attendance a ON a.session_id=cs.session_id AND a.undergraduate_id=? LEFT JOIN medical m ON m.medical_id=a.medical_id " +
                "WHERE cs.course_id=? ORDER BY cs.session_type, cs.session_number";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, student.getUserId());
            ps.setInt(2, course.getCourseId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Session s = new Session(rs.getInt("session_id"), course, SessionType.valueOf(rs.getString("session_type")),
                            rs.getInt("session_number"), rs.getDate("session_date"), rs.getDouble("duration_hours"));
                    AttendanceRecord r = AttendanceRecord.create(student, s, AttendanceStatus.valueOf(rs.getString("status")));
                    r.setMedicalId(rs.getInt("medical_id"));
                    r.setMedicalStatus(rs.getString("approval_status"));
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            throw wrap("loading attendance", e);
        }
        return list;
    }

    private int medicalCovering(int studentId, java.sql.Date day) throws SQLException, DatabaseException {
        String sql = "SELECT medical_id FROM medical WHERE undergraduate_id=? AND ? BETWEEN date_from AND date_to " +
                "ORDER BY FIELD(approval_status,'APPROVED','PENDING','REJECTED'), medical_id LIMIT 1";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setDate(2, day);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public void saveSession(List<AttendanceRecord> records, int officerId) throws AppException {
        if (records.isEmpty()) throw new AttendanceException("There are no students to record.");
        String sql = "INSERT INTO attendance (undergraduate_id,session_id,status,recorded_by_officer_id,medical_id) VALUES (?,?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE status=VALUES(status), recorded_by_officer_id=VALUES(recorded_by_officer_id), medical_id=VALUES(medical_id)";
        try {
            conn().setAutoCommit(false);
            try (PreparedStatement ps = conn().prepareStatement(sql)) {
                for (AttendanceRecord r : records) {
                    int medical = r.getStatus() == AttendanceStatus.ABSENT ? medicalCovering(r.getStudent().getUserId(), r.getSession().getSessionDate()) : 0;
                    ps.setInt(1, r.getStudent().getUserId());
                    ps.setInt(2, r.getSession().getSessionId());
                    ps.setString(3, r.getStatus().name());
                    ps.setInt(4, officerId);
                    ps.setObject(5, medical == 0 ? null : medical);
                    ps.executeUpdate();
                }
            }
            conn().commit();
        } catch (SQLException e) {
            try { conn().rollback(); } catch (SQLException ignored) { /* nothing to do */ }
            throw wrap("saving attendance", e);
        } finally {
            try { conn().setAutoCommit(true); } catch (SQLException ignored) { /* nothing to do */ }
        }
    }
}
