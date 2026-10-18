/*
 * ====================================================================
 *  PART    : M1 - ADMIN
 *  PURPOSE : The Admin user (class diagram: Admin). Creates/maintains users, courses, notices, timetables by delegating to the admin DAOs.
 *  PLUGS IN: Created by UserFactory when role = ADMIN; used by AdminDashboard and the 5 management panels.
 *  OOP     : INHERITANCE (extends common.User), POLYMORPHISM (overrides updateProfile / getEditableFields / getRoleTable), ENCAPSULATION.
 * ====================================================================
 */
package admin;

import common.*;

import java.sql.ResultSet;
import java.util.*;

public class Admin extends User {
    private final UserAdminDAO userDAO = new UserAdminDAO();
    private final CourseAdminDAO courseDAO = new CourseAdminDAO();
    private final NoticeAdminDAO noticeDAO = new NoticeAdminDAO();
    private final TimetableAdminDAO timetableDAO = new TimetableAdminDAO();

    public Admin() {
        super(Role.ADMIN);
    }

    // ---------- diagram methods ----------
    public void createUser(User u, String plainPassword) throws AppException {
        userDAO.createUser(u, plainPassword);
    }

    public void updateUser(User u) throws AppException {
        userDAO.update(u);
    }

    public void deactivateUser(int id, boolean active) throws AppException {
        if (id == getUserId()) {
            throw new ValidationException("You cannot deactivate your own account.");
        }
        userDAO.setActive(id, active);
    }

    public void deleteUser(int id) throws AppException {
        if (id == getUserId()) {
            throw new ValidationException("You cannot delete your own account.");
        }
        userDAO.delete(id);
    }

    public void resetPassword(int id, String plain) throws AppException {
        if (plain == null || plain.length() < 6) {
            throw new ValidationException("The new password must have at least 6 characters.");
        }
        userDAO.updatePassword(id, plain);
    }

    public CourseAdminDAO manageCourses() { return courseDAO; }
    public NoticeAdminDAO manageNotices() { return noticeDAO; }
    public TimetableAdminDAO manageTimetables() { return timetableDAO; }
    public UserAdminDAO manageUsers() { return userDAO; }

    // ---------- overrides of the abstract User methods ----------
    @Override
    public Set<String> getEditableFields() {
        return new HashSet<>(Arrays.asList("username", "firstName", "lastName", "email", "contactNumber", "address", "profilePicture"));
    }

    @Override
    public void updateProfile(Map<String, String> data) {
        Set<String> allowed = getEditableFields();            // Admin may change everything
        for (Map.Entry<String, String> e : data.entrySet()) {
            applyField(e.getKey(), e.getValue(), allowed);
        }
    }

    @Override public String getRoleTable() { return "admin"; }
    @Override public Map<String, Object> getRoleColumns() { return new LinkedHashMap<>(); }
    @Override public void loadRoleDetails(ResultSet rs) { /* the admin table has no extra columns */ }
    @Override public Map<String, Object> getRoleInput() { return new HashMap<>(); }
    @Override public void setRoleInput(Map<String, Object> input) { /* nothing role specific */ }
    @Override public String getRoleSummary() { return "Administrator - Faculty of Technology"; }
}
