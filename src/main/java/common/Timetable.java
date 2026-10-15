/*
 * ====================================================================
 *  PART    : COMMON (shared by M1-M4) (Admin M1 manages; M2/M3/M4 view)
 *  PURPOSE : One weekly timetable slot.
 *  PLUGS IN: TimetableAdminDAO (M1) writes it; TimetablePanel shows it to every role.
 *  OOP     : Encapsulation + composition (HAS a Course, Batch, Department).
 * ====================================================================
 */
package common;

import java.sql.Time;

public class Timetable {
    private int timetableId = 0;
    private Course course = new Course();
    private SessionType sessionType = SessionType.THEORY;
    private String dayOfWeek = "Monday";
    private Time startTime = Time.valueOf("08:00:00");
    private Time endTime = Time.valueOf("10:00:00");
    private String venue = "";
    private Batch batch = new Batch();
    private Department department = new Department();

    public Timetable() {
    }

    public int getTimetableId() { return timetableId; }
    public void setTimetableId(int timetableId) { this.timetableId = timetableId; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public SessionType getSessionType() { return sessionType; }
    public void setSessionType(SessionType sessionType) { this.sessionType = sessionType; }
    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public Time getStartTime() { return startTime; }
    public void setStartTime(Time startTime) { this.startTime = startTime; }
    public Time getEndTime() { return endTime; }
    public void setEndTime(Time endTime) { this.endTime = endTime; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public Batch getBatch() { return batch; }
    public void setBatch(Batch batch) { this.batch = batch; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
}
