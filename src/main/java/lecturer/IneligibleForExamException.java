/*
 * ====================================================================
 *  PART    : M2 - LECTURER
 *  PURPOSE : Thrown when a final-exam mark is entered for a student who is not eligible (attendance < 80% or CA < 40%).
 *  PLUGS IN: FinalExamMark.validate() -> MarkEntryPanel.
 *  OOP     : INHERITANCE: extends common.AppException.
 * ====================================================================
 */
package lecturer;

import common.AppException;

public class IneligibleForExamException extends AppException {
    public IneligibleForExamException(String studentName, String reason) {
        super(studentName + " is not eligible to sit the final exam (" + reason + "). The mark was not saved.");
    }
}
