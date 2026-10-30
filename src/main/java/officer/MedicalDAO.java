/*
 * ====================================================================
 *  PART    : M3 - TECHNICAL OFFICER
 *  PURPOSE : Saves, updates, approves / rejects and lists medicals of the officer's department. Recording a medical links it to the student's absences inside the date range.
 *  PLUGS IN: TechnicalOfficer.addMedical()/verifyMedical(); MedicalPanel; OfficerHomePanel.
 *  OOP     : INHERITANCE: extends common.BaseDAO; JDBC; business rule in SQL.
 * ====================================================================
 */
package officer;

import common.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MedicalDAO extends BaseDAO {

    public List<StudentInfo> studentsOfDepartment(int departmentId) throws DatabaseException {
        List<StudentInfo> list = new ArrayList<>();
        String sql = "SELECT g.user_id, g.registration_number, CONCAT(u.first_name,' ',u.last_name) AS n FROM undergraduate g " +
                "JOIN app_user u ON u.user_id=g.user_id WHERE g.department_id=? ORDER BY g.registration_number";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new StudentInfo(rs.getInt(1), rs.getString(2), rs.getString(3)));
            }
        } catch (SQLException e) {
            throw wrap("loading undergraduates", e);
        }
        return list;
    }

    public List<Medical> findByDepartment(int departmentId) throws DatabaseException {
        List<Medical> list = new ArrayList<>();
        String sql = "SELECT m.*, g.registration_number, CONCAT(u.first_name,' ',u.last_name) AS sname, CONCAT(o.first_name,' ',o.last_name) AS officer " +
                "FROM medical m JOIN undergraduate g ON g.user_id=m.undergraduate_id JOIN app_user u ON u.user_id=g.user_id " +
                "LEFT JOIN app_user o ON o.user_id=m.verified_by_officer_id WHERE g.department_id=? ORDER BY m.date_from DESC, m.medical_id DESC";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Medical m = new Medical();
                    m.setMedicalId(rs.getInt("medical_id"));
                    m.setStudent(new StudentInfo(rs.getInt("undergraduate_id"), rs.getString("registration_number"), rs.getString("sname")));
                    m.setDateFrom(rs.getDate("date_from"));
                    m.setDateTo(rs.getDate("date_to"));
                    m.setReason(rs.getString("reason"));
                    m.setDocumentPath(rs.getString("document_path") == null ? "" : rs.getString("document_path"));
                    m.setApprovalStatus(ApprovalStatus.valueOf(rs.getString("approval_status")));
                    m.setVerifiedBy(rs.getString("officer") == null ? "-" : rs.getString("officer"));
                    list.add(m);
                }
            }
        } catch (SQLException e) {
            throw wrap("loading medicals", e);
        }
        return list;
    }

    /** Returns how many absences were linked to this medical. */
    public int insert(Medical m) throws AppException {
        m.validate();
        String sql = "INSERT INTO medical (undergraduate_id,date_from,date_to,reason,document_path,approval_status,submitted_date) VALUES (?,?,?,?,?,'PENDING',?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, m.getStudent().getUserId());
            ps.setDate(2, m.getDateFrom());
            ps.setDate(3, m.getDateTo());
            ps.setString(4, m.getReason());
            ps.setString(5, m.getDocumentPath());
            ps.setDate(6, new java.sql.Date(System.currentTimeMillis()));
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) {
                k.next();
                m.setMedicalId(k.getInt(1));
            }
        } catch (SQLException e) {
            throw wrap("saving the medical", e);
        }
        return linkAbsences(m);
    }

    public int update(Medical m) throws AppException {
        m.validate();
        try (PreparedStatement ps = conn().prepareStatement("UPDATE medical SET date_from=?, date_to=?, reason=?, document_path=? WHERE medical_id=?")) {
            ps.setDate(1, m.getDateFrom());
            ps.setDate(2, m.getDateTo());
            ps.setString(3, m.getReason());
            ps.setString(4, m.getDocumentPath());
            ps.setInt(5, m.getMedicalId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("updating the medical", e);
        }
        try (PreparedStatement ps = conn().prepareStatement("UPDATE attendance SET medical_id=NULL WHERE medical_id=?")) {
            ps.setInt(1, m.getMedicalId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("updating the medical", e);
        }
        return linkAbsences(m);
    }

    /** Links the student's ABSENT sessions inside the medical's date range to this medical. */
    private int linkAbsences(Medical m) throws DatabaseException {
        String sql = "UPDATE attendance a JOIN class_session cs ON cs.session_id=a.session_id SET a.medical_id=? " +
                "WHERE a.undergraduate_id=? AND a.status='ABSENT' AND a.medical_id IS NULL AND cs.session_date BETWEEN ? AND ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, m.getMedicalId());
            ps.setInt(2, m.getStudent().getUserId());
            ps.setDate(3, m.getDateFrom());
            ps.setDate(4, m.getDateTo());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("linking the medical to absences", e);
        }
    }

    public void setStatus(int medicalId, ApprovalStatus status, int officerId) throws DatabaseException {
        try (PreparedStatement ps = conn().prepareStatement("UPDATE medical SET approval_status=?, verified_by_officer_id=? WHERE medical_id=?")) {
            ps.setString(1, status.name());
            ps.setInt(2, officerId);
            ps.setInt(3, medicalId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("changing the medical status", e);
        }
    }

    public void delete(int medicalId) throws DatabaseException {
        try (PreparedStatement ps = conn().prepareStatement("DELETE FROM medical WHERE medical_id=?")) {
            ps.setInt(1, medicalId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("deleting the medical", e);
        }
    }
}
