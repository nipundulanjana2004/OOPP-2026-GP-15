/*
 * ====================================================================
 *  PART    : COMMON (shared by M1-M4)
 *  PURPOSE : Bad user input (empty field, bad e-mail, marks outside range ...).
 *  PLUGS IN: Thrown by DAOs/forms, caught by the GUI layer.
 *  OOP     : Inheritance: extends AppException.
 * ====================================================================
 */
package common;

public class ValidationException extends AppException {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
