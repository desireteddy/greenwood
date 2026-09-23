package com.smartcampus.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;

import com.smartcampus.model.Marks;
import com.smartcampus.model.Transcript;
import com.smartcampus.repository.CourseRepository;
import com.smartcampus.repository.MarksRepository;
import com.smartcampus.repository.TranscriptRepository;

@Service
public class TranscriptService {
    private final MarksRepository marksRepository;
    private final CourseRepository courseRepository;
    private final TranscriptRepository transcriptRepository;

    public TranscriptService(MarksRepository marksRepository, CourseRepository courseRepository,
                             TranscriptRepository transcriptRepository) {
        this.marksRepository = marksRepository;
        this.courseRepository = courseRepository;
        this.transcriptRepository = transcriptRepository;
    }

    public void recalculate(Long studentId) {
        List<Marks> marks = marksRepository.findByStudent(studentId);
        BigDecimal qualityPoints = BigDecimal.ZERO;
        int credits = 0;
        for (Marks mark : marks) {
            Long courseId = mark.getCourse();
            if (courseId == null) {
                continue;
            }
            var course = courseRepository.findById(courseId).orElse(null);
            if (course == null) {
                continue;
            }
            int creditUnits = course.getCreditUnits();
            qualityPoints = qualityPoints.add(gradePoints(mark.getGrade()).multiply(BigDecimal.valueOf(creditUnits)));
            credits += creditUnits;
        }
        BigDecimal gpa = credits == 0
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : qualityPoints.divide(BigDecimal.valueOf(credits), 2, RoundingMode.HALF_UP);
        Transcript transcript = transcriptRepository.findByStudentId(studentId).orElseGet(Transcript::new);
        transcript.setStudentId(studentId);
        transcript.setGpa(gpa);
        transcript.setAcademicStatus(gpa.compareTo(BigDecimal.valueOf(2.0)) >= 0 ? "Good Standing" : "Academic Probation");
        transcriptRepository.save(transcript);
    }

    private BigDecimal gradePoints(String grade) {
        return switch (grade == null ? "" : grade.toUpperCase()) {
            case "A" -> BigDecimal.valueOf(5);
            case "B" -> BigDecimal.valueOf(4);
            case "C" -> BigDecimal.valueOf(3);
            case "D" -> BigDecimal.valueOf(2);
            case "F" -> BigDecimal.ZERO;
            default -> BigDecimal.ZERO;
        };
    }
}
