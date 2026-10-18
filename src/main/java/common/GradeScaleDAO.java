
package common;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GradeScaleDAO extends BaseDAO {
    public List<GradeScale> findAll() throws DatabaseException {
        List<GradeScale> list = new ArrayList<>();
        String sql = "SELECT * FROM grade_scale ORDER BY min_marks DESC";
        try (PreparedStatement ps = conn().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new GradeScale(rs.getInt("grade_id"), rs.getString("grade"), rs.getDouble("grade_point"),
                        rs.getDouble("min_marks"), rs.getDouble("max_marks")));
            }
        } catch (SQLException e) {
            throw wrap("reading the grade scale", e);
        }
        return list;
    }
}
