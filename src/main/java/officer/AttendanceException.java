/*
 * ====================================================================
 *  PART    : M3 - TECHNICAL OFFICER
 *  PURPOSE : Problem while recording attendance (no students, nothing to save ...).
 *  PLUGS IN: Thrown by the M3 DAOs / models, shown by the officer panels.
 *  OOP     : INHERITANCE: extends common.AppException.
 * ====================================================================
 */
package officer;

import common.AppException;

public class AttendanceException extends AppException {
    public AttendanceException(String message) {
        super(message);
    }
}
