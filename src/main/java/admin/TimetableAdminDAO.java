/*
 * ====================================================================
 *  PART    : M1 - ADMIN
 *  PURPOSE : Admin timetable operations: create, update, delete, with a venue-clash check.
 *  PLUGS IN: Admin.manageTimetables(); TimetableManagementPanel. Others read through common.TimetableDAO.
 *  OOP     : INHERITANCE: extends common.TimetableDAO.
 * ====================================================================
 */
package admin;

import common.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TimetableAdminDAO extends TimetableDAO {

    /** Two slots in the same venue, same day, overlapping times = clash. */
    private void checkClash(Timetable t) throws AppException {
        if (!t.getEndTime().after(t.getStartTime())) {
            throw new ValidationException("The end time must be after the start time.");
        }
        String sql = "SELECT 1 FROM timetable WHERE venue=? AND day_of_week=? AND start_time<? AND end_time>? AND timetable_id<>?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, t.getVenue());
            ps.setString(2, t.getDayOfWeek());
            ps.setTime(3, t.getEndTime());
            ps.setTime(4, t.getStartTime());
            ps.setInt(5, t.getTimetableId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    throw new ValidationException("Venue " + t.getVenue() + " is already booked on " + t.getDayOfWeek() + " at that time.");
                }
            }
        } catch (SQLException e) {
            throw wrap("checking the venue", e);
        }
    }

    public void insert(Timetable t, int adminId) throws AppException {
        checkClash(t);
        String sql = "INSERT INTO timetable (course_id,session_type,day_of_week,start_time,end_time,venue,batch_id,department_id,admin_id) VALUES (?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, t.getCourse().getCourseId());
            ps.setString(2, t.getSessionType().name());
            ps.setString(3, t.getDayOfWeek());
            ps.setTime(4, t.getStartTime());
            ps.setTime(5, t.getEndTime());
            ps.setString(6, t.getVenue());
            ps.setInt(7, t.getBatch().getBatchId());
            ps.setInt(8, t.getCourse().getDepartment().getDepartmentId());
            ps.setInt(9, adminId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("saving the timetable slot", e);
        }
    }

    public void update(Timetable t) throws AppException {
        checkClash(t);
        String sql = "UPDATE timetable SET course_id=?, session_type=?, day_of_week=?, start_time=?, end_time=?, venue=?, batch_id=?, department_id=? WHERE timetable_id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, t.getCourse().getCourseId());
            ps.setString(2, t.getSessionType().name());
            ps.setString(3, t.getDayOfWeek());
            ps.setTime(4, t.getStartTime());
            ps.setTime(5, t.getEndTime());
            ps.setString(6, t.getVenue());
            ps.setInt(7, t.getBatch().getBatchId());
            ps.setInt(8, t.getCourse().getDepartment().getDepartmentId());
            ps.setInt(9, t.getTimetableId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("updating the timetable slot", e);
        }
    }

    public void delete(int id) throws DatabaseException {
        try (PreparedStatement ps = conn().prepareStatement("DELETE FROM timetable WHERE timetable_id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw wrap("deleting the timetable slot", e);
        }
    }
}
