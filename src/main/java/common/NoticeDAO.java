
package common;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NoticeDAO extends BaseDAO {

    protected static final String SELECT_NOTICE =
            "SELECT n.*, d.department_code, d.department_name, b.batch_name, b.academic_year, " +
            "CONCAT(u.first_name,' ',u.last_name) AS poster FROM notice n JOIN app_user u ON u.user_id=n.posted_by " +
            "LEFT JOIN department d ON d.department_id=n.department_id LEFT JOIN batch b ON b.batch_id=n.batch_id ";

    protected Notice mapNotice(ResultSet rs) throws SQLException {
        Notice n = new Notice(rs.getInt("notice_id"), rs.getString("title"), rs.getString("content"), rs.getDate("posted_date"));
        n.setTargetType(NoticeTarget.valueOf(rs.getString("target_type")));
        String r = rs.getString("target_role");
        n.setTargetRole(r == null ? null : Role.valueOf(r));
        if (rs.getInt("department_id") > 0) {
            n.setTargetDepartment(new Department(rs.getInt("department_id"), rs.getString("department_code"), rs.getString("department_name")));
        }
        if (rs.getInt("batch_id") > 0) {
            n.setTargetBatch(new Batch(rs.getInt("batch_id"), rs.getString("batch_name"), rs.getString("academic_year")));
        }
        n.setPostedBy(rs.getInt("posted_by"));
        n.setPostedByName(rs.getString("poster"));
        return n;
    }

    public List<Notice> findAll() throws DatabaseException {
        List<Notice> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(SELECT_NOTICE + "ORDER BY n.posted_date DESC, n.notice_id DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapNotice(rs));
        } catch (SQLException e) {
            throw wrap("loading notices", e);
        }
        return list;
    }

    public List<Notice> findFor(User user) throws DatabaseException {
        List<Notice> list = new ArrayList<>();
        String sql = SELECT_NOTICE + "WHERE n.target_type='ALL' OR (n.target_type='ROLE' AND n.target_role=?) " +
                "OR (n.target_type='DEPARTMENT' AND n.department_id=?) OR (n.target_type='BATCH' AND n.batch_id=?) " +
                "ORDER BY n.posted_date DESC, n.notice_id DESC";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, user.getRole().name());
            ps.setInt(2, user.getDepartmentId());
            ps.setInt(3, user.getBatchId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapNotice(rs));
            }
        } catch (SQLException e) {
            throw wrap("loading notices", e);
        }
        return list;
    }
}
