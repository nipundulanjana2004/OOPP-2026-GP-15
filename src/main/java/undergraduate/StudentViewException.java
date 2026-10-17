/*
 * ====================================================================
 *  PART    : M4 - UNDERGRADUATE
 *  PURPOSE : Thrown when an undergraduate screen cannot find the student's own data (e.g. no registration yet).
 *  PLUGS IN: UndergraduateViewPanel.loadData() -> Dialogs.handle().
 *  OOP     : INHERITANCE: extends common.AppException.
 * ====================================================================
 */
package undergraduate;

import common.AppException;

public class StudentViewException extends AppException {
    public StudentViewException(String message) {
        super(message);
    }
}
