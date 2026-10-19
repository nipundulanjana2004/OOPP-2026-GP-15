/*
 * ====================================================================
 *  PART    : M3 - TECHNICAL OFFICER
 *  PURPOSE : ABSTRACT attendance of one undergraduate in one session (class diagram: AttendanceRecord).
 *  PLUGS IN: TheoryAttendance and PracticalAttendance extend it; AttendanceDAO loads/saves any AttendanceRecord; AttendanceEntryPanel edits them.
 *  OOP     : ABSTRACTION (abstract methods), INHERITANCE, POLYMORPHISM (create() builds the right child; calculateCredit() / getSessionLabel() differ), ENCAPSULATION.
 * ====================================================================
 */
package officer;

import common.AttendanceStatus;
import common.SessionType;
import common.StudentInfo;

public abstract class AttendanceRecord {
    private int attendanceId = 0;
    private StudentInfo student = new StudentInfo();
    private Session session = new Session();
    private AttendanceStatus status = AttendanceStatus.PRESENT;
    private int medicalId = 0;                 // 0 = no medical linked
    private String medicalStatus = "";         // PENDING / APPROVED / REJECTED / ""

    public AttendanceRecord() {
    }

    public AttendanceRecord(StudentInfo student, Session session, AttendanceStatus status) {
        this.student = student;
        this.session = session;
        this.status = status;
    }

    /** Factory: picks Theory or Practical child from the session type. */
    public static AttendanceRecord create(StudentInfo student, Session session, AttendanceStatus status) {
        return session.getSessionType() == SessionType.THEORY
                ? new TheoryAttendance(student, session, status)
                : new PracticalAttendance(student, session, status);
    }

    public int getAttendanceId() { return attendanceId; }
    public void setAttendanceId(int attendanceId) { this.attendanceId = attendanceId; }
    public StudentInfo getStudent() { return student; }
    public Session getSession() { return session; }
    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }
    public int getMedicalId() { return medicalId; }
    public void setMedicalId(int medicalId) { this.medicalId = medicalId; }
    public String getMedicalStatus() { return medicalStatus; }
    public void setMedicalStatus(String medicalStatus) { this.medicalStatus = medicalStatus == null ? "" : medicalStatus; }

    public boolean isExcused() {
        return status == AttendanceStatus.ABSENT && "APPROVED".equals(medicalStatus);
    }

    /** Hours this record adds to the student's attendance (present, or absent with an approved medical). */
    public double calculateCredit() {
        return (status == AttendanceStatus.PRESENT || isExcused()) ? session.getDurationHours() : 0;
    }

    /** "Lecture" or "Lab" - differs per child. */
    public abstract String getSessionLabel();

    /** Short text for the GUI, differs per child. */
    public abstract String describe();
}
