
package common;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseDAO {

    protected Connection conn() throws DatabaseException {
        return DBConnection.getConnection();
    }

    protected DatabaseException wrap(String action, SQLException e) {
        String detail = e.getErrorCode() == 1062 ? " (that value already exists)"
                : e.getErrorCode() == 1451 ? " (it is still used by other records)" : "";
        return new DatabaseException("Database problem while " + action + detail + ".", e);
    }
}
