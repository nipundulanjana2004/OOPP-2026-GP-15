
package lecturer;

import common.AcademicResource;
import common.Course;

public class CourseMaterial extends AcademicResource {
    private Course course = new Course();
    private String filePath = "";
    private int uploadedBy = 0;
    private String fileName = "";        // name shown to students when they download
    private boolean hasFile = false;      // true when the file itself is stored in the database

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
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName == null ? "" : fileName; }
    public boolean isHasFile() { return hasFile; }
    public void setHasFile(boolean hasFile) { this.hasFile = hasFile; }
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
