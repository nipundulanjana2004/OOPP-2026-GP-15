
package undergraduate;

import common.AttemptType;
import common.Course;

public class CourseRegistration {
    private Course course = new Course();
    private String academicYear = "";
    private AttemptType attemptType = AttemptType.PROPER;

    public CourseRegistration() {
    }

    public CourseRegistration(Course course, String academicYear, AttemptType attemptType) {
        this.course = course;
        this.academicYear = academicYear;
        this.attemptType = attemptType;
    }

    public Course getCourse() { return course; }
    public String getAcademicYear() { return academicYear; }
    public AttemptType getAttemptType() { return attemptType; }
}
