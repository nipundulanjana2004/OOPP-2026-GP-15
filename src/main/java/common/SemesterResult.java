/*
 * ====================================================================
 *  PART    : COMMON (shared by M1-M4) (M2 publishes, M4 views)
 *  PURPOSE : SGPA of one semester and CGPA up to it for one student.
 *  PLUGS IN: Built by GradeCalculator/ReportService; snapshot stored by M2's ResultDAO.
 *  OOP     : Encapsulation (value object).
 * ====================================================================
 */
package common;

public class SemesterResult {
    private StudentInfo student = new StudentInfo();
    private int semester = 0;
    private double sgpa = 0;
    private double cgpa = 0;
    private double credits = 0;

    public SemesterResult() {
    }

    public SemesterResult(StudentInfo student, int semester, double sgpa, double cgpa, double credits) {
        this.student = student;
        this.semester = semester;
        this.sgpa = sgpa;
        this.cgpa = cgpa;
        this.credits = credits;
    }

    public StudentInfo getStudent() { return student; }
    public int getSemester() { return semester; }
    public double getSgpa() { return sgpa; }
    public double getCgpa() { return cgpa; }
    public double getCredits() { return credits; }
}
