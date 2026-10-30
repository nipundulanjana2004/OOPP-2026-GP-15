/*
 * ====================================================================
 *  PART    : M4 - UNDERGRADUATE
 *  PURPOSE : A medical submitted by an undergraduate is not acceptable (dates reversed, overlaps another medical, not theirs to cancel ...).
 *  PLUGS IN: Undergraduate.submitMedical() / cancelMedical() -> MyMedicalsPanel shows the message.
 *  OOP     : INHERITANCE: extends common.AppException.
 * ====================================================================
 */
package undergraduate;

import common.AppException;

public class MedicalSubmissionException extends AppException {
    public MedicalSubmissionException(String message) {
        super(message);
    }
}
