/*
 * ====================================================================
 *  PART    : M2 - LECTURER
 *  PURPOSE : Database access for course materials (add, modify, delete, list).
 *  PLUGS IN: Lecturer.manageMaterials(); MaterialPanel.
 *  OOP     : INHERITANCE: extends common.BaseDAO; JDBC PreparedStatements.
 * ====================================================================
 */
package lecturer;

import common.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MaterialDAO extends BaseDAO {

    public List<CourseMaterial> findByLecturer(int lecturerId, List<Course> courses) throws DatabaseException {
        List<CourseMaterial> list = new ArrayList<>();
        String sql = "SELECT * FROM course_material WHERE course_id IN (SELECT course_id FROM course_lecturer WHERE lecturer_id=?) ORDER BY upload_date DESC, material_id DESC";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Course c = null;
                    for (Course x : courses) if (x.getCourseId() == rs.getInt("course_id")) c = x;
                    if (c == null) continue;
                    list.add(new CourseMaterial(rs.getInt("material_id"), rs.getString("title"), rs.getDate("upload_date"), c,
                            rs.getString("file_path"), rs.getInt("lecturer_id")));
                }
            }
        } catch (SQLException e) {
            throw wrap("loading materials", e);
        }
        return list;
    }

    public void insert(CourseMaterial m) throws DatabaseException {
        String sql = "INSERT INTO course_material (course_id,lecturer_id,title,file_path,upload_date) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, m.getCourse().getCourseId());
            ps.setInt(2, m.getUploadedBy());
            ps.setString(3, m.getTitle());
            ps.setString(4, m.getFilePath());
            ps.setDate(5, new java.sql.Date(System.currentTimeMillis()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("saving the material", e);
        }
    }

    public void update(CourseMaterial m) throws DatabaseException {
        try (PreparedStatement ps = conn().prepareStatement("UPDATE course_material SET course_id=?, title=?, file_path=? WHERE material_id=?")) {
            ps.setInt(1, m.getCourse().getCourseId());
            ps.setString(2, m.getTitle());
            ps.setString(3, m.getFilePath());
            ps.setInt(4, m.getResourceId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("updating the material", e);
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
}
