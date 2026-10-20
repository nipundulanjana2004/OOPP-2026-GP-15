/*
 * ====================================================================
 *  PART    : M1 - ADMIN
 *  PURPOSE : Admin-only user operations: create with duplicate check, delete, activate/deactivate.
 *  PLUGS IN: Used by the Admin class and UserManagementPanel. Login + profile reading stay in common.UserDAO.
 *  OOP     : INHERITANCE: extends common.UserDAO and adds the admin features.
 * ====================================================================
 */
package admin;

import common.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAdminDAO extends UserDAO {

    public int createUser(User u, String plainPassword) throws AppException {
        if (usernameExists(u.getUsername())) {
            throw new DuplicateUserException(u.getUsername());
        }
        return insert(u, plainPassword);
    }

    public void setActive(int userId, boolean active) throws DatabaseException {
        try (PreparedStatement ps = conn().prepareStatement("UPDATE app_user SET is_active=? WHERE user_id=?")) {
            ps.setInt(1, active ? 1 : 0);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("changing the account status", e);
        }
    }

    public void delete(int userId) throws DatabaseException {
        try (PreparedStatement ps = conn().prepareStatement("DELETE FROM app_user WHERE user_id=?")) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("deleting the user", e);
        }
    }

    public int count(String table) throws DatabaseException {
        // table names come from code (never from user input)
        try (PreparedStatement ps = conn().prepareStatement("SELECT COUNT(*) FROM " + table); ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw wrap("counting records", e);
        }
    }
}
