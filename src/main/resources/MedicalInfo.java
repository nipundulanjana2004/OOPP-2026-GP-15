
package common;

public class MedicalInfo {
    private int medicalId = 0;
    private String registrationNumber = "";
    private String studentName = "";
    private java.sql.Date dateFrom;
    private java.sql.Date dateTo;
    private String reason = "";
    private String status = "PENDING";
    private String verifiedBy = "";

    public MedicalInfo() {
    }

    public MedicalInfo(int medicalId, String registrationNumber, String studentName, java.sql.Date dateFrom,
                       java.sql.Date dateTo, String reason, String status, String verifiedBy) {
        this.medicalId = medicalId;
        this.registrationNumber = registrationNumber;
        this.studentName = studentName;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.reason = reason;
        this.status = status;
        this.verifiedBy = verifiedBy;
    }

    public int getMedicalId() { return medicalId; }
    public String getRegistrationNumber() { return registrationNumber; }
    public String getStudentName() { return studentName; }
    public java.sql.Date getDateFrom() { return dateFrom; }
    public java.sql.Date getDateTo() { return dateTo; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
    public String getVerifiedBy() { return verifiedBy; }
}
