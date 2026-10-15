
package common;

public class Course {
    private int courseId = 0;
    private String courseCode = "";
    private String courseName = "";
    private int theoryCredits = 0;
    private int practicalCredits = 0;
    private int semester = 1;
    private Department department = new Department();

    public Course() {
    }

    public Course(int courseId, String courseCode, String courseName, int theoryCredits, int practicalCredits,
                  int semester, Department department) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.theoryCredits = theoryCredits;
        this.practicalCredits = practicalCredits;
        this.semester = semester;
        this.department = department;
    }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public int getTheoryCredits() { return theoryCredits; }
    public void setTheoryCredits(int theoryCredits) { this.theoryCredits = theoryCredits; }
    public int getPracticalCredits() { return practicalCredits; }
    public void setPracticalCredits(int practicalCredits) { this.practicalCredits = practicalCredits; }
    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public int getTotalCredits() {
        return theoryCredits + practicalCredits;
    }

    public boolean hasTheory() {
        return theoryCredits > 0;
    }

    public boolean hasPractical() {
        return practicalCredits > 0;
    }


    public double sessionHours(SessionType type) {
        return 2.0 * (type == SessionType.THEORY ? theoryCredits : practicalCredits);
    }

    @Override
    public String toString() {
        return courseCode + " - " + courseName;
    }
}
