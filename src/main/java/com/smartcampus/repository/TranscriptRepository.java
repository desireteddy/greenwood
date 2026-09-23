package com.smartcampus.repository;

import com.smartcampus.model.Transcript;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TranscriptRepository extends JpaRepository<Transcript, Long> {
    Optional<Transcript> findByStudentId(Long studentId);
}
