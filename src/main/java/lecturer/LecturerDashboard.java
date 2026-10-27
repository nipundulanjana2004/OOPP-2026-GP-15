/*
 * ====================================================================
 *  PART    : M2 - LECTURER
 *  PURPOSE : The Lecturer window: sidebar + pages. Opened by common.RoleRouter after a Lecturer logs in.
 *  PLUGS IN: common.LoginFrame -> RoleRouter -> new LecturerDashboard(user). Reuses common NoticePanel, TimetablePanel, ProfilePanel.
 *  OOP     : INHERITANCE (extends common.ui.DashboardFrame), POLYMORPHISM.
 * ====================================================================
 */
package lecturer;

import common.User;
import common.ui.*;

public class LecturerDashboard extends DashboardFrame {

    public LecturerDashboard(User user) {
        super(user, "Lecturer");
        Lecturer lecturer = (Lecturer) user;
        addSection("Overview");
        addPage("My Courses", new LecturerHomePanel(lecturer));
        addPage("Notices", new NoticePanel(user));
        addPage("Timetable", new TimetablePanel(user, TimetablePanel.Mode.ALL));
        addSection("Teaching");
        addPage("Materials", new MaterialPanel(lecturer));
        addPage("Evaluation Setup", new EvaluationSetupPanel(lecturer));
        addPage("Upload Marks", new MarkEntryPanel(lecturer));
        addSection("Students");
        addPage("Undergraduates", new UndergraduateDetailsPanel(lecturer));
        addPage("Eligibility", new EligibilityPanel(lecturer));
        addPage("Attendance & Medicals", new AttendanceMedicalPanel(lecturer));
        addPage("Results & GPA", new ResultsPanel(lecturer));
        addSection("Account");
        addPage("My Profile", new ProfilePanel(user));
        start();
    }
}
