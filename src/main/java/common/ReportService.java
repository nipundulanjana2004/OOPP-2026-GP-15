
package common;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class ReportService extends BaseDAO {
    private final LookupDAO lookup = new LookupDAO();
    private final CourseDAO courseDAO = new CourseDAO();


    public AttendanceSummary attendanceFor(StudentInfo student, Course course) throws DatabaseException {
        AttendanceTally theory = new AttendanceTally();
        AttendanceTally practical = new AttendanceTally();
        // Only sessions that have been conducted (someone has attendance rows for them) are counted.
        String sql = "SELECT cs.session_type, SUM(cs.duration_hours) AS total, " +
                "SUM(CASE WHEN a.status='PRESENT' THEN cs.duration_hours ELSE 0 END) AS present, " +
                "SUM(CASE WHEN a.status='ABSENT' AND m.approval_status='APPROVED' THEN cs.duration_hours ELSE 0 END) AS excused " +
                "FROM class_session cs LEFT JOIN attendance a ON a.session_id=cs.session_id AND a.undergraduate_id=? " +
                "LEFT JOIN medical m ON m.medical_id=a.medical_id WHERE cs.course_id=? " +
                "AND EXISTS (SELECT 1 FROM attendance x WHERE x.session_id=cs.session_id) GROUP BY cs.session_type";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, student.getUserId());
            ps.setInt(2, course.getCourseId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AttendanceTally t = new AttendanceTally(rs.getDouble("total"), rs.getDouble("present"), rs.getDouble("excused"));
                    if ("THEORY".equals(rs.getString("session_type"))) theory = t; else practical = t;
                }
            }
            AttendanceSummary summary = new AttendanceSummary(student, course, theory, practical);
            String msql = "SELECT COUNT(DISTINCT m.medical_id) AS cnt, COUNT(DISTINCT CASE WHEN m.approval_status='APPROVED' THEN m.medical_id END) AS ok " +
                    "FROM attendance a JOIN class_session cs ON cs.session_id=a.session_id JOIN medical m ON m.medical_id=a.medical_id " +
                    "WHERE a.undergraduate_id=? AND cs.course_id=?";
            try (PreparedStatement ps2 = conn().prepareStatement(msql)) {
                ps2.setInt(1, student.getUserId());
                ps2.setInt(2, course.getCourseId());
                try (ResultSet rs2 = ps2.executeQuery()) {
                    if (rs2.next()) {
                        summary.setMedicalCount(rs2.getInt("cnt"));
                        summary.setApprovedMedicalCount(rs2.getInt("ok"));
                    }
                }
            }
            return summary;
        } catch (SQLException e) {
            throw wrap("calculating attendance", e);
        }
    }

    public List<AttendanceSummary> attendanceForBatch(Course course) throws DatabaseException {
        List<AttendanceSummary> list = new ArrayList<>();
        for (StudentInfo s : lookup.studentsOfCourse(course.getCourseId())) {
            list.add(attendanceFor(s, course));
        }
        return list;
    }


    public CourseResult resultFor(StudentInfo student, Course course) throws DatabaseException {
        CourseResult r = new CourseResult(student, course);
        double caSum = 0, caWeight = 0, finSum = 0, finWeight = 0;
        boolean finalSeen = false;
        String sql = "SELECT ec.component_type, ec.weightage, m.marks_obtained FROM evaluation_component ec " +
                "LEFT JOIN mark m ON m.component_id=ec.component_id AND m.undergraduate_id=? WHERE ec.course_id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, student.getUserId());
            ps.setInt(2, course.getCourseId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    double w = rs.getDouble("weightage");
                    double mark = rs.getDouble("marks_obtained");
                    boolean has = rs.getObject("marks_obtained") != null;
                    if ("CA".equals(rs.getString("component_type"))) {
                        caWeight += w;
                        if (has) caSum += mark * w;
                    } else if (has) {
                        finalSeen = true;
                        finSum += mark * w;
                        finWeight += w;
                    }
                }
            }
        } catch (SQLException e) {
            throw wrap("calculating marks", e);
        }
        r.setCa(caSum, caWeight);
        if (finalSeen) r.setFinalMark(finSum / finWeight, finWeight);
        r.setAttendance(attendanceFor(student, course));
        r.computeEligibility();
        r.computeTotalMarks();
        r.assignGrade();
        return r;
    }

    public List<CourseResult> resultsForBatch(Course course) throws DatabaseException {
        List<CourseResult> list = new ArrayList<>();
        for (StudentInfo s : lookup.studentsOfCourse(course.getCourseId())) {
            list.add(resultFor(s, course));
        }
        return list;
    }

    public List<CourseResult> resultsForStudent(StudentInfo student) throws DatabaseException {
        List<CourseResult> list = new ArrayList<>();
        for (Course c : courseDAO.findByUndergraduate(student.getUserId())) {
            list.add(resultFor(student, c));
        }
        return list;
    }

    public List<SemesterResult> semesterResults(StudentInfo student) throws DatabaseException {
        List<CourseResult> all = resultsForStudent(student);
        TreeSet<Integer> semesters = new TreeSet<>();
        for (CourseResult r : all) semesters.add(r.getCourse().getSemester());
        List<SemesterResult> out = new ArrayList<>();
        for (int sem : semesters) {
            List<CourseResult> thisSem = new ArrayList<>();
            List<CourseResult> upTo = new ArrayList<>();
            for (CourseResult r : all) {
                if (r.getCourse().getSemester() == sem) thisSem.add(r);
                if (r.getCourse().getSemester() <= sem) upTo.add(r);
            }
            out.add(new SemesterResult(student, sem, GradeCalculator.calculateGPA(thisSem),
                    GradeCalculator.calculateGPA(upTo), GradeCalculator.countedCredits(thisSem)));
        }
        return out;
    }


    private List<MedicalInfo> medicals(String where, int param) throws DatabaseException {
        List<MedicalInfo> list = new ArrayList<>();
        String sql = "SELECT m.*, g.registration_number, CONCAT(u.first_name,' ',u.last_name) AS sname, " +
                "CONCAT(o.first_name,' ',o.last_name) AS officer FROM medical m " +
                "JOIN undergraduate g ON g.user_id=m.undergraduate_id JOIN app_user u ON u.user_id=g.user_id " +
                "LEFT JOIN app_user o ON o.user_id=m.verified_by_officer_id " + where + " ORDER BY m.date_from DESC, m.medical_id DESC";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            if (param > 0) ps.setInt(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String officer = rs.getString("officer");
                    list.add(new MedicalInfo(rs.getInt("medical_id"), rs.getString("registration_number"), rs.getString("sname"),
                            rs.getDate("date_from"), rs.getDate("date_to"), rs.getString("reason"),
                            rs.getString("approval_status"), officer == null ? "-" : officer));
                }
            }
        } catch (SQLException e) {
            throw wrap("loading medicals", e);
        }
        return list;
    }

    public List<MedicalInfo> medicalsOfStudent(int undergraduateId) throws DatabaseException {
        return medicals("WHERE m.undergraduate_id=?", undergraduateId);
    }

    public List<MedicalInfo> medicalsOfCourse(int courseId) throws DatabaseException {
        return medicals("WHERE m.undergraduate_id IN (SELECT undergraduate_id FROM course_registration WHERE course_id=?)", courseId);
    }
}
