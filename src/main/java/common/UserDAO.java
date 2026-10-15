/*
 * ====================================================================
 *  PART    : COMMON (shared by M1-M4) (M1 extends it for admin work; every role logs in through it)
 *  PURPOSE : Login + load/save users. Handles the shared app_user table and delegates the role table to each User subclass.
 *  PLUGS IN: LoginFrame.authenticate(); M1 UserAdminDAO extends this class; M2/M3/M4 profile saves use update().
 *  OOP     : Inheritance, POLYMORPHISM (user.getRoleTable()/getRoleColumns()), JDBC PreparedStatement, transactions.
 * ====================================================================
 */
package common;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserDAO extends BaseDAO {

    protected static final String SELECT_USER =
            "SELECT u.*, l.designation, g.registration_number, g.batch_id, g.academic_status, b.batch_name, b.academic_year, " +
            "COALESCE(l.department_id, t.department_id, g.department_id) AS dept_id, d.department_code AS dept_code, d.department_name AS dept_name " +
            "FROM app_user u LEFT JOIN lecturer l ON l.user_id=u.user_id LEFT JOIN technical_officer t ON t.user_id=u.user_id " +
            "LEFT JOIN undergraduate g ON g.user_id=u.user_id LEFT JOIN batch b ON b.batch_id=g.batch_id " +
            "LEFT JOIN department d ON d.department_id=COALESCE(l.department_id, t.department_id, g.department_id) ";

    /** Turns one row into the right subclass (Admin / Lecturer / TechnicalOfficer / Undergraduate). */
    protected User mapUser(ResultSet rs) throws SQLException, AppException {
        Role role = Role.valueOf(rs.getString("role"));
        User u = UserFactory.create(role);
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setFirstName(rs.getString("first_name"));
        u.setLastName(rs.getString("last_name"));
        u.setEmail(nz(rs.getString("email")));
        u.setContactNumber(nz(rs.getString("contact_number")));
        u.setAddress(nz(rs.getString("address")));
        u.setProfilePicture(nz(rs.getString("profile_picture")));
        u.setActive(rs.getInt("is_active") == 1);
        u.loadRoleDetails(rs);      // each subclass reads its own columns
        return u;
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }

    public User authenticate(String username, String password) throws AppException {
        User u = findByUsername(username);
        if (u == null || !PasswordUtil.matches(password, u.getPasswordHash())) {
            throw new InvalidLoginException("Invalid username or password.");
        }
        if (!u.isActive()) {
            throw new InvalidLoginException("This account has been deactivated. Please contact the Admin.");
        }
        return u;
    }

    public User findByUsername(String username) throws AppException {
        try (PreparedStatement ps = conn().prepareStatement(SELECT_USER + "WHERE u.username=?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapUser(rs) : null;
            }
        } catch (SQLException e) {
            throw wrap("looking up the user", e);
        }
    }

    public User findById(int id) throws AppException {
        try (PreparedStatement ps = conn().prepareStatement(SELECT_USER + "WHERE u.user_id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapUser(rs) : null;
            }
        } catch (SQLException e) {
            throw wrap("looking up the user", e);
        }
    }

    public List<User> findAll() throws AppException {
        return query(SELECT_USER + "ORDER BY u.role, u.username", null);
    }

    public List<User> findByRole(Role role) throws AppException {
        return query(SELECT_USER + "WHERE u.role=? ORDER BY u.username", role.name());
    }

    private List<User> query(String sql, String param) throws AppException {
        List<User> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            if (param != null) ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        list.add(mapUser(rs));
                    } catch (ModuleMissingException skip) {
                        // that member's zip is not installed in this build: leave the user out of the list
                    }
                }
            }
        } catch (SQLException e) {
            throw wrap("loading users", e);
        }
        return list;
    }

    public boolean usernameExists(String username) throws DatabaseException {
        try (PreparedStatement ps = conn().prepareStatement("SELECT 1 FROM app_user WHERE username=?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw wrap("checking the username", e);
        }
    }

    /** Inserts into app_user AND the role table in ONE transaction. Returns the new user id. */
    public int insert(User user, String plainPassword) throws DatabaseException {
        String sql = "INSERT INTO app_user (username,password_hash,first_name,last_name,email,contact_number,address," +
                "profile_picture,role,is_active,created_date) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try {
            conn().setAutoCommit(false);
            int id;
            try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, user.getUsername());
                ps.setString(2, PasswordUtil.hash(plainPassword));
                ps.setString(3, user.getFirstName());
                ps.setString(4, user.getLastName());
                ps.setString(5, user.getEmail());
                ps.setString(6, user.getContactNumber());
                ps.setString(7, user.getAddress());
                ps.setString(8, user.getProfilePicture());
                ps.setString(9, user.getRole().name());
                ps.setInt(10, user.isActive() ? 1 : 0);
                ps.setDate(11, new Date(System.currentTimeMillis()));
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    id = keys.getInt(1);
                }
            }
            user.setUserId(id);
            saveRoleRow(user);
            conn().commit();
            return id;
        } catch (SQLException e) {
            rollbackQuietly();
            throw wrap("creating the user", e);
        } finally {
            resetAutoCommit();
        }
    }

    /** Updates the profile fields of app_user and the role table (not the password). */
    public void update(User user) throws DatabaseException {
        String sql = "UPDATE app_user SET username=?, first_name=?, last_name=?, email=?, contact_number=?, address=?, " +
                "profile_picture=?, is_active=? WHERE user_id=?";
        try {
            conn().setAutoCommit(false);
            try (PreparedStatement ps = conn().prepareStatement(sql)) {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getFirstName());
                ps.setString(3, user.getLastName());
                ps.setString(4, user.getEmail());
                ps.setString(5, user.getContactNumber());
                ps.setString(6, user.getAddress());
                ps.setString(7, user.getProfilePicture());
                ps.setInt(8, user.isActive() ? 1 : 0);
                ps.setInt(9, user.getUserId());
                ps.executeUpdate();
            }
            saveRoleRow(user);
            conn().commit();
        } catch (SQLException e) {
            rollbackQuietly();
            throw wrap("saving the profile", e);
        } finally {
            resetAutoCommit();
        }
    }

    public void updatePassword(int userId, String plainPassword) throws DatabaseException {
        try (PreparedStatement ps = conn().prepareStatement("UPDATE app_user SET password_hash=? WHERE user_id=?")) {
            ps.setString(1, PasswordUtil.hash(plainPassword));
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("changing the password", e);
        }
    }

    /** Builds "INSERT ... ON DUPLICATE KEY UPDATE" from whatever columns the subclass reports. */
    private void saveRoleRow(User user) throws SQLException, DatabaseException {
        Map<String, Object> cols = user.getRoleColumns();
        StringBuilder names = new StringBuilder("user_id");
        StringBuilder marks = new StringBuilder("?");
        StringBuilder upd = new StringBuilder();
        for (String c : cols.keySet()) {
            names.append(",").append(c);
            marks.append(",?");
            if (upd.length() > 0) upd.append(",");
            upd.append(c).append("=VALUES(").append(c).append(")");
        }
        if (upd.length() == 0) upd.append("user_id=user_id");
        String sql = "INSERT INTO " + user.getRoleTable() + " (" + names + ") VALUES (" + marks + ") ON DUPLICATE KEY UPDATE " + upd;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, user.getUserId());
            int i = 2;
            for (Object v : cols.values()) {
                ps.setObject(i++, v);
            }
            ps.executeUpdate();
        }
    }

    protected void rollbackQuietly() {
        try {
            conn().rollback();
        } catch (Exception ignored) {
            // nothing more we can do
        }
    }

    protected void resetAutoCommit() {
        try {
            conn().setAutoCommit(true);
        } catch (Exception ignored) {
            // nothing more we can do
        }
    }
}
