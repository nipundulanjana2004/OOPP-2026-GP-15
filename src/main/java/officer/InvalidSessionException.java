/*
 * ====================================================================
 *  PART    : M3 - TECHNICAL OFFICER
 *  PURPOSE : A session cannot be created / used (already generated, course has no such component ...).
 *  PLUGS IN: Thrown by the M3 DAOs / models, shown by the officer panels.
 *  OOP     : INHERITANCE: extends common.AppException.
 * ====================================================================
 */
package officer;

import common.AppException;

public class InvalidSessionException extends AppException {
    public InvalidSessionException(String message) {
        super(message);
    }
}
