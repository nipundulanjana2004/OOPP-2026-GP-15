/*
 * ====================================================================
 *  PART    : M3 - TECHNICAL OFFICER
 *  PURPOSE : The Technical Officer user (class diagram: TechnicalOfficer). Records attendance, maintains medicals, views attendance summaries and the department timetable.
 *  PLUGS IN: Created by common.UserFactory when role = TECH_OFFICER; used by every officer screen.
 *  OOP     : INHERITANCE (extends common.User), POLYMORPHISM (own updateProfile / getEditableFields), ENCAPSULATION, composition (HAS a Department, DAOs).
 * ====================================================================
 */
package officer;

import common.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TechnicalOfficer extends User {
    private Department department = new Department();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final MedicalDAO medicalDAO = new MedicalDAO();
    private final SessionDAO sessionDAO = new SessionDAO();
    private final ReportService reports = new ReportService();

    public TechnicalOfficer() {
        super(Role.TECH_OFFICER);
    }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    @Override
    public int getDepartmentId() {
        return department.getDepartmentId();
    }

    // ---------- behaviour from the class diagram ----------
    public List<Course> departmentCourses() throws DatabaseException {
        return new CourseDAO().findByDepartment(department.getDepartmentId());
    }

    public void recordAttendance(List<AttendanceRecord> records) throws AppException {
        attendanceDAO.saveSession(records, getUserId());
    }

    public int recordMedical(Medical m) throws AppException {
        return medicalDAO.insert(m);
    }

    public void verifyMedical(Medical m, boolean approve) throws AppException {
        if (approve) m.approve(); else m.reject();
        medicalDAO.setStatus(m.getMedicalId(), m.getApprovalStatus(), getUserId());
    }

    public List<AttendanceSummary> attendanceSummary(Course c) throws DatabaseException {
        return reports.attendanceForBatch(c);
    }

    public AttendanceDAO attendance() { return attendanceDAO; }
    public MedicalDAO medicals() { return medicalDAO; }
    public SessionDAO sessions() { return sessionDAO; }

    // ---------- overrides ----------
    @Override
    public Set<String> getEditableFields() {
        return new HashSet<>(Arrays.asList("firstName", "lastName", "email", "contactNumber", "address", "profilePicture"));
    }

    @Override
    public void updateProfile(Map<String, String> data) {
        Set<String> allowed = getEditableFields();
        for (Map.Entry<String, String> e : data.entrySet()) {
            applyField(e.getKey(), e.getValue(), allowed);
        }
    }

    @Override public String getRoleTable() { return "technical_officer"; }

    @Override
    public Map<String, Object> getRoleColumns() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("department_id", department.getDepartmentId());
        return m;
    }

    @Override
    public void loadRoleDetails(ResultSet rs) throws SQLException {
        department = new Department(rs.getInt("dept_id"), rs.getString("dept_code"), rs.getString("dept_name"));
    }

    @Override
    public Map<String, Object> getRoleInput() {
        Map<String, Object> m = new HashMap<>();
        m.put("department", department);
        return m;
    }

    @Override
    public void setRoleInput(Map<String, Object> in) {
        if (in.get("department") != null) department = (Department) in.get("department");
    }

    @Override
    public String getRoleSummary() {
        return "Technical Officer - " + department.getDepartmentCode();
    }
}
