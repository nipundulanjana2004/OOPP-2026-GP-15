/*
 * ====================================================================
 *  PART    : M1 - ADMIN
 *  PURPOSE : Thrown when the Admin creates a user whose username already exists.
 *  PLUGS IN: UserAdminDAO.createUser -> UserManagementPanel shows the message.
 *  OOP     : INHERITANCE: extends common.AppException.
 * ====================================================================
 */
package admin;

import common.AppException;

public class DuplicateUserException extends AppException {
    public DuplicateUserException(String username) {
        super("The username '" + username + "' is already taken. Please choose another one.");
    }
}
