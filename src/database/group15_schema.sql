
DROP DATABASE IF EXISTS faculty_system;
CREATE DATABASE faculty_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE faculty_system;

CREATE TABLE Department (
    DepartmentID    INT AUTO_INCREMENT PRIMARY KEY,
    DepartmentName  VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE Batch (
    BatchID         INT AUTO_INCREMENT PRIMARY KEY,
    BatchName       VARCHAR(50) NOT NULL,
    AcademicYear    VARCHAR(20) NOT NULL,
    DepartmentID    INT NOT NULL,
    CONSTRAINT fk_batch_department FOREIGN KEY (DepartmentID)
        REFERENCES Department(DepartmentID) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE `User` (
    UserID            INT AUTO_INCREMENT PRIMARY KEY,
    Username          VARCHAR(50)  NOT NULL UNIQUE,
    PasswordHash      VARCHAR(255) NOT NULL,
    FirstName         VARCHAR(50)  NOT NULL,
    LastName          VARCHAR(50)  NOT NULL,
    Email             VARCHAR(100) NOT NULL UNIQUE,
    ContactNumber     VARCHAR(20),
    Address           VARCHAR(255),
    ProfilePicture    VARCHAR(255),
    Role              ENUM('Admin','Lecturer','TechnicalOfficer','Undergraduate') NOT NULL,
    CreatedDate       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CreatedByAdminID  INT NULL
) ENGINE=InnoDB;

CREATE TABLE Admin (
    UserID INT PRIMARY KEY,
    CONSTRAINT fk_admin_user FOREIGN KEY (UserID)
        REFERENCES `User`(UserID) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

ALTER TABLE `User`
    ADD CONSTRAINT fk_user_createdby FOREIGN KEY (CreatedByAdminID)
        REFERENCES Admin(UserID) ON UPDATE CASCADE ON DELETE SET NULL;

CREATE TABLE Lecturer (
    UserID        INT PRIMARY KEY,
    Designation   VARCHAR(100),
    DepartmentID  INT NOT NULL,
    CONSTRAINT fk_lecturer_user FOREIGN KEY (UserID)
        REFERENCES `User`(UserID) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_lecturer_department FOREIGN KEY (DepartmentID)
        REFERENCES Department(DepartmentID) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE Technical_Officer (
    UserID        INT PRIMARY KEY,
    DepartmentID  INT NOT NULL,
    CONSTRAINT fk_to_user FOREIGN KEY (UserID)
        REFERENCES `User`(UserID) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_to_department FOREIGN KEY (DepartmentID)
        REFERENCES Department(DepartmentID) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE Undergraduate (
    UserID              INT PRIMARY KEY,
    RegistrationNumber  VARCHAR(30) NOT NULL UNIQUE,
    BatchID             INT NOT NULL,
    DepartmentID        INT NOT NULL,
    AcademicStatus      ENUM('Active','Suspended','Graduated','Withdrawn') NOT NULL DEFAULT 'Active',
    CONSTRAINT fk_ug_user FOREIGN KEY (UserID)
        REFERENCES `User`(UserID) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_ug_batch FOREIGN KEY (BatchID)
        REFERENCES Batch(BatchID) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_ug_department FOREIGN KEY (DepartmentID)
        REFERENCES Department(DepartmentID) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE Course (
    CourseID          INT AUTO_INCREMENT PRIMARY KEY,
    CourseCode        VARCHAR(20)  NOT NULL UNIQUE,
    CourseName        VARCHAR(150) NOT NULL,
    TheoryCredits     DECIMAL(3,1) NOT NULL DEFAULT 0,
    PracticalCredits  DECIMAL(3,1) NOT NULL DEFAULT 0,
    Semester          TINYINT NOT NULL,
    DepartmentID      INT NOT NULL,
    AdminID           INT NULL,
    Status            ENUM('Active','Inactive') NOT NULL DEFAULT 'Active',
    CONSTRAINT fk_course_department FOREIGN KEY (DepartmentID)
        REFERENCES Department(DepartmentID) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_course_admin FOREIGN KEY (AdminID)
        REFERENCES Admin(UserID) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE Course_Lecturer (
    CourseID      INT NOT NULL,
    LecturerID    INT NOT NULL,
    RoleInCourse  VARCHAR(50),
    PRIMARY KEY (CourseID, LecturerID),
    CONSTRAINT fk_cl_course FOREIGN KEY (CourseID)
        REFERENCES Course(CourseID) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_cl_lecturer FOREIGN KEY (LecturerID)
        REFERENCES Lecturer(UserID) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE Course_Registration (
    RegistrationID  INT AUTO_INCREMENT PRIMARY KEY,
    UndergraduateID INT NOT NULL,
    CourseID        INT NOT NULL,
    AcademicYear    VARCHAR(20) NOT NULL,
    AttemptType     ENUM('First','Repeat','Proper','Suspended') NOT NULL DEFAULT 'First',
    UNIQUE KEY uq_registration (UndergraduateID, CourseID, AcademicYear, AttemptType),
    CONSTRAINT fk_cr_ug FOREIGN KEY (UndergraduateID)
        REFERENCES Undergraduate(UserID) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_cr_course FOREIGN KEY (CourseID)
        REFERENCES Course(CourseID) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE Course_Material (
    MaterialID   INT AUTO_INCREMENT PRIMARY KEY,
    CourseID     INT NOT NULL,
    LecturerID   INT NOT NULL,
    Title        VARCHAR(150) NOT NULL,
    FilePath     VARCHAR(255) NOT NULL,
    UploadDate   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cm_course FOREIGN KEY (CourseID)
        REFERENCES Course(CourseID) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_cm_lecturer FOREIGN KEY (LecturerID)
        REFERENCES Lecturer(UserID) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE `Session` (
    SessionID      INT AUTO_INCREMENT PRIMARY KEY,
    CourseID       INT NOT NULL,
    SessionType    ENUM('Theory','Practical') NOT NULL,
    SessionNumber  INT NOT NULL,
    SessionDate    DATE NOT NULL,
    DurationHours  DECIMAL(3,1) NOT NULL,
    UNIQUE KEY uq_session (CourseID, SessionType, SessionNumber),
    CONSTRAINT fk_session_course FOREIGN KEY (CourseID)
        REFERENCES Course(CourseID) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE Medical (
    MedicalID            INT AUTO_INCREMENT PRIMARY KEY,
    UndergraduateID      INT NOT NULL,
    DateFrom             DATE NOT NULL,
    DateTo               DATE NOT NULL,
    Reason               VARCHAR(255),
    DocumentPath         VARCHAR(255),
    ApprovalStatus       ENUM('Pending','Approved','Rejected') NOT NULL DEFAULT 'Pending',
    VerifiedByOfficerID  INT NULL,
    CONSTRAINT chk_medical_dates CHECK (DateTo >= DateFrom),
    CONSTRAINT fk_med_ug FOREIGN KEY (UndergraduateID)
        REFERENCES Undergraduate(UserID) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_med_officer FOREIGN KEY (VerifiedByOfficerID)
        REFERENCES Technical_Officer(UserID) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE Attendance (
    AttendanceID     INT AUTO_INCREMENT PRIMARY KEY,
    SessionID        INT NOT NULL,
    UndergraduateID  INT NOT NULL,
    Status           ENUM('Present','Absent','Medical') NOT NULL,
    OfficerID        INT NULL,
    MedicalID        INT NULL,
    UNIQUE KEY uq_attendance (SessionID, UndergraduateID),
    CONSTRAINT fk_att_session FOREIGN KEY (SessionID)
        REFERENCES `Session`(SessionID) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_att_ug FOREIGN KEY (UndergraduateID)
        REFERENCES Undergraduate(UserID) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_att_officer FOREIGN KEY (OfficerID)
        REFERENCES Technical_Officer(UserID) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_att_medical FOREIGN KEY (MedicalID)
        REFERENCES Medical(MedicalID) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE Evaluation_Component (
    ComponentID    INT AUTO_INCREMENT PRIMARY KEY,
    CourseID       INT NOT NULL,
    ComponentName  VARCHAR(100) NOT NULL,
    ComponentType  ENUM('CA','Final') NOT NULL,
    MaxMarks       DECIMAL(5,2) NOT NULL,
    Weightage      DECIMAL(5,2) NOT NULL,
    CONSTRAINT fk_ec_course FOREIGN KEY (CourseID)
        REFERENCES Course(CourseID) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE Mark (
    MarkID           INT AUTO_INCREMENT PRIMARY KEY,
    ComponentID      INT NOT NULL,
    UndergraduateID  INT NOT NULL,
    LecturerID       INT NOT NULL,
    MarksObtained    DECIMAL(5,2) NOT NULL,
    UploadDate       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_mark (ComponentID, UndergraduateID),
    CONSTRAINT fk_mark_component FOREIGN KEY (ComponentID)
        REFERENCES Evaluation_Component(ComponentID) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_mark_ug FOREIGN KEY (UndergraduateID)
        REFERENCES Undergraduate(UserID) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_mark_lecturer FOREIGN KEY (LecturerID)
        REFERENCES Lecturer(UserID) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE Grade_Scale (
    GradeID     INT AUTO_INCREMENT PRIMARY KEY,
    MinMarks    DECIMAL(5,2) NOT NULL,
    MaxMarks    DECIMAL(5,2) NOT NULL,
    Grade       VARCHAR(3) NOT NULL UNIQUE,
    GradePoint  DECIMAL(3,2) NOT NULL,
    CONSTRAINT chk_grade_range CHECK (MaxMarks >= MinMarks)
) ENGINE=InnoDB;

CREATE TABLE Course_Result (
    ResultID                    INT AUTO_INCREMENT PRIMARY KEY,
    UndergraduateID             INT NOT NULL,
    CourseID                    INT NOT NULL,
    AcademicYear                VARCHAR(20) NOT NULL,
    CAMarks                     DECIMAL(5,2),
    FinalExamMarks              DECIMAL(5,2),
    TotalMarks                  DECIMAL(5,2),
    AttendanceTheoryPercent     DECIMAL(5,2),
    AttendancePracticalPercent  DECIMAL(5,2),
    AttendanceOverallPercent    DECIMAL(5,2),
    AttendanceEligible          BOOLEAN NOT NULL DEFAULT FALSE,
    CAEligible                  BOOLEAN NOT NULL DEFAULT FALSE,
    OverallEligible             BOOLEAN NOT NULL DEFAULT FALSE,
    GradeID                     INT NULL,
    UNIQUE KEY uq_course_result (UndergraduateID, CourseID, AcademicYear),
    CONSTRAINT fk_result_ug FOREIGN KEY (UndergraduateID)
        REFERENCES Undergraduate(UserID) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_result_course FOREIGN KEY (CourseID)
        REFERENCES Course(CourseID) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_result_grade FOREIGN KEY (GradeID)
        REFERENCES Grade_Scale(GradeID) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE Semester_Result (
    SemesterResultID  INT AUTO_INCREMENT PRIMARY KEY,
    UndergraduateID   INT NOT NULL,
    Semester          TINYINT NOT NULL,
    SGPA              DECIMAL(4,2),
    CGPA              DECIMAL(4,2),
    UNIQUE KEY uq_sem_result (UndergraduateID, Semester),
    CONSTRAINT fk_sr_ug FOREIGN KEY (UndergraduateID)
        REFERENCES Undergraduate(UserID) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE Notice (
    NoticeID      INT AUTO_INCREMENT PRIMARY KEY,
    Title         VARCHAR(150) NOT NULL,
    Content       TEXT NOT NULL,
    PostedDate    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    TargetRole    ENUM('All','Lecturer','TechnicalOfficer','Undergraduate') NOT NULL DEFAULT 'All',
    DepartmentID  INT NULL,
    BatchID       INT NULL,
    AdminID       INT NOT NULL,
    CONSTRAINT fk_notice_department FOREIGN KEY (DepartmentID)
        REFERENCES Department(DepartmentID) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_notice_batch FOREIGN KEY (BatchID)
        REFERENCES Batch(BatchID) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_notice_admin FOREIGN KEY (AdminID)
        REFERENCES Admin(UserID) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE Timetable (
    TimetableID  INT AUTO_INCREMENT PRIMARY KEY,
    CourseID     INT NOT NULL,
    BatchID      INT NOT NULL,
    SessionType  ENUM('Theory','Practical') NOT NULL,
    DayOfWeek    ENUM('Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday') NOT NULL,
    StartTime    TIME NOT NULL,
    EndTime      TIME NOT NULL,
    Venue        VARCHAR(100),
    AdminID      INT NULL,
    CONSTRAINT chk_tt_time CHECK (EndTime > StartTime),
    CONSTRAINT fk_tt_course FOREIGN KEY (CourseID)
        REFERENCES Course(CourseID) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_tt_batch FOREIGN KEY (BatchID)
        REFERENCES Batch(BatchID) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_tt_admin FOREIGN KEY (AdminID)
        REFERENCES Admin(UserID) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;
