/*
 * ====================================================================
 *  PART    : M2 - LECTURER
 *  PURPOSE : Thrown when a mark is not between 0 and 100 (NFR-12).
 *  PLUGS IN: AssessmentMark.validate() -> MarkEntryPanel shows the message.
 *  OOP     : INHERITANCE: extends common.AppException.
 * ====================================================================
 */
package lecturer;

import common.AppException;

public class InvalidMarksException extends AppException {
    public InvalidMarksException(String message) {
        super(message);
    }
}
