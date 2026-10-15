/*
 * ====================================================================
 *  PART    : COMMON (shared by M1-M4) (parent of Admin M1, Lecturer M2, TechnicalOfficer M3, Undergraduate M4)
 *  PURPOSE : ABSTRACT parent of all users - holds the fields every user has (Group 15 class diagram).
 *  PLUGS IN: Each member extends it in their own package. UserDAO builds the right subclass from the 'role' column.
 *  OOP     : ABSTRACTION (abstract class + abstract methods), INHERITANCE (4 subclasses), ENCAPSULATION, POLYMORPHISM (updateProfile / getRoleColumns behave differently per subclass).
 * ====================================================================
 */
package common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

public abstract class User {
    private int userId = 0;
    private String username = "";
    private String passwordHash = "";
    private String firstName = "";
    private String lastName = "";
    private String email = "";
    private String contactNumber = "";
    private String address = "";
    private String profilePicture = "";
    private Role role = Role.ADMIN;
    private boolean isActive = true;

    public User() {
    }

    public User(Role role) {
        this.role = role;
    }

    public User(int userId, String username, Role role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    // ---------------- getters / setters (encapsulation) ----------------
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getFullName() {
        return (firstName + " " + lastName).trim();
    }

    public String getInitials() {
        String a = firstName.isEmpty() ? "?" : firstName.substring(0, 1);
        String b = lastName.isEmpty() ? "" : lastName.substring(0, 1);
        return (a + b).toUpperCase();
    }

    // ---------------- behaviour from the class diagram ----------------
    public boolean login(String password) {
        return isActive && PasswordUtil.matches(password, passwordHash);
    }

    public void logout() {
        LoginContext.clear();
    }

    /**
     * Copies the values in 'data' into this user, but ONLY for the fields this role is allowed to edit.
     * Keys: firstName, lastName, email, contactNumber, address, profilePicture, username.
     */
    public abstract void updateProfile(Map<String, String> data);

    /** Profile fields this role may edit (the GUI locks everything else). */
    public abstract Set<String> getEditableFields();

    /** Name of the role-specific MySQL table, e.g. "lecturer". */
    public abstract String getRoleTable();

    /** Role-specific columns to save, e.g. designation, department_id. */
    public abstract Map<String, Object> getRoleColumns();

    /** Reads the role-specific columns from a joined row (see UserDAO). */
    public abstract void loadRoleDetails(ResultSet rs) throws SQLException;

    /**
     * Role-specific form values (used by the Admin's user form). Keys: department (Department), batch (Batch),
     * designation, registrationNumber (String), academicStatus (AttemptType). Each subclass reports only its own.
     */
    public abstract Map<String, Object> getRoleInput();

    /** Reverse of getRoleInput(): copies form values into the subclass's own fields. */
    public abstract void setRoleInput(Map<String, Object> input);

    /** Text for the dashboard header, e.g. "Lecturer - ICT". */
    public abstract String getRoleSummary();

    /** Department of this user (0 = none). Lecturer, TechnicalOfficer and Undergraduate override this. */
    public int getDepartmentId() {
        return 0;
    }

    /** Batch of this user (0 = none). Only Undergraduate overrides this. */
    public int getBatchId() {
        return 0;
    }

    /** Helper for subclasses: applies one field, if it is in the allowed set. */
    protected void applyField(String key, String value, Set<String> allowed) {
        if (value == null || !allowed.contains(key)) {
            return;
        }
        switch (key) {
            case "firstName": this.firstName = value; break;
            case "lastName": this.lastName = value; break;
            case "email": this.email = value; break;
            case "contactNumber": this.contactNumber = value; break;
            case "address": this.address = value; break;
            case "profilePicture": this.profilePicture = value; break;
            case "username": this.username = value; break;
            default: break;
        }
    }

    @Override
    public String toString() {
        return getFullName() + " (" + username + ")";
    }
}
