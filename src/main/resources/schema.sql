-- =====================================================================
-- Faculty of Technology Management System  -  MySQL 8 schema (Group 15)
-- Mirrors the Group 15 ER diagram. Safe to run many times.
-- Run automatically by common.DatabaseInitializer, or import manually:
--   mysql -u root -p < schema.sql      (after: CREATE DATABASE fot_system;)
-- =====================================================================

CREATE TABLE IF NOT EXISTS department (
    department_id INT AUTO_INCREMENT PRIMARY KEY,
    department_code VARCHAR(10) NOT NULL UNIQUE,
    department_name VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS batch (
    batch_id INT AUTO_INCREMENT PRIMARY KEY,
    batch_name VARCHAR(50) NOT NULL UNIQUE,
    academic_year VARCHAR(20) NOT NULL
) ENGINE=InnoDB;

-- Parent table of the 4 user types (table-per-subclass mapping of the abstract User class)
CREATE TABLE IF NOT EXISTS app_user (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(200) NOT NULL,
    first_name VARCHAR(60) NOT NULL,
    last_name VARCHAR(60) NOT NULL,
    email VARCHAR(100),
    contact_number VARCHAR(20),
    address VARCHAR(200),
    profile_picture VARCHAR(255),
    role VARCHAR(20) NOT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_date DATE NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS admin (
    user_id INT PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES app_user(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS lecturer (
    user_id INT PRIMARY KEY,
    designation VARCHAR(80),
    department_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES app_user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES department(department_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS technical_officer (
    user_id INT PRIMARY KEY,
    department_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES app_user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES department(department_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS undergraduate (
    user_id INT PRIMARY KEY,
    registration_number VARCHAR(30) NOT NULL UNIQUE,
    batch_id INT NOT NULL,
    department_id INT NOT NULL,
    academic_status VARCHAR(20) NOT NULL DEFAULT 'PROPER',
    FOREIGN KEY (user_id) REFERENCES app_user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (batch_id) REFERENCES batch(batch_id),
    FOREIGN KEY (department_id) REFERENCES department(department_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS course (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(15) NOT NULL UNIQUE,
    course_name VARCHAR(120) NOT NULL,
    theory_credits INT NOT NULL DEFAULT 0,
    practical_credits INT NOT NULL DEFAULT 0,
    semester INT NOT NULL,
    department_id INT NOT NULL,
    FOREIGN KEY (department_id) REFERENCES department(department_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS course_lecturer (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    lecturer_id INT NOT NULL,
    role_in_course VARCHAR(40) NOT NULL DEFAULT 'Lecturer',
    UNIQUE (course_id, lecturer_id),
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE,
    FOREIGN KEY (lecturer_id) REFERENCES lecturer(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS course_registration (
    registration_id INT AUTO_INCREMENT PRIMARY KEY,
    undergraduate_id INT NOT NULL,
    course_id INT NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    attempt_type VARCHAR(20) NOT NULL DEFAULT 'PROPER',
    UNIQUE (undergraduate_id, course_id),
    FOREIGN KEY (undergraduate_id) REFERENCES undergraduate(user_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS course_material (
    material_id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    lecturer_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    upload_date DATE NOT NULL,
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE,
    FOREIGN KEY (lecturer_id) REFERENCES lecturer(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS notice (
    notice_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    content TEXT NOT NULL,
    target_type VARCHAR(20) NOT NULL DEFAULT 'ALL',
    target_role VARCHAR(20),
    department_id INT,
    batch_id INT,
    posted_by INT NOT NULL,
    posted_date DATE NOT NULL,
    FOREIGN KEY (posted_by) REFERENCES admin(user_id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES department(department_id) ON DELETE SET NULL,
    FOREIGN KEY (batch_id) REFERENCES batch(batch_id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS timetable (
    timetable_id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    session_type VARCHAR(20) NOT NULL,
    day_of_week VARCHAR(12) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    venue VARCHAR(60) NOT NULL,
    batch_id INT NOT NULL,
    department_id INT NOT NULL,
    admin_id INT NOT NULL,
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE,
    FOREIGN KEY (batch_id) REFERENCES batch(batch_id),
    FOREIGN KEY (department_id) REFERENCES department(department_id),
    FOREIGN KEY (admin_id) REFERENCES admin(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS class_session (
    session_id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    session_type VARCHAR(20) NOT NULL,
    session_number INT NOT NULL,
    session_date DATE NOT NULL,
    duration_hours DOUBLE NOT NULL,
    UNIQUE (course_id, session_type, session_number),
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS medical (
    medical_id INT AUTO_INCREMENT PRIMARY KEY,
    undergraduate_id INT NOT NULL,
    date_from DATE NOT NULL,
    date_to DATE NOT NULL,
    reason VARCHAR(200) NOT NULL,
    document_path VARCHAR(255),
    approval_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    verified_by_officer_id INT,
    submitted_date DATE NOT NULL,
    FOREIGN KEY (undergraduate_id) REFERENCES undergraduate(user_id) ON DELETE CASCADE,
    FOREIGN KEY (verified_by_officer_id) REFERENCES technical_officer(user_id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    undergraduate_id INT NOT NULL,
    session_id INT NOT NULL,
    status VARCHAR(10) NOT NULL,
    recorded_by_officer_id INT,
    medical_id INT,
    UNIQUE (undergraduate_id, session_id),
    FOREIGN KEY (undergraduate_id) REFERENCES undergraduate(user_id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES class_session(session_id) ON DELETE CASCADE,
    FOREIGN KEY (recorded_by_officer_id) REFERENCES technical_officer(user_id) ON DELETE SET NULL,
    FOREIGN KEY (medical_id) REFERENCES medical(medical_id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS evaluation_component (
    component_id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    component_name VARCHAR(80) NOT NULL,
    component_type VARCHAR(10) NOT NULL,
    max_marks DOUBLE NOT NULL DEFAULT 100,
    weightage DOUBLE NOT NULL,
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS mark (
    mark_id INT AUTO_INCREMENT PRIMARY KEY,
    undergraduate_id INT NOT NULL,
    component_id INT NOT NULL,
    marks_obtained DOUBLE NOT NULL,
    uploaded_by_lecturer_id INT,
    upload_date DATE NOT NULL,
    UNIQUE (undergraduate_id, component_id),
    FOREIGN KEY (undergraduate_id) REFERENCES undergraduate(user_id) ON DELETE CASCADE,
    FOREIGN KEY (component_id) REFERENCES evaluation_component(component_id) ON DELETE CASCADE,
    FOREIGN KEY (uploaded_by_lecturer_id) REFERENCES lecturer(user_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Single place that defines the grading scheme (NFR-16). Edit rows here to change grading.
CREATE TABLE IF NOT EXISTS grade_scale (
    grade_id INT AUTO_INCREMENT PRIMARY KEY,
    grade VARCHAR(5) NOT NULL UNIQUE,
    grade_point DOUBLE NOT NULL,
    min_marks DOUBLE NOT NULL,
    max_marks DOUBLE NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS course_result (
    result_id INT AUTO_INCREMENT PRIMARY KEY,
    undergraduate_id INT NOT NULL,
    course_id INT NOT NULL,
    ca_marks DOUBLE,
    final_exam_marks DOUBLE,
    total_marks DOUBLE,
    attendance_theory_percent DOUBLE,
    attendance_practical_percent DOUBLE,
    attendance_overall_percent DOUBLE,
    ca_eligible TINYINT(1),
    attendance_eligible TINYINT(1),
    overall_eligible TINYINT(1),
    grade_id INT,
    UNIQUE (undergraduate_id, course_id),
    FOREIGN KEY (undergraduate_id) REFERENCES undergraduate(user_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE,
    FOREIGN KEY (grade_id) REFERENCES grade_scale(grade_id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS semester_result (
    semester_result_id INT AUTO_INCREMENT PRIMARY KEY,
    undergraduate_id INT NOT NULL,
    semester INT NOT NULL,
    sgpa DOUBLE,
    cgpa DOUBLE,
    UNIQUE (undergraduate_id, semester),
    FOREIGN KEY (undergraduate_id) REFERENCES undergraduate(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;
