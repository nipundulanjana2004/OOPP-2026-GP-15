/*
 * ====================================================================
 *  PART    : M3 - TECHNICAL OFFICER
 *  PURPOSE : A medical certificate record: dates, reason, document, approval status (class diagram: Medical).
 *  PLUGS IN: MedicalDAO saves it; MedicalPanel creates / approves / rejects it; common.ReportService counts APPROVED ones as excused attendance.
 *  OOP     : Encapsulation, business methods (approve / reject / covers), validation with a custom exception.
 * ====================================================================
 */
package officer;

import common.ApprovalStatus;
import common.StudentInfo;

public class Medical {
    private int medicalId = 0;
    private StudentInfo student = new StudentInfo();
    private java.sql.Date dateFrom;
    private java.sql.Date dateTo;
    private String reason = "";
    private String documentPath = "";
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;
    private String verifiedBy = "-";

    public Medical() {
    }

    public int getMedicalId() { return medicalId; }
    public void setMedicalId(int medicalId) { this.medicalId = medicalId; }
    public StudentInfo getStudent() { return student; }
    public void setStudent(StudentInfo student) { this.student = student; }
    public java.sql.Date getDateFrom() { return dateFrom; }
    public void setDateFrom(java.sql.Date dateFrom) { this.dateFrom = dateFrom; }
    public java.sql.Date getDateTo() { return dateTo; }
    public void setDateTo(java.sql.Date dateTo) { this.dateTo = dateTo; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getDocumentPath() { return documentPath; }
    public void setDocumentPath(String documentPath) { this.documentPath = documentPath; }
    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }
    public String getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(String verifiedBy) { this.verifiedBy = verifiedBy; }

    public void approve() { approvalStatus = ApprovalStatus.APPROVED; }
    public void reject() { approvalStatus = ApprovalStatus.REJECTED; }

    public boolean covers(java.sql.Date day) {
        return !day.before(dateFrom) && !day.after(dateTo);
    }

    public void validate() throws InvalidMedicalException {
        if (student == null || student.getUserId() == 0) throw new InvalidMedicalException("Please choose the undergraduate.");
        if (dateFrom == null || dateTo == null) throw new InvalidMedicalException("Both dates are required (format yyyy-MM-dd).");
        if (dateTo.before(dateFrom)) throw new InvalidMedicalException("'To' date cannot be before 'From' date.");
        if (reason == null || reason.trim().isEmpty()) throw new InvalidMedicalException("Please write the reason / diagnosis summary.");
    }
}
