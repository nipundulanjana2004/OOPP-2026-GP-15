/*
 * ====================================================================
 *  PART    : M2 - LECTURER
 *  PURPOSE : The Lecturer user (class diagram: Lecturer). Updates own profile (not username/password), manages materials, uploads marks, views eligibility / results.
 *  PLUGS IN: Created by common.UserFactory when role = LECTURER; used by every lecturer screen.
 *  OOP     : INHERITANCE (extends common.User), POLYMORPHISM (own updateProfile / getEditableFields), ENCAPSULATION (designation, department), composition (HAS a Department).
 * ====================================================================
 */
package lecturer;

import common.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Lecturer extends User {
    private String designation = "";
    private Department department = new Department();
    private final MaterialDAO materialDAO = new MaterialDAO();
    private final MarkDAO markDAO = new MarkDAO();
    private final ReportService reports = new ReportService();

    public Lecturer() {
        super(Role.LECTURER);
    }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    @Override
    public int getDepartmentId() {
        return department.getDepartmentId();
    }

    // ---------- behaviour from the class diagram ----------
    public List<Course> myCourses() throws DatabaseException {
        return new CourseDAO().findByLecturer(getUserId());
    }

    public void addMaterial(CourseMaterial m) throws DatabaseException {
        m.setUploadedBy(getUserId());
        materialDAO.insert(m);
    }

    public MaterialDAO manageMaterials() {
        return materialDAO;
    }

    /** Validates (polymorphic) and saves ONE mark of any kind. */
    public void uploadMark(AssessmentMark mark) throws AppException {
        mark.validate();
        markDAO.save(mark);
    }

    public List<StudentInfo> viewUndergraduateDetails(Course c) throws DatabaseException {
        return new LookupDAO().studentsOfCourse(c.getCourseId());
    }

    public List<CourseResult> viewEligibility(Course c) throws DatabaseException {
        return reports.resultsForBatch(c);
    }

    public List<AttendanceSummary> viewAttendance(Course c) throws DatabaseException {
        return reports.attendanceForBatch(c);
    }

    // ---------- overrides ----------
    @Override
    public Set<String> getEditableFields() {      // everything except username (and password, which has no field)
        return new HashSet<>(Arrays.asList("firstName", "lastName", "email", "contactNumber", "address", "profilePicture"));
    }

    @Override
    public void updateProfile(Map<String, String> data) {
        Set<String> allowed = getEditableFields();
        for (Map.Entry<String, String> e : data.entrySet()) {
            applyField(e.getKey(), e.getValue(), allowed);
        }
    }

    @Override public String getRoleTable() { return "lecturer"; }

    @Override
    public Map<String, Object> getRoleColumns() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("designation", designation);
        m.put("department_id", department.getDepartmentId());
        return m;
    }

    @Override
    public void loadRoleDetails(ResultSet rs) throws SQLException {
        designation = rs.getString("designation") == null ? "" : rs.getString("designation");
        department = new Department(rs.getInt("dept_id"), rs.getString("dept_code"), rs.getString("dept_name"));
    }

    @Override
    public Map<String, Object> getRoleInput() {
        Map<String, Object> m = new HashMap<>();
        m.put("department", department);
        m.put("designation", designation);
        return m;
    }

    @Override
    public void setRoleInput(Map<String, Object> in) {
        if (in.get("department") != null) department = (Department) in.get("department");
        if (in.get("designation") != null) designation = in.get("designation").toString();
    }

    @Override
    public String getRoleSummary() {
        return designation + " - " + department.getDepartmentCode();
    }
}
