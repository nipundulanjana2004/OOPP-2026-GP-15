/*
 * ====================================================================
 *  PART    : M2 - LECTURER
 *  PURPOSE : A lecture note / slide / file attached to a course (class diagram: CourseMaterial).
 *  PLUGS IN: MaterialDAO saves it; MaterialPanel lets the lecturer add/modify/delete; undergraduates could download the file path.
 *  OOP     : INHERITANCE (extends common.AcademicResource), POLYMORPHISM (overrides display()).
 * ====================================================================
 */
package lecturer;

import common.AcademicResource;
import common.Course;

public class CourseMaterial extends AcademicResource {
    private Course course = new Course();
    private String filePath = "";
    private int uploadedBy = 0;

    public CourseMaterial() {
    }

    public CourseMaterial(int id, String title, java.sql.Date date, Course course, String filePath, int uploadedBy) {
        super(id, title, date);
        this.course = course;
        this.filePath = filePath;
        this.uploadedBy = uploadedBy;
    }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public int getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(int uploadedBy) { this.uploadedBy = uploadedBy; }

    public void updateMaterial(String title, String filePath) {
        setTitle(title);
        this.filePath = filePath;
    }

    @Override
    public String display() {
        return course.getCourseCode() + " | " + getTitle() + " | " + filePath;
    }
}
