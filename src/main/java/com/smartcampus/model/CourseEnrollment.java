package com.smartcampus.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "course_enrollments")
public class CourseEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long student;

    @Column(name = "course_id", nullable = false)
    private Long course;

    @Column(name = "academic_period_id", nullable = false)
    private Long academicPeriod;

    @Column(name = "enrolled_on", nullable = false)
    private LocalDate enrolledOn;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "ENROLLED";

    public CourseEnrollment() {
    }

    public CourseEnrollment(Long student, Long course, Long academicPeriod) {
        this.student = student;
        this.course = course;
        this.academicPeriod = academicPeriod;
        this.enrolledOn = LocalDate.now();
    }

    @PrePersist
    void setDefaults() {
        if (enrolledOn == null) {
            enrolledOn = LocalDate.now();
        }
        if (status == null) {
            status = "ENROLLED";
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudent() { return student; }
    public void setStudent(Long student) { this.student = student; }
    public Long getCourse() { return course; }
    public void setCourse(Long course) { this.course = course; }
    public Long getAcademicPeriod() { return academicPeriod; }
    public void setAcademicPeriod(Long academicPeriod) { this.academicPeriod = academicPeriod; }
    public LocalDate getEnrolledOn() { return enrolledOn; }
    public void setEnrolledOn(LocalDate enrolledOn) { this.enrolledOn = enrolledOn; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
