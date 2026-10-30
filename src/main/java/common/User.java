
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


    public boolean login(String password) {
        return isActive && PasswordUtil.matches(password, passwordHash);
    }

    public void logout() {
        LoginContext.clear();
    }


    public abstract void updateProfile(Map<String, String> data);


    public abstract Set<String> getEditableFields();


    public abstract String getRoleTable();


    public abstract Map<String, Object> getRoleColumns();


    public abstract void loadRoleDetails(ResultSet rs) throws SQLException;


    public abstract Map<String, Object> getRoleInput();


    public abstract void setRoleInput(Map<String, Object> input);


    public abstract String getRoleSummary();


    public int getDepartmentId() {
        return 0;
    }


    public int getBatchId() {
        return 0;
    }


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
