/*
 * ====================================================================
 *  PART    : M2 - LECTURER
 *  PURPOSE : A final-exam mark. Extra rule: the undergraduate must be ELIGIBLE (attendance >= 80% and CA >= 40%).
 *  PLUGS IN: Created by MarkEntryPanel for FINAL components; eligibility comes from common.ReportService.
 *  OOP     : INHERITANCE + POLYMORPHISM (validate() is stricter than the parent), custom exception.
 * ====================================================================
 */
package lecturer;

import common.StudentInfo;

public class FinalExamMark extends AssessmentMark {
    private boolean eligible = true;
    private String ineligibleReason = "";

    public FinalExamMark(StudentInfo undergraduate, EvaluationComponent component, double marks, int uploadedBy,
                         boolean eligible, String reason) {
        super(undergraduate, component, marks, uploadedBy);
        this.eligible = eligible;
        this.ineligibleReason = reason;
    }

    @Override
    public void validate() throws InvalidMarksException {
        super.validate();
    }

    /** Separate check so the GUI can report it with its own exception type. */
    public void checkEligibility() throws IneligibleForExamException {
        if (!eligible) {
            throw new IneligibleForExamException(getUndergraduate().getFullName(), ineligibleReason);
        }
    }

    @Override
    public double weightedMark() {
        return getMarksObtained() * getComponent().getWeightage() / 100.0;
    }
}
