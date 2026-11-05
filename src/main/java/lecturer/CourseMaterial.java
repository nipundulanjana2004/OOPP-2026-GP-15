package lecturer;

import common.AcademicResource;
import common.Course;

import java.time.LocalDate;

public class CourseMaterial extends AcademicResource {
    private Course course = new Course();
    private String filePath = "";
    private int uploadedBy = 0;
    private String fileName = "";        // name shown to students when they download
    private boolean hasFile = false;      // true when the file itself is stored in the database

    // Validity period: students can see / download the material only from fromDate to toDate (both days included).
    // null = no limit (materials uploaded before this feature existed).
    private LocalDate fromDate;
    private LocalDate toDate;

    public CourseMaterial() {
    }

    public CourseMaterial(int id, String title, java.sql.Date date, Course course, String filePath, int uploadedBy) {
        super(id, title, date);
        this.course = course;
        this.filePath = filePath;
        this.uploadedBy = uploadedBy;
    }

    public CourseMaterial(int id, String title, java.sql.Date date, Course course, String filePath, int uploadedBy,
                          LocalDate fromDate, LocalDate toDate) {
        this(id, title, date, course, filePath, uploadedBy);
        this.fromDate = fromDate;
        this.toDate = toDate;
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
    public LocalDate getFromDate() { return fromDate; }
    public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }
    public LocalDate getToDate() { return toDate; }
    public void setToDate(LocalDate toDate) { this.toDate = toDate; }

    /** True when the given day is inside the validity period (From Date and To Date themselves are included). */
    public boolean isActiveOn(LocalDate day) {
        if (fromDate != null && day.isBefore(fromDate)) return false;
        if (toDate != null && day.isAfter(toDate)) return false;
        return true;
    }

    /** True when students can use this material today. */
    public boolean isActive() {
        return isActiveOn(LocalDate.now());
    }

    /** "Active", "Scheduled" (not started yet) or "Expired" - shown to the lecturer in the table. */
    public String getStatus() {
        LocalDate today = LocalDate.now();
        if (fromDate != null && today.isBefore(fromDate)) return "Scheduled";
        if (toDate != null && today.isAfter(toDate)) return "Expired";
        return "Active";
    }

    public void updateMaterial(String title, String filePath) {
        setTitle(title);
        this.filePath = filePath;
    }

    @Override
    public String display() {
        return course.getCourseCode() + " | " + getTitle() + " | " + filePath;
    }
}