/*
 * ====================================================================
 *  PART    : COMMON (shared by M1-M4)
 *  PURPOSE : Light-weight read-only view of an undergraduate (id, reg no, name, batch ...).
 *  PLUGS IN: Returned by LookupDAO; used by M2 (marks entry), M3 (attendance entry) and M1 (lists).
 *  OOP     : Encapsulation (value object).
 * ====================================================================
 */
package common;

public class StudentInfo {
    private int userId = 0;
    private String registrationNumber = "";
    private String fullName = "";
    private String batchName = "";
    private String departmentName = "";
    private String attemptType = "PROPER";
    private String email = "";
    private String contactNumber = "";

    public StudentInfo() {
    }

    public StudentInfo(int userId, String registrationNumber, String fullName) {
        this.userId = userId;
        this.registrationNumber = registrationNumber;
        this.fullName = fullName;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getBatchName() { return batchName; }
    public void setBatchName(String batchName) { this.batchName = batchName; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public String getAttemptType() { return attemptType; }
    public void setAttemptType(String attemptType) { this.attemptType = attemptType; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    @Override
    public String toString() {
        return registrationNumber + " - " + fullName;
    }
}
