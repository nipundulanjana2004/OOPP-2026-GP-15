
package common;

public class CourseResult {
    private StudentInfo student = new StudentInfo();
    private Course course = new Course();
    private double caMarks = 0;            // CA normalised to 100
    private double caWeightedSum = 0;      // sum(mark * weight)
    private double caWeightTotal = 0;
    private double finalExamMarks = 0;     // out of 100
    private boolean finalPresent = false;
    private double totalMarks = 0;         // out of 100, only when final is present
    private double attendanceTheoryPercent = 0;
    private double attendancePracticalPercent = 0;
    private double attendanceOverallPercent = 0;
    private boolean anySessions = false;
    private boolean caEligible = false;
    private boolean attendanceEligible = false;
    private boolean overallEligible = false;
    private GradeScale grade = null;

    public CourseResult() {
    }

    public CourseResult(StudentInfo student, Course course) {
        this.student = student;
        this.course = course;
    }

    public StudentInfo getStudent() { return student; }
    public Course getCourse() { return course; }
    public double getCaMarks() { return caMarks; }
    public double getFinalExamMarks() { return finalExamMarks; }
    public boolean isFinalPresent() { return finalPresent; }
    public double getTotalMarks() { return totalMarks; }
    public double getAttendanceTheoryPercent() { return attendanceTheoryPercent; }
    public double getAttendancePracticalPercent() { return attendancePracticalPercent; }
    public double getAttendanceOverallPercent() { return attendanceOverallPercent; }
    public boolean isCaEligible() { return caEligible; }
    public boolean isAttendanceEligible() { return attendanceEligible; }
    public boolean isOverallEligible() { return overallEligible; }
    public GradeScale getGrade() { return grade; }

    public void setCa(double weightedSum, double weightTotal) {
        this.caWeightedSum = weightedSum;
        this.caWeightTotal = weightTotal;
        this.caMarks = weightTotal <= 0 ? 0 : weightedSum / weightTotal;
    }

    public void setFinalMark(double mark, double weight) {
        this.finalExamMarks = mark;
        this.finalPresent = true;
        this.totalMarks = (caWeightedSum + mark * weight) / 100.0;
    }

    public void setAttendance(AttendanceSummary a) {
        this.attendanceTheoryPercent = a.effTheory();
        this.attendancePracticalPercent = a.effPractical();
        this.attendanceOverallPercent = a.effCombined();
        this.anySessions = a.hasAnySessions();
    }

    public void computeEligibility() {
        caEligible = caMarks + 1e-9 >= 40.0;
        attendanceEligible = AttendanceCalculator.isEligible(attendanceOverallPercent, anySessions);
        overallEligible = caEligible && attendanceEligible;
    }

    public void computeTotalMarks() {
        if (!finalPresent) {
            totalMarks = 0;
        }
    }

    public void assignGrade() {
        grade = (finalPresent && overallEligible) ? GradeCalculator.assignGrade(totalMarks) : null;
    }

    public String eligibilityText() {
        return overallEligible ? "ELIGIBLE" : "NOT ELIGIBLE";
    }

    public String eligibilityReason() {
        if (overallEligible) return "Meets attendance (>=80%) and CA (>=40%)";
        StringBuilder sb = new StringBuilder();
        if (!attendanceEligible) sb.append(String.format("Attendance %.1f%% < 80%%", attendanceOverallPercent));
        if (!caEligible) {
            if (sb.length() > 0) sb.append("; ");
            sb.append(String.format("CA %.1f%% < 40%%", caMarks));
        }
        return sb.toString();
    }


    public String gradeText() {
        if (!overallEligible) return "Not eligible";
        if (!finalPresent) return "Pending";
        return grade == null ? "-" : grade.getGrade();
    }


    public double gradePointOrMinusOne() {
        return grade == null ? -1 : grade.getGradePoint();
    }
}
