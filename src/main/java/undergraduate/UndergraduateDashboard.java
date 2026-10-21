package undergraduate;

import common.User;
import common.ui.*;

public class UndergraduateDashboard extends DashboardFrame {

    public UndergraduateDashboard(User user) {
        super(user, "Undergraduate");
        Undergraduate student = (Undergraduate) user;
        addSection("Overview");
        addPage("Dashboard", new UndergraduateHomePanel(student));
        addPage("Notices", new NoticePanel(user));
        addPage("Timetable", new TimetablePanel(user, TimetablePanel.Mode.MINE));
        addSection("Academics");
        addPage("My Courses", new MyCoursesPanel(student));
        addPage("My Attendance", new MyAttendancePanel(student));
        addPage("My Medicals", new MyMedicalsPanel(student));
        addPage("My Grades", new MyGradesPanel(student));
        addSection("Account");
        addPage("My Profile", new ProfilePanel(user));
        start();
    }
}
