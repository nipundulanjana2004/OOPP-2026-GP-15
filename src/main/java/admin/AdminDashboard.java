/*
 * ====================================================================
 *  PART    : M1 - ADMIN
 *  PURPOSE : The Admin window: sidebar + pages. Opened by common.RoleRouter after an Admin logs in.
 *  PLUGS IN: common.LoginFrame -> RoleRouter -> new AdminDashboard(user).
 *  OOP     : INHERITANCE (extends common.ui.DashboardFrame), POLYMORPHISM (same frame, Admin-specific pages).
 * ====================================================================
 */
package admin;

import common.User;
import common.ui.DashboardFrame;
import common.ui.ProfilePanel;

public class AdminDashboard extends DashboardFrame {

    public AdminDashboard(User user) {
        super(user, "Administrator");
        Admin admin = (Admin) user;
        addSection("Overview");
        addPage("Dashboard", new AdminHomePanel(admin));
        addSection("Manage");
        addPage("Users", new UserManagementPanel(admin));
        addPage("Courses", new CourseManagementPanel(admin));
        addPage("Registrations", new RegistrationPanel(admin));
        addPage("Notices", new NoticeManagementPanel(admin));
        addPage("Timetables", new TimetableManagementPanel(admin));
        addSection("Account");
        addPage("My Profile", new ProfilePanel(user));
        start();
    }
}
