
package common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    private DatabaseInitializer() {
    }

    public static void ensureReady() throws DatabaseException {
        String dbName = DBConnection.getConfig().getProperty("db.name", "fot_system");
        if (!dbName.matches("[A-Za-z0-9_]+")) {
            throw new DatabaseException("db.name in db.properties may only contain letters, digits and _.");
        }
        try (Connection server = DBConnection.getServerConnection(); Statement st = server.createStatement()) {
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName + " CHARACTER SET utf8mb4");
        } catch (SQLException e) {
            throw new DatabaseException("Could not create the database '" + dbName + "'.", e);
        }
        Connection c = DBConnection.getConnection();
        runSchema(c);
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM app_user")) {
            rs.next();
            if (rs.getInt(1) == 0) {
                SampleDataSeeder.seed(c);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not check / fill the sample data.", e);
        }
    }

    private static void runSchema(Connection c) throws DatabaseException {
        try (InputStream in = DatabaseInitializer.class.getResourceAsStream("/schema.sql")) {
            if (in == null) {
                throw new DatabaseException("schema.sql is missing from the project resources.");
            }
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.trim().startsWith("--")) sb.append(line).append('\n');
                }
            }
            try (Statement st = c.createStatement()) {
                for (String s : sb.toString().split(";")) {
                    if (!s.trim().isEmpty()) st.execute(s);
                }
            }
        } catch (java.io.IOException | SQLException e) {
            throw new DatabaseException("Could not create the tables.", e);
        }
    }
}
