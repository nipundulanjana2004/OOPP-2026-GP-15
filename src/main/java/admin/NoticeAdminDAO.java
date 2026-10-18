/*
 * ====================================================================
 *  PART    : M1 - ADMIN
 *  PURPOSE : Admin notice operations: post, edit, delete.
 *  PLUGS IN: Admin.manageNotices(); NoticeManagementPanel. Everyone else only reads via common.NoticeDAO.
 *  OOP     : INHERITANCE: extends common.NoticeDAO.
 * ====================================================================
 */
package admin;

import common.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NoticeAdminDAO extends NoticeDAO {

    private void bind(PreparedStatement ps, Notice n) throws SQLException {
        ps.setString(1, n.getTitle());
        ps.setString(2, n.getContent());
        ps.setString(3, n.getTargetType().name());
        ps.setString(4, n.getTargetType() == NoticeTarget.ROLE && n.getTargetRole() != null ? n.getTargetRole().name() : null);
        ps.setObject(5, n.getTargetType() == NoticeTarget.DEPARTMENT && n.getTargetDepartment() != null ? n.getTargetDepartment().getDepartmentId() : null);
        ps.setObject(6, n.getTargetType() == NoticeTarget.BATCH && n.getTargetBatch() != null ? n.getTargetBatch().getBatchId() : null);
    }

    public void insert(Notice n, int adminId) throws DatabaseException {
        String sql = "INSERT INTO notice (title,content,target_type,target_role,department_id,batch_id,posted_by,posted_date) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            bind(ps, n);
            ps.setInt(7, adminId);
            ps.setDate(8, new java.sql.Date(System.currentTimeMillis()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("posting the notice", e);
        }
    }

    public void update(Notice n) throws DatabaseException {
        String sql = "UPDATE notice SET title=?, content=?, target_type=?, target_role=?, department_id=?, batch_id=? WHERE notice_id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            bind(ps, n);
            ps.setInt(7, n.getResourceId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("updating the notice", e);
        }
    }

    public void delete(int noticeId) throws DatabaseException {
        try (PreparedStatement ps = conn().prepareStatement("DELETE FROM notice WHERE notice_id=?")) {
            ps.setInt(1, noticeId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("deleting the notice", e);
        }
    }
}
