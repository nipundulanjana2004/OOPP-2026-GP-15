/*
 * ====================================================================
 *  PART    : M3 - TECHNICAL OFFICER
 *  PURPOSE : One conducted class (theory or practical) of a course on a date (class diagram: Session).
 *  PLUGS IN: SessionDAO creates the 15 sessions per type; AttendanceEntryPanel records attendance against it; common.ReportService sums the hours.
 *  OOP     : Encapsulation + composition (HAS a Course).
 * ====================================================================
 */
package officer;

import common.Course;
import common.SessionType;

public class Session {
    private int sessionId = 0;
    private Course course = new Course();
    private SessionType sessionType = SessionType.THEORY;
    private int sessionNumber = 1;
    private java.sql.Date sessionDate = new java.sql.Date(System.currentTimeMillis());
    private double durationHours = 2;

    public Session() {
    }

    public Session(int sessionId, Course course, SessionType sessionType, int sessionNumber, java.sql.Date sessionDate, double durationHours) {
        this.sessionId = sessionId;
        this.course = course;
        this.sessionType = sessionType;
        this.sessionNumber = sessionNumber;
        this.sessionDate = sessionDate;
        this.durationHours = durationHours;
    }

    public int getSessionId() { return sessionId; }
    public Course getCourse() { return course; }
    public SessionType getSessionType() { return sessionType; }
    public int getSessionNumber() { return sessionNumber; }
    public java.sql.Date getSessionDate() { return sessionDate; }
    public double getDurationHours() { return durationHours; }

    @Override
    public String toString() {
        return "Session " + sessionNumber + "  -  " + sessionDate + "  (" + (int) durationHours + " h)";
    }
}
