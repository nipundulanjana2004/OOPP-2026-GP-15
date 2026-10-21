/*
 * ====================================================================
 *  PART    : M3 - TECHNICAL OFFICER
 *  PURPOSE : The Technical Officer window: sidebar + pages. Opened by common.RoleRouter after a Technical Officer logs in.
 *  PLUGS IN: common.LoginFrame -> RoleRouter -> new OfficerDashboard(user). Reuses common NoticePanel, TimetablePanel (department mode), ProfilePanel.
 *  OOP     : INHERITANCE (extends common.ui.DashboardFrame), POLYMORPHISM.
 * ====================================================================
 */
package officer;

import common.User;
import common.ui.*;

public class OfficerDashboard extends DashboardFrame {

    public OfficerDashboard(User user) {
        super(user, "Technical Officer");
        TechnicalOfficer officer = (TechnicalOfficer) user;
        addSection("Overview");
        addPage("Dashboard", new OfficerHomePanel(officer));
        addPage("Notices", new NoticePanel(user));
        addPage("Timetable", new TimetablePanel(user, TimetablePanel.Mode.DEPARTMENT));
        addSection("Attendance");
        addPage("Record Attendance", new AttendanceEntryPanel(officer));
        addPage("Attendance Summary", new AttendanceSummaryPanel(officer));
        addPage("Medicals", new MedicalPanel(officer));
        addSection("Account");
        addPage("My Profile", new ProfilePanel(user));
        start();
    }
}
