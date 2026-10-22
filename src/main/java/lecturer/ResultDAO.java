/*
 * ====================================================================
 *  PART    : M2 - LECTURER
 *  PURPOSE : Saves a snapshot of the computed results into course_result and semester_result (ER diagram tables).
 *  PLUGS IN: ResultsPanel 'Save results snapshot' button. The live numbers always come from common.ReportService.
 *  OOP     : INHERITANCE: extends common.BaseDAO; JDBC upsert.
 * ====================================================================
 */
package lecturer;

import common.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ResultDAO extends BaseDAO {

    public void saveCourseResult(CourseResult r) throws DatabaseException {
        String sql = "INSERT INTO course_result (undergraduate_id,course_id,ca_marks,final_exam_marks,total_marks,attendance_theory_percent," +
                "attendance_practical_percent,attendance_overall_percent,ca_eligible,attendance_eligible,overall_eligible,grade_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE ca_marks=VALUES(ca_marks), final_exam_marks=VALUES(final_exam_marks), total_marks=VALUES(total_marks), " +
                "attendance_theory_percent=VALUES(attendance_theory_percent), attendance_practical_percent=VALUES(attendance_practical_percent), " +
                "attendance_overall_percent=VALUES(attendance_overall_percent), ca_eligible=VALUES(ca_eligible), attendance_eligible=VALUES(attendance_eligible), " +
                "overall_eligible=VALUES(overall_eligible), grade_id=VALUES(grade_id)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, r.getStudent().getUserId());
            ps.setInt(2, r.getCourse().getCourseId());
            ps.setDouble(3, r.getCaMarks());
            ps.setObject(4, r.isFinalPresent() ? r.getFinalExamMarks() : null);
            ps.setObject(5, r.isFinalPresent() ? r.getTotalMarks() : null);
            ps.setDouble(6, r.getAttendanceTheoryPercent());
            ps.setDouble(7, r.getAttendancePracticalPercent());
            ps.setDouble(8, r.getAttendanceOverallPercent());
            ps.setInt(9, r.isCaEligible() ? 1 : 0);
            ps.setInt(10, r.isAttendanceEligible() ? 1 : 0);
            ps.setInt(11, r.isOverallEligible() ? 1 : 0);
            ps.setObject(12, r.getGrade() == null ? null : r.getGrade().getGradeId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("saving the course result", e);
        }
    }

    public void saveSemesterResult(SemesterResult s) throws DatabaseException {
        String sql = "INSERT INTO semester_result (undergraduate_id,semester,sgpa,cgpa) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE sgpa=VALUES(sgpa), cgpa=VALUES(cgpa)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, s.getStudent().getUserId());
            ps.setInt(2, s.getSemester());
            ps.setDouble(3, s.getSgpa());
            ps.setDouble(4, s.getCgpa());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("saving the semester result", e);
        }
    }

    /** Saves course results of the whole batch and refreshes SGPA/CGPA of those students. */
    public int publish(List<CourseResult> results, ReportService service) throws DatabaseException {
        int n = 0;
        for (CourseResult r : results) {
            saveCourseResult(r);
            n++;
            for (SemesterResult s : service.semesterResults(r.getStudent())) saveSemesterResult(s);
        }
        return n;
    }
}
