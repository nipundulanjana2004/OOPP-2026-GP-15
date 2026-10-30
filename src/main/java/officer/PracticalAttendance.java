/*
 * ====================================================================
 *  PART    : M3 - TECHNICAL OFFICER
 *  PURPOSE : Attendance in a PRACTICAL (lab) session.
 *  PLUGS IN: Created by AttendanceRecord.create() for practical sessions.
 *  OOP     : INHERITANCE + POLYMORPHISM (overrides getSessionLabel / describe).
 * ====================================================================
 */
package officer;

import common.AttendanceStatus;
import common.StudentInfo;

public class PracticalAttendance extends AttendanceRecord {
    public PracticalAttendance(StudentInfo student, Session session, AttendanceStatus status) {
        super(student, session, status);
    }

    @Override
    public String getSessionLabel() {
        return "Lab";
    }

    @Override
    public String describe() {
        return "Lab " + getSession().getSessionNumber() + " - " + getStatus();
    }
}
