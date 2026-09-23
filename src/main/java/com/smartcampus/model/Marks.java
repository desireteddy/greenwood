package com.smartcampus.model;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.lang.Nullable;

@Entity
@Table(name = "marks")
public class Marks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Nullable Long id;

    @Column(name = "student_id", nullable = false)
    private @Nullable Long student;

    @Column(name = "course_id", nullable = false)
    private @Nullable Long course;

    @Column(name = "coursework", nullable = false, precision = 5, scale = 2)
    private @Nullable BigDecimal coursework;

    @Column(name = "exam", nullable = false, precision = 5, scale = 2)
    private @Nullable BigDecimal exam;

    @Column(name = "total", nullable = false, precision = 5, scale = 2)
    private @Nullable BigDecimal total;

    @Column(name = "grade", nullable = false, length = 2)
    private @Nullable String grade;

    public @Nullable Long getId() {
        return id;
    }

    public void setId(@Nullable Long id) {
        this.id = id;
    }

    public @Nullable Long getStudent() {
        return student;
    }

    public void setStudent(@Nullable Long student) {
        this.student = student;
    }

    public @Nullable Long getCourse() {
        return course;
    }

    public void setCourse(@Nullable Long course) {
        this.course = course;
    }

    public @Nullable BigDecimal getCoursework() {
        return coursework;
    }

    public void setCoursework(@Nullable BigDecimal coursework) {
        this.coursework = coursework;
    }

    public @Nullable BigDecimal getExam() {
        return exam;
    }

    public void setExam(@Nullable BigDecimal exam) {
        this.exam = exam;
    }

    public @Nullable BigDecimal getTotal() {
        return total;
    }

    public void setTotal(@Nullable BigDecimal total) {
        this.total = total;
    }

    public @Nullable String getGrade() {
        return grade;
    }

    public void setGrade(@Nullable String grade) {
        this.grade = grade;
    }
}