
package common;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static Connection connection = null;
    private static Properties config = null;

    private DBConnection() {
    }

    public static Properties getConfig() throws DatabaseException {
        if (config == null) {
            Properties p = new Properties();
            try {
                File external = new File("db.properties");
                if (external.exists()) {
                    try (InputStream in = new FileInputStream(external)) {
                        p.load(in);
                    }
                } else {
                    try (InputStream in = DBConnection.class.getResourceAsStream("/db.properties")) {
                        if (in == null) {
                            throw new DatabaseException("db.properties not found.");
                        }
                        p.load(in);
                    }
                }
            } catch (java.io.IOException e) {
                throw new DatabaseException("Could not read db.properties.", e);
            }
            config = p;
        }
        return config;
    }

    public static String serverUrl() throws DatabaseException {
        Properties p = getConfig();
        return "jdbc:" + p.getProperty("db.scheme", "mysql") + "://" + p.getProperty("db.host", "localhost")
                + ":" + p.getProperty("db.port", "3306") + "/" + options(p);
    }

    public static String databaseUrl() throws DatabaseException {
        Properties p = getConfig();
        return "jdbc:" + p.getProperty("db.scheme", "mysql") + "://" + p.getProperty("db.host", "localhost")
                + ":" + p.getProperty("db.port", "3306") + "/" + p.getProperty("db.name", "fot_system") + options(p);
    }

    private static String options(Properties p) {
        if ("mysql".equals(p.getProperty("db.scheme", "mysql"))) {
            return "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        }
        return "";
    }

    public static Connection getServerConnection() throws DatabaseException {
        Properties p = getConfig();
        try {
            return DriverManager.getConnection(serverUrl(), p.getProperty("db.user"), p.getProperty("db.password"));
        } catch (SQLException e) {
            throw new DatabaseException("Cannot reach MySQL. Is the MySQL server running, and are the user/password in db.properties correct?", e);
        }
    }

    public static Connection getConnection() throws DatabaseException {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                Properties p = getConfig();
                connection = DriverManager.getConnection(databaseUrl(), p.getProperty("db.user"), p.getProperty("db.password"));
            }
            return connection;
        } catch (SQLException e) {
            throw new DatabaseException("Cannot connect to the database. Check that MySQL is running and db.properties is correct.", e);
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
        }
        connection = null;
    }
}
