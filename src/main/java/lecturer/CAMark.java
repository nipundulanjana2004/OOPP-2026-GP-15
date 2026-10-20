/*
 * ====================================================================
 *  PART    : M2 - LECTURER
 *  PURPOSE : A continuous-assessment mark (quiz, mid exam, assignment ...).
 *  PLUGS IN: Created by MarkEntryPanel for CA components.
 *  OOP     : INHERITANCE (extends AssessmentMark), POLYMORPHISM (own weightedMark).
 * ====================================================================
 */
package lecturer;

import common.StudentInfo;

public class CAMark extends AssessmentMark {
    public CAMark(StudentInfo undergraduate, EvaluationComponent component, double marks, int uploadedBy) {
        super(undergraduate, component, marks, uploadedBy);
    }

    @Override
    public double weightedMark() {
        return getMarksObtained() * getComponent().getWeightage() / 100.0;
    }
}
