/*
 * ====================================================================
 *  PART    : M2 - LECTURER
 *  PURPOSE : Database access for marks: save (upsert, never silently overwritten - NFR-14) and read.
 *  PLUGS IN: Lecturer.uploadMark(); MarkEntryPanel; ResultsPanel.
 *  OOP     : INHERITANCE: extends common.BaseDAO; POLYMORPHISM: save() accepts ANY AssessmentMark.
 * ====================================================================
 */
package lecturer;

import common.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MarkDAO extends BaseDAO {

    public void save(AssessmentMark m) throws DatabaseException {
        String sql = "INSERT INTO mark (undergraduate_id,component_id,marks_obtained,uploaded_by_lecturer_id,upload_date) VALUES (?,?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE marks_obtained=VALUES(marks_obtained), uploaded_by_lecturer_id=VALUES(uploaded_by_lecturer_id), upload_date=VALUES(upload_date)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, m.getUndergraduate().getUserId());
            ps.setInt(2, m.getComponent().getComponentId());
            ps.setDouble(3, m.getMarksObtained());
            ps.setInt(4, m.getUploadedBy());
            ps.setDate(5, m.getUploadDate());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("saving the mark", e);
        }
    }

    /** studentId -> mark, for one component. */
    public Map<Integer, Double> findByComponent(int componentId) throws DatabaseException {
        Map<Integer, Double> map = new HashMap<>();
        try (PreparedStatement ps = conn().prepareStatement("SELECT undergraduate_id, marks_obtained FROM mark WHERE component_id=?")) {
            ps.setInt(1, componentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) map.put(rs.getInt(1), rs.getDouble(2));
            }
        } catch (SQLException e) {
            throw wrap("loading marks", e);
        }
        return map;
    }
}
