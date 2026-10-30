/*
 * ====================================================================
 *  PART    : M1 - ADMIN
 *  PURPOSE : Which lecturer teaches which course and in what role (class diagram: CourseAssignment).
 *  PLUGS IN: Created by CourseAdminDAO, shown in CourseManagementPanel; M2's lecturer sees only the courses assigned to them.
 *  OOP     : Encapsulation + composition (HAS a Course).
 * ====================================================================
 */
package admin;

import common.Course;

public class CourseAssignment {
    private int assignmentId = 0;
    private Course course = new Course();
    private int lecturerId = 0;
    private String lecturerName = "";
    private String roleInCourse = "Lecturer";

    public CourseAssignment() {
    }

    public CourseAssignment(int assignmentId, Course course, int lecturerId, String lecturerName, String roleInCourse) {
        this.assignmentId = assignmentId;
        this.course = course;
        this.lecturerId = lecturerId;
        this.lecturerName = lecturerName;
        this.roleInCourse = roleInCourse;
    }

    public int getAssignmentId() { return assignmentId; }
    public Course getCourse() { return course; }
    public int getLecturerId() { return lecturerId; }
    public String getLecturerName() { return lecturerName; }
    public String getRoleInCourse() { return roleInCourse; }
    public void setRoleInCourse(String roleInCourse) { this.roleInCourse = roleInCourse; }

    public String getDetails() {
        return lecturerName + " - " + roleInCourse + " of " + course.getCourseCode();
    }
}
