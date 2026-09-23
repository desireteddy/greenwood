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
@Table(name = "lecturer_course_assignments")
public class LecturerCourseAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lecturer_id", nullable = false)
    private Long lecturer;

    @Column(name = "course_id", nullable = false)
    private Long course;

    @Column(name = "academic_period_id", nullable = false)
    private Long academicPeriod;

    @Column(name = "assigned_on", nullable = false)
    private LocalDate assignedOn;

    public LecturerCourseAssignment() {
    }

    public LecturerCourseAssignment(Long lecturer, Long course, Long academicPeriod) {
        this.lecturer = lecturer;
        this.course = course;
        this.academicPeriod = academicPeriod;
        this.assignedOn = LocalDate.now();
    }

    @PrePersist
    void setDefaults() {
        if (assignedOn == null) {
            assignedOn = LocalDate.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getLecturer() { return lecturer; }
    public void setLecturer(Long lecturer) { this.lecturer = lecturer; }
    public Long getCourse() { return course; }
    public void setCourse(Long course) { this.course = course; }
    public Long getAcademicPeriod() { return academicPeriod; }
    public void setAcademicPeriod(Long academicPeriod) { this.academicPeriod = academicPeriod; }
    public LocalDate getAssignedOn() { return assignedOn; }
    public void setAssignedOn(LocalDate assignedOn) { this.assignedOn = assignedOn; }
}
