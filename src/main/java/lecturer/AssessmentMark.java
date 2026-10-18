/*
 * ====================================================================
 *  PART    : M2 - LECTURER
 *  PURPOSE : ABSTRACT mark of one undergraduate in one evaluation component (class diagram: AssessmentMark).
 *  PLUGS IN: CAMark and FinalExamMark extend it; MarkDAO saves any AssessmentMark; MarkEntryPanel builds the right child from the component type.
 *  OOP     : ABSTRACTION (abstract validate/weightedMark), INHERITANCE, POLYMORPHISM, ENCAPSULATION.
 * ====================================================================
 */
package lecturer;

import common.StudentInfo;

public abstract class AssessmentMark {
    private int markId = 0;
    private StudentInfo undergraduate = new StudentInfo();
    private EvaluationComponent component = new EvaluationComponent();
    private double marksObtained = 0;
    private int uploadedBy = 0;
    private java.sql.Date uploadDate = new java.sql.Date(System.currentTimeMillis());

    public AssessmentMark() {
    }

    public AssessmentMark(StudentInfo undergraduate, EvaluationComponent component, double marksObtained, int uploadedBy) {
        this.undergraduate = undergraduate;
        this.component = component;
        this.marksObtained = marksObtained;
        this.uploadedBy = uploadedBy;
    }

    public int getMarkId() { return markId; }
    public void setMarkId(int markId) { this.markId = markId; }
    public StudentInfo getUndergraduate() { return undergraduate; }
    public EvaluationComponent getComponent() { return component; }
    public double getMarksObtained() { return marksObtained; }
    public void setMarksObtained(double marksObtained) { this.marksObtained = marksObtained; }
    public int getUploadedBy() { return uploadedBy; }
    public java.sql.Date getUploadDate() { return uploadDate; }

    /** Every kind of mark must be out of 100. Children add their own rules. */
    public void validate() throws InvalidMarksException {
        if (Double.isNaN(marksObtained) || marksObtained < 0 || marksObtained > 100) {
            throw new InvalidMarksException("Marks for " + undergraduate.getRegistrationNumber() + " must be between 0 and 100.");
        }
    }

    /** Contribution of this mark to the course total (out of 100). */
    public abstract double weightedMark();
}
