package com.smartcampus.repository;

import com.smartcampus.model.Marks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MarksRepository extends JpaRepository<Marks, Long> {
    List<Marks> findByStudent(Long studentId);
    Optional<Marks> findByStudentAndCourse(Long studentId, Long courseId);
}
