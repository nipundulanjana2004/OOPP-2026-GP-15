
package common;

public class AttendanceSummary {
    private StudentInfo student = new StudentInfo();
    private Course course = new Course();
    private AttendanceTally theory = new AttendanceTally();
    private AttendanceTally practical = new AttendanceTally();
    private int medicalCount = 0;
    private int approvedMedicalCount = 0;

    public AttendanceSummary() {
    }

    public AttendanceSummary(StudentInfo student, Course course, AttendanceTally theory, AttendanceTally practical) {
        this.student = student;
        this.course = course;
        this.theory = theory;
        this.practical = practical;
    }

    public StudentInfo getStudent() { return student; }
    public Course getCourse() { return course; }
    public AttendanceTally getTheory() { return theory; }
    public AttendanceTally getPractical() { return practical; }
    public int getMedicalCount() { return medicalCount; }
    public void setMedicalCount(int medicalCount) { this.medicalCount = medicalCount; }
    public int getApprovedMedicalCount() { return approvedMedicalCount; }
    public void setApprovedMedicalCount(int approvedMedicalCount) { this.approvedMedicalCount = approvedMedicalCount; }

    public AttendanceTally getCombined() { return theory.plus(practical); }
    public boolean hasAnySessions() { return !theory.isEmpty() || !practical.isEmpty(); }

    public double rawTheory() { return AttendanceCalculator.rawPercent(theory); }
    public double rawPractical() { return AttendanceCalculator.rawPercent(practical); }
    public double rawCombined() { return AttendanceCalculator.rawPercent(getCombined()); }
    public double effTheory() { return AttendanceCalculator.theoryPercent(theory); }
    public double effPractical() { return AttendanceCalculator.practicalPercent(practical); }
    public double effCombined() { return AttendanceCalculator.combinedPercent(theory, practical); }

    public boolean isEligible() {
        return AttendanceCalculator.isEligible(effCombined(), hasAnySessions());
    }

    public String scenario() {
        return AttendanceCalculator.scenario(rawCombined(), effCombined(), approvedMedicalCount > 0);
    }
}
