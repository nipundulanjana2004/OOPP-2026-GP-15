/*
 * ====================================================================
 *  PART    : M3 - TECHNICAL OFFICER
 *  PURPOSE : A medical record is invalid (dates reversed, missing reason ...).
 *  PLUGS IN: Thrown by the M3 DAOs / models, shown by the officer panels.
 *  OOP     : INHERITANCE: extends common.AppException.
 * ====================================================================
 */
package officer;

import common.AppException;

public class InvalidMedicalException extends AppException {
    public InvalidMedicalException(String message) {
        super(message);
    }
}
