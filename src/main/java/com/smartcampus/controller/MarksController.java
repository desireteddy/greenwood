package com.smartcampus.controller;

import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import com.smartcampus.model.User;
import com.smartcampus.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.smartcampus.model.Marks;
import com.smartcampus.repository.MarksRepository;
import com.smartcampus.service.GradeCalculator;
import com.smartcampus.service.TranscriptService;
import com.smartcampus.service.AuditService;
import com.smartcampus.repository.StudentRepository;
import com.smartcampus.repository.CourseRepository;
import java.util.Objects;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Controller
public class MarksController {
    private final MarksRepository repository;
    private final GradeCalculator gradeCalculator = new GradeCalculator();
    private final UserRepository userRepository;
    private final TranscriptService transcriptService;
    private final AuditService auditService;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public MarksController(MarksRepository repository, UserRepository userRepository,
                           TranscriptService transcriptService, AuditService auditService,
                           StudentRepository studentRepository, CourseRepository courseRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.transcriptService = transcriptService;
        this.auditService = auditService;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @GetMapping("/marks")
    @PreAuthorize("isAuthenticated()")
    public String marks(Model model, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName());
        if (user != null && "STUDENT".equals(user.getRole())) {
            model.addAttribute("marks", user.getStudentId() == null
                    ? Collections.emptyList() : repository.findByStudent(user.getStudentId()));
        } else {
            model.addAttribute("marks", repository.findAll());
        }
        List<com.smartcampus.model.Student> students = studentRepository.findAll();
        List<com.smartcampus.model.Course> courses = courseRepository.findAll();
        Map<Long, String> studentNames = students.stream().collect(Collectors.toMap(
                com.smartcampus.model.Student::getId,
                st -> st.getFirstName() + " " + st.getLastName(),
                (a, b) -> a));
        Map<Long, String> courseNames = courses.stream().collect(Collectors.toMap(
                com.smartcampus.model.Course::getId,
                com.smartcampus.model.Course::getCourseName,
                (a, b) -> a));
        model.addAttribute("studentNames", studentNames);
        model.addAttribute("courseNames", courseNames);
        model.addAttribute("pageTitle", "Marks & Results");
        model.addAttribute("pageSubtitle", "Results register and grade management");
        return "marks";
    }

    @GetMapping("/addMarks")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public String addMarksForm(Model model) {
        model.addAttribute("marks", new Marks());
        addMarkFormOptions(model);
        model.addAttribute("pageTitle", "Record marks");
        model.addAttribute("pageSubtitle", "Enter coursework and exam results");
        return "marks-form";
    }

    @PostMapping("/saveMarks")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public String saveMarks(@ModelAttribute @NonNull Marks marks, Authentication authentication,
                            Model model) {
        Long studentId = marks.getStudent();
        Long courseId = marks.getCourse();
        if (studentId == null || courseId == null
            || !studentRepository.existsById(Objects.requireNonNull(studentId))
            || !courseRepository.existsById(Objects.requireNonNull(courseId))) {
            model.addAttribute("marks", marks);
            addMarkFormOptions(model);
            model.addAttribute("pageTitle", "Record marks");
            model.addAttribute("pageSubtitle", "Enter coursework and exam results");
            model.addAttribute("error", "Select an existing student and course.");
            return "marks-form";
        }

        BigDecimal coursework = marks.getCoursework() == null
                ? BigDecimal.ZERO : marks.getCoursework().setScale(2, RoundingMode.HALF_UP);
        BigDecimal exam = marks.getExam() == null
                ? BigDecimal.ZERO : marks.getExam().setScale(2, RoundingMode.HALF_UP);
        if (coursework.compareTo(BigDecimal.ZERO) < 0 || coursework.compareTo(BigDecimal.valueOf(40)) > 0
                || exam.compareTo(BigDecimal.ZERO) < 0 || exam.compareTo(BigDecimal.valueOf(60)) > 0) {
            model.addAttribute("marks", marks);
            addMarkFormOptions(model);
            model.addAttribute("pageTitle", "Record marks");
            model.addAttribute("pageSubtitle", "Enter coursework and exam results");
            model.addAttribute("error", "Coursework must be 0-40 and exam must be 0-60.");
            return "marks-form";
        }
        BigDecimal total = coursework.add(exam).setScale(2, RoundingMode.HALF_UP);
        Marks saved = repository.findByStudentAndCourse(studentId, courseId).orElseGet(Marks::new);
        saved.setStudent(studentId);
        saved.setCourse(courseId);
        saved.setCoursework(coursework);
        saved.setExam(exam);
        saved.setTotal(total);
        saved.setGrade(gradeCalculator.calculateGrade(total.doubleValue()));
        saved = repository.save(saved);
        transcriptService.recalculate(studentId);
        auditService.record(authentication.getName(), "SAVE_OR_UPDATE", "MARKS", saved.getId().toString());
        return "redirect:/marks";
    }
    private void addMarkFormOptions(Model model) {
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("courses", courseRepository.findAll());
    }
}
