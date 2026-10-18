
package common;

public class GradeScale {
    private int gradeId = 0;
    private String grade = "";
    private double gradePoint = 0;
    private double minMarks = 0;
    private double maxMarks = 0;

    public GradeScale() {
    }

    public GradeScale(int gradeId, String grade, double gradePoint, double minMarks, double maxMarks) {
        this.gradeId = gradeId;
        this.grade = grade;
        this.gradePoint = gradePoint;
        this.minMarks = minMarks;
        this.maxMarks = maxMarks;
    }

    public int getGradeId() { return gradeId; }
    public String getGrade() { return grade; }
    public double getGradePoint() { return gradePoint; }
    public double getMinMarks() { return minMarks; }
    public double getMaxMarks() { return maxMarks; }

    public boolean contains(double marks) {
        return marks >= minMarks && marks < maxMarks + 1;
    }
}
