/*
 * ====================================================================
 *  PART    : M3 - TECHNICAL OFFICER
 *  PURPOSE : Attendance in a THEORY (lecture) session.
 *  PLUGS IN: Created by AttendanceRecord.create() for theory sessions.
 *  OOP     : INHERITANCE + POLYMORPHISM (overrides getSessionLabel / describe).
 * ====================================================================
 */
package officer;

import common.AttendanceStatus;
import common.StudentInfo;

public class TheoryAttendance extends AttendanceRecord {
    public TheoryAttendance(StudentInfo student, Session session, AttendanceStatus status) {
        super(student, session, status);
    }

    @Override
    public String getSessionLabel() {
        return "Lecture";
    }

    @Override
    public String describe() {
        return "Lecture " + getSession().getSessionNumber() + " - " + getStatus();
    }
}
