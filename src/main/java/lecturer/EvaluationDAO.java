/*
 * ====================================================================
 *  PART    : M2 - LECTURER
 *  PURPOSE : Database access for evaluation components (the marking scheme of a course).
 *  PLUGS IN: MarkEntryPanel, EvaluationSetupPanel, Lecturer.
 *  OOP     : INHERITANCE: extends common.BaseDAO.
 * ====================================================================
 */
package lecturer;

import common.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EvaluationDAO extends BaseDAO {

    public List<EvaluationComponent> findByCourse(int courseId) throws DatabaseException {
        List<EvaluationComponent> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement("SELECT * FROM evaluation_component WHERE course_id=? ORDER BY component_type, component_id")) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new EvaluationComponent(rs.getInt("component_id"), courseId, rs.getString("component_name"),
                            ComponentType.valueOf(rs.getString("component_type")), rs.getDouble("weightage")));
                }
            }
        } catch (SQLException e) {
            throw wrap("loading evaluation components", e);
        }
        return list;
    }

    public void insert(int courseId, String name, ComponentType type, double weightage) throws DatabaseException {
        try (PreparedStatement ps = conn().prepareStatement("INSERT INTO evaluation_component (course_id,component_name,component_type,max_marks,weightage) VALUES (?,?,?,100,?)")) {
            ps.setInt(1, courseId);
            ps.setString(2, name);
            ps.setString(3, type.name());
            ps.setDouble(4, weightage);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("saving the component", e);
        }
    }

    public void delete(int componentId) throws DatabaseException {
        try (PreparedStatement ps = conn().prepareStatement("DELETE FROM evaluation_component WHERE component_id=?")) {
            ps.setInt(1, componentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("deleting the component (its marks are deleted too)", e);
        }
    }
}
