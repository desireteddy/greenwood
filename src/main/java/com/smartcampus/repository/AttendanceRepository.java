package com.smartcampus.repository;

import com.smartcampus.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudent(Long studentId);
    Optional<Attendance> findByStudentAndCourseAndAttendanceDate(Long studentId, Long courseId, LocalDate attendanceDate);
}
