/*
 * ====================================================================
 *  PART    : M2 - LECTURER  (+ M4 - UNDERGRADUATE download)
 *  PURPOSE : Database access for course materials (add, modify, delete, list) and the FILE itself.
 *            The uploaded file is stored in the database (course_material.file_data) so any undergraduate
 *            on any computer can download it.
 *  PLUGS IN: Lecturer.manageMaterials(); MaterialPanel (upload); Undergraduate.viewMaterials(); MyMaterialsPanel (download).
 *  OOP     : INHERITANCE: extends common.BaseDAO; JDBC PreparedStatements; exception handling.
 * ====================================================================
 */
package lecturer;

import common.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MaterialDAO extends BaseDAO {
    public static final long MAX_FILE_BYTES = 25L * 1024 * 1024;   // 25 MB per file

    /** Reads a chosen file from the lecturer's computer; throws a friendly error when it cannot be used. */
    public static byte[] readLocalFile(String path) throws ValidationException {
        File f = new File(path);
        if (!f.isFile()) throw new ValidationException("File not found: " + path + "\nUse the Browse button to choose the file.");
        if (f.length() > MAX_FILE_BYTES) throw new ValidationException("The file is larger than 25 MB. Please upload a smaller file.");
        try {
            return Files.readAllBytes(f.toPath());
        } catch (IOException e) {
            throw new ValidationException("Could not read the file: " + e.getMessage(), e);
        }
    }

    public List<CourseMaterial> findByLecturer(int lecturerId, List<Course> courses) throws DatabaseException {
        // the lecturer sees ALL materials (also expired / not started yet) so they can extend or fix the dates
        String sql = "SELECT material_id, course_id, lecturer_id, title, file_path, file_name, upload_date, from_date, to_date, (file_data IS NOT NULL) AS has_file " +
                "FROM course_material WHERE course_id IN (SELECT course_id FROM course_lecturer WHERE lecturer_id=?) ORDER BY upload_date DESC, material_id DESC";
        return query(sql, lecturerId, courses, "loading materials");
    }

    /**
     * Materials of every course this undergraduate is registered for (file bytes are NOT loaded here).
     * ALL materials are returned, so the student can see which ones are Active, Expired or Scheduled together with
     * the From / To dates. Only an Active one (From Date <= today <= To Date) can be downloaded - see requireAvailableToday().
     */
    public List<CourseMaterial> findForStudent(int undergraduateId, List<Course> registeredCourses) throws DatabaseException {
        String sql = "SELECT material_id, course_id, lecturer_id, title, file_path, file_name, upload_date, from_date, to_date, (file_data IS NOT NULL) AS has_file " +
                "FROM course_material WHERE course_id IN (SELECT course_id FROM course_registration WHERE undergraduate_id=?) " +
                "ORDER BY upload_date DESC, material_id DESC";
        return query(sql, undergraduateId, registeredCourses, "loading the course materials");
    }

    private List<CourseMaterial> query(String sql, int id, List<Course> courses, String action, java.sql.Date... extraDates) throws DatabaseException {
        List<CourseMaterial> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            int next = 2;
            for (java.sql.Date d : extraDates) ps.setDate(next++, d);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Course c = null;
                    for (Course x : courses) if (x.getCourseId() == rs.getInt("course_id")) c = x;
                    if (c == null) continue;
                    CourseMaterial m = new CourseMaterial(rs.getInt("material_id"), rs.getString("title"), rs.getDate("upload_date"), c,
                            rs.getString("file_path"), rs.getInt("lecturer_id"));
                    m.setFileName(rs.getString("file_name"));
                    m.setHasFile(rs.getBoolean("has_file"));
                    java.sql.Date from = rs.getDate("from_date");
                    java.sql.Date to = rs.getDate("to_date");
                    m.setFromDate(from == null ? null : from.toLocalDate());
                    m.setToDate(to == null ? null : to.toLocalDate());
                    list.add(m);
                }
            }
        } catch (SQLException e) {
            throw wrap(action, e);
        }
        return list;
    }

    /** Saves the material AND the bytes of the file (read from m.getFilePath()). */
    public void insert(CourseMaterial m) throws AppException {
        checkPeriod(m);
        byte[] data = readLocalFile(m.getFilePath());
        String sql = "INSERT INTO course_material (course_id,lecturer_id,title,file_path,file_name,file_data,upload_date,from_date,to_date) VALUES (?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, m.getCourse().getCourseId());
            ps.setInt(2, m.getUploadedBy());
            ps.setString(3, m.getTitle());
            ps.setString(4, m.getFilePath());
            ps.setString(5, new File(m.getFilePath()).getName());
            ps.setBytes(6, data);
            ps.setDate(7, new java.sql.Date(System.currentTimeMillis()));
            ps.setDate(8, java.sql.Date.valueOf(m.getFromDate()));
            ps.setDate(9, java.sql.Date.valueOf(m.getToDate()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("saving the material (is the file too big for MySQL max_allowed_packet?)", e);
        }
    }

    /** Updates title/course. If the path now points to a real file on this computer the stored file is REPLACED, otherwise the stored file is kept. */
    public void update(CourseMaterial m) throws AppException {
        checkPeriod(m);
        boolean replace = new File(m.getFilePath()).isFile();
        byte[] data = replace ? readLocalFile(m.getFilePath()) : null;
        String sql = replace
                ? "UPDATE course_material SET course_id=?, title=?, from_date=?, to_date=?, file_path=?, file_name=?, file_data=? WHERE material_id=?"
                : "UPDATE course_material SET course_id=?, title=?, from_date=?, to_date=? WHERE material_id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, m.getCourse().getCourseId());
            ps.setString(2, m.getTitle());
            ps.setDate(3, java.sql.Date.valueOf(m.getFromDate()));
            ps.setDate(4, java.sql.Date.valueOf(m.getToDate()));
            if (replace) {
                ps.setString(5, m.getFilePath());
                ps.setString(6, new File(m.getFilePath()).getName());
                ps.setBytes(7, data);
                ps.setInt(8, m.getResourceId());
            } else {
                ps.setInt(5, m.getResourceId());
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("updating the material", e);
        }
    }

    /** Rule: every material must have a From Date and a To Date, and From Date cannot be after To Date. */
    private static void checkPeriod(CourseMaterial m) throws ValidationException {
        if (m.getFromDate() == null || m.getToDate() == null) {
            throw new ValidationException("Please give both a From Date and a To Date for the material.");
        }
        if (m.getFromDate().isAfter(m.getToDate())) {
            throw new ValidationException("From Date cannot be after To Date.");
        }
    }

    /** Student side guard: throws unless TODAY is inside the material's validity period (checked again at download time). */
    public void requireAvailableToday(int materialId) throws AppException {
        String sql = "SELECT 1 FROM course_material WHERE material_id=? AND from_date IS NOT NULL AND to_date IS NOT NULL AND from_date <= ? AND to_date >= ?";
        java.sql.Date today = java.sql.Date.valueOf(LocalDate.now());
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, materialId);
            ps.setDate(2, today);
            ps.setDate(3, today);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new ValidationException("This material is not available now. It is outside the valid period (From Date - To Date) set by the lecturer.");
                }
            }
        } catch (SQLException e) {
            throw wrap("checking the validity period of the material", e);
        }
    }

    public void delete(int id) throws DatabaseException {
        try (PreparedStatement ps = conn().prepareStatement("DELETE FROM course_material WHERE material_id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("deleting the material", e);
        }
    }

    /** Download: copies the stored file to the place the student chose. */
    public void saveFileTo(int materialId, File target) throws AppException {
        try (PreparedStatement ps = conn().prepareStatement("SELECT file_data FROM course_material WHERE material_id=?")) {
            ps.setInt(1, materialId);
            try (ResultSet rs = ps.executeQuery()) {
                byte[] data = rs.next() ? rs.getBytes(1) : null;
                if (data == null) throw new ValidationException("The lecturer has not attached a file to this material yet.");
                Files.write(target.toPath(), data);
            }
        } catch (SQLException e) {
            throw wrap("downloading the material", e);
        } catch (IOException e) {
            throw new ValidationException("Could not save the file: " + e.getMessage(), e);
        }
    }
}