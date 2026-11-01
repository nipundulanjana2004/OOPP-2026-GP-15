
package undergraduate;

import common.*;
import officer.InvalidMedicalException;
import officer.Medical;
import officer.MedicalDAO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Undergraduate extends User {
    private String registrationNumber = "";
    private Batch batch = new Batch();
    private Department department = new Department();
    private AttemptType academicStatus = AttemptType.PROPER;
    private final ReportService reports = new ReportService();
    private final UndergraduateDAO dao = new UndergraduateDAO();
    private final MedicalDAO medicalDAO = new MedicalDAO();

    public Undergraduate() {
        super(Role.UNDERGRADUATE);
    }

    public String getRegistrationNumber() { return registrationNumber; }
    public Batch getBatch() { return batch; }
    public Department getDepartment() { return department; }
    public AttemptType getAcademicStatus() { return academicStatus; }

    @Override
    public int getDepartmentId() { return department.getDepartmentId(); }

    @Override
    public int getBatchId() { return batch.getBatchId(); }


    public StudentInfo toStudentInfo() {
        StudentInfo s = new StudentInfo(getUserId(), registrationNumber, getFullName());
        s.setBatchName(batch.getBatchName());
        s.setDepartmentName(department.getDepartmentName());
        s.setAttemptType(academicStatus.name());
        return s;
    }


    public List<CourseRegistration> viewCourses() throws DatabaseException {
        return dao.registrationsOf(getUserId());
    }

    /** Lecture notes / slides uploaded by the lecturers of the courses I am registered for (FR-17 download side). */
    public List<lecturer.CourseMaterial> viewMaterials() throws DatabaseException {
        List<Course> mine = new java.util.ArrayList<>();
        for (CourseRegistration r : viewCourses()) mine.add(r.getCourse());
        return new lecturer.MaterialDAO().findForStudent(getUserId(), mine);
    }

    /** Saves the stored file of one material to the given place (only for courses I am registered for). */
    public void downloadMaterial(lecturer.CourseMaterial m, java.io.File target) throws AppException {
        if (!dao.isRegistered(getUserId(), m.getCourse().getCourseId())) {
            throw new StudentViewException("You are not registered for " + m.getCourse().getCourseCode() + ".");
        }
        new lecturer.MaterialDAO().saveFileTo(m.getResourceId(), target);
    }


    public AttendanceSummary viewAttendance(Course c) throws AppException {
        if (!dao.isRegistered(getUserId(), c.getCourseId())) {
            throw new StudentViewException("You are not registered for " + c.getCourseCode() + ".");
        }
        return reports.attendanceFor(toStudentInfo(), c);
    }

    public List<MedicalInfo> viewMedicals() throws DatabaseException {
        return reports.medicalsOfStudent(getUserId());
    }

    /**
     * The undergraduate asks for a medical to be approved. The paper certificate is handed to the Technical Officer in person;
     * this only records the request (always PENDING). Returns how many absences were linked to it.
     */
    public int submitMedicalRequest(Course course, java.sql.Date from, java.sql.Date to, String reason) throws AppException {
        if (course == null) throw new InvalidMedicalException("Please choose the course.");
        if (!dao.isRegistered(getUserId(), course.getCourseId())) {
            throw new StudentViewException("You are not registered for " + course.getCourseCode() + ".");
        }
        Medical m = new Medical();
        m.setStudent(toStudentInfo());
        m.setCourse(course);
        m.setDateFrom(from);
        m.setDateTo(to);
        m.setReason(reason == null ? "" : reason.trim());
        return medicalDAO.insert(m);
    }

    public List<CourseResult> viewGrades() throws DatabaseException {
        return reports.resultsForStudent(toStudentInfo());
    }

    public List<SemesterResult> viewGpa() throws DatabaseException {
        return reports.semesterResults(toStudentInfo());
    }

    public UndergraduateDAO data() { return dao; }


    @Override
    public Set<String> getEditableFields() {
        return new HashSet<>(Arrays.asList("email", "contactNumber", "address", "profilePicture"));
    }

    @Override
    public void updateProfile(Map<String, String> data) {
        Set<String> allowed = getEditableFields();      // name, username and registration number stay locked
        for (Map.Entry<String, String> e : data.entrySet()) {
            applyField(e.getKey(), e.getValue(), allowed);
        }
    }

    @Override public String getRoleTable() { return "undergraduate"; }

    @Override
    public Map<String, Object> getRoleColumns() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("registration_number", registrationNumber);
        m.put("batch_id", batch.getBatchId());
        m.put("department_id", department.getDepartmentId());
        m.put("academic_status", academicStatus.name());
        return m;
    }

    @Override
    public void loadRoleDetails(ResultSet rs) throws SQLException {
        registrationNumber = rs.getString("registration_number");
        batch = new Batch(rs.getInt("batch_id"), rs.getString("batch_name"), rs.getString("academic_year"));
        department = new Department(rs.getInt("dept_id"), rs.getString("dept_code"), rs.getString("dept_name"));
        academicStatus = AttemptType.valueOf(rs.getString("academic_status"));
    }

    @Override
    public Map<String, Object> getRoleInput() {
        Map<String, Object> m = new HashMap<>();
        m.put("department", department);
        m.put("batch", batch);
        m.put("registrationNumber", registrationNumber);
        m.put("academicStatus", academicStatus);
        return m;
    }

    @Override
    public void setRoleInput(Map<String, Object> in) {
        if (in.get("department") != null) department = (Department) in.get("department");
        if (in.get("batch") != null) batch = (Batch) in.get("batch");
        if (in.get("registrationNumber") != null) registrationNumber = in.get("registrationNumber").toString();
        if (in.get("academicStatus") != null) academicStatus = (AttemptType) in.get("academicStatus");
    }

    @Override
    public String getRoleSummary() {
        return registrationNumber + " - " + batch.getBatchName() + " - " + department.getDepartmentCode();
    }
}
