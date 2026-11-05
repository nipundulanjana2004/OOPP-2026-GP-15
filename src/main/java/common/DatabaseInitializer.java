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
        upgradeExistingTables(c);
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM app_user")) {
            rs.next();
            if (rs.getInt(1) == 0) {
                SampleDataSeeder.seed(c);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not check / fill the sample data.", e);
        }
    }

    /** Databases created by an older version of schema.sql get the new columns added here (CREATE TABLE IF NOT EXISTS never alters). */
    private static void upgradeExistingTables(Connection c) throws DatabaseException {
        try {
            boolean hasCourse;
            try (ResultSet rs = c.getMetaData().getColumns(c.getCatalog(), null, "medical", "course_id")) {
                hasCourse = rs.next();
            }
            if (!hasCourse) {
                try (Statement st = c.createStatement()) {
                    st.execute("ALTER TABLE medical ADD COLUMN course_id INT NULL AFTER undergraduate_id");
                    st.execute("ALTER TABLE medical ADD CONSTRAINT fk_medical_course FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE SET NULL");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not upgrade the medical table.", e);
        }
        try {
            boolean hasFile;
            try (ResultSet rs = c.getMetaData().getColumns(c.getCatalog(), null, "course_material", "file_data")) {
                hasFile = rs.next();
            }
            if (!hasFile) {   // lecture files are now stored in the database so every student can download them
                try (Statement st = c.createStatement()) {
                    st.execute("ALTER TABLE course_material ADD COLUMN file_name VARCHAR(255) NULL AFTER file_path");
                    st.execute("ALTER TABLE course_material ADD COLUMN file_data LONGBLOB NULL AFTER file_name");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not upgrade the course_material table.", e);
        }
        try {
            boolean hasDates;
            try (ResultSet rs = c.getMetaData().getColumns(c.getCatalog(), null, "course_material", "from_date")) {
                hasDates = rs.next();
            }
            if (!hasDates) {   // validity period of a material (NULL = no limit, so old materials stay visible)
                try (Statement st = c.createStatement()) {
                    st.execute("ALTER TABLE course_material ADD COLUMN from_date DATE NULL AFTER upload_date");
                    st.execute("ALTER TABLE course_material ADD COLUMN to_date DATE NULL AFTER from_date");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not add the From / To date columns to the course_material table.", e);
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