-- Greenwood Eco School LMS baseline schema.
-- IF NOT EXISTS keeps this migration safe for databases where tables already exist.
CREATE TABLE IF NOT EXISTS students (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_number VARCHAR(20) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    gender VARCHAR(10) NULL,
    email VARCHAR(100) NULL,
    phone_number VARCHAR(15) NULL,
    program VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_students_student_number (student_number),
    KEY idx_students_program (program)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS courses (
    id BIGINT NOT NULL AUTO_INCREMENT,
    course_code VARCHAR(20) NOT NULL,
    course_name VARCHAR(100) NOT NULL,
    credit_units INT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_courses_course_code (course_code),
    CONSTRAINT chk_courses_credit_units CHECK (credit_units > 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS lecturers (
    id BIGINT NOT NULL AUTO_INCREMENT,
    lecturer_number VARCHAR(20) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NULL,
    phone_number VARCHAR(30) NULL,
    department VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_lecturers_lecturer_number (lecturer_number),
    KEY idx_lecturers_department (department)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS academic_periods (
    id BIGINT NOT NULL AUTO_INCREMENT,
    academic_year VARCHAR(20) NOT NULL,
    semester VARCHAR(20) NOT NULL,
    start_date DATE NULL,
    end_date DATE NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_academic_period_year_semester (academic_year, semester),
    CONSTRAINT chk_academic_period_dates
        CHECK (end_date IS NULL OR start_date IS NULL OR end_date >= start_date)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS course_enrollments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    academic_period_id BIGINT NOT NULL,
    enrolled_on DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ENROLLED',
    PRIMARY KEY (id),
    CONSTRAINT fk_course_enrollments_student
        FOREIGN KEY (student_id) REFERENCES students (id),
    CONSTRAINT fk_course_enrollments_course
        FOREIGN KEY (course_id) REFERENCES courses (id),
    CONSTRAINT fk_course_enrollments_period
        FOREIGN KEY (academic_period_id) REFERENCES academic_periods (id),
    UNIQUE KEY uk_course_enrollments_student_course_period
        (student_id, course_id, academic_period_id),
    KEY idx_course_enrollments_period (academic_period_id),
    CONSTRAINT chk_course_enrollments_status
        CHECK (status IN ('ENROLLED', 'DROPPED', 'COMPLETED'))
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS lecturer_course_assignments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    lecturer_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    academic_period_id BIGINT NOT NULL,
    assigned_on DATE NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_lecturer_assignments_lecturer
        FOREIGN KEY (lecturer_id) REFERENCES lecturers (id),
    CONSTRAINT fk_lecturer_assignments_course
        FOREIGN KEY (course_id) REFERENCES courses (id),
    CONSTRAINT fk_lecturer_assignments_period
        FOREIGN KEY (academic_period_id) REFERENCES academic_periods (id),
    UNIQUE KEY uk_lecturer_assignments_lecturer_course_period
        (lecturer_id, course_id, academic_period_id),
    KEY idx_lecturer_assignments_course_period (course_id, academic_period_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS attendance (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    status VARCHAR(10) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_attendance_student
        FOREIGN KEY (student_id) REFERENCES students (id),
    CONSTRAINT fk_attendance_course
        FOREIGN KEY (course_id) REFERENCES courses (id),
    UNIQUE KEY uk_attendance_student_course_date (student_id, course_id, attendance_date),
    KEY idx_attendance_course_date (course_id, attendance_date),
    CONSTRAINT chk_attendance_status CHECK (status IN ('Present', 'Absent', 'Late'))
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS marks (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    coursework DECIMAL(5,2) NOT NULL,
    exam DECIMAL(5,2) NOT NULL,
    total DECIMAL(5,2) NOT NULL,
    grade VARCHAR(2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_marks_student
        FOREIGN KEY (student_id) REFERENCES students (id),
    CONSTRAINT fk_marks_course
        FOREIGN KEY (course_id) REFERENCES courses (id),
    UNIQUE KEY uk_marks_student_course (student_id, course_id),
    KEY idx_marks_course (course_id),
    CONSTRAINT chk_marks_coursework CHECK (coursework BETWEEN 0 AND 40),
    CONSTRAINT chk_marks_exam CHECK (exam BETWEEN 0 AND 60),
    CONSTRAINT chk_marks_total CHECK (total BETWEEN 0 AND 100),
    CONSTRAINT chk_marks_grade CHECK (grade IN ('A', 'B', 'C', 'D', 'F'))
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    student_id BIGINT NULL,
    must_change_password BOOLEAN NOT NULL DEFAULT TRUE,
    failed_login_attempts INT NOT NULL DEFAULT 0,
    locked_until DATETIME NULL,
    last_login_at DATETIME NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_users_student
        FOREIGN KEY (student_id) REFERENCES students (id),
    UNIQUE KEY uk_users_student (student_id),
    CONSTRAINT chk_users_role CHECK (role IN ('ADMIN', 'LECTURER', 'STUDENT'))
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS transcript (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    gpa DECIMAL(5,2) NOT NULL,
    academic_status VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_transcript_student
        FOREIGN KEY (student_id) REFERENCES students (id),
    UNIQUE KEY uk_transcript_student (student_id),
    CONSTRAINT chk_transcript_gpa CHECK (gpa BETWEEN 0 AND 5.00)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS system_settings (
    id BIGINT NOT NULL AUTO_INCREMENT,
    setting_key VARCHAR(100) NOT NULL,
    setting_value VARCHAR(1000) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_system_settings_key (setting_key)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    actor_username VARCHAR(100) NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id VARCHAR(100) NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    KEY idx_audit_logs_created_at (created_at),
    KEY idx_audit_logs_actor (actor_username)
) ENGINE=InnoDB;