package com.smartcampus.controller;

import java.util.Collections;
import java.util.Objects;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.smartcampus.model.Attendance;
import com.smartcampus.model.User;
import com.smartcampus.repository.AttendanceRepository;
import com.smartcampus.repository.CourseRepository;
import com.smartcampus.repository.StudentRepository;
import com.smartcampus.repository.UserRepository;
import com.smartcampus.service.AuditService;

@Controller
public class AttendanceController {
    private final AttendanceRepository repository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public AttendanceController(AttendanceRepository repository, UserRepository userRepository,
                                AuditService auditService, StudentRepository studentRepository,
                                CourseRepository courseRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.auditService = auditService;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @GetMapping("/attendance")
    @PreAuthorize("isAuthenticated()")
    public String attendance(Model model, org.springframework.security.core.Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName());
        if (user != null && "STUDENT".equals(user.getRole())) {
            model.addAttribute("attendanceRecords", user.getStudentId() == null
                    ? Collections.emptyList() : repository.findByStudent(user.getStudentId()));
        } else {
            model.addAttribute("attendanceRecords", repository.findAll());
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
        model.addAttribute("pageTitle", "Attendance");
        model.addAttribute("pageSubtitle", "Daily attendance records");
        return "attendance";
    }

    @GetMapping("/addAttendance")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public String addAttendanceForm(Model model) {
        model.addAttribute("attendance", new Attendance());
        addAttendanceFormOptions(model);
        model.addAttribute("pageTitle", "Record attendance");
        model.addAttribute("pageSubtitle", "Record daily participation");
        return "attendance-form";
    }

    @PostMapping("/saveAttendance")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public String saveAttendance(@ModelAttribute @NonNull Attendance attendance,
                                 org.springframework.security.core.Authentication authentication, Model model) {
        Long studentId = attendance.getStudent();
        Long courseId = attendance.getCourse();
        if (studentId == null || courseId == null
            || !studentRepository.existsById(Objects.requireNonNull(studentId))
            || !courseRepository.existsById(Objects.requireNonNull(courseId))
                || attendance.getAttendanceDate() == null
                || !java.util.Set.of("Present", "Absent", "Late").contains(attendance.getStatus())) {
            model.addAttribute("attendance", attendance);
            addAttendanceFormOptions(model);
            model.addAttribute("pageTitle", "Record attendance");
            model.addAttribute("pageSubtitle", "Record daily participation");
            model.addAttribute("error", "Select valid student, course, date, and attendance status.");
            return "attendance-form";
        }
        Attendance saved = repository.findByStudentAndCourseAndAttendanceDate(
                        studentId, courseId, attendance.getAttendanceDate())
                .orElseGet(Attendance::new);
        saved.setStudent(studentId);
        saved.setCourse(courseId);
        saved.setAttendanceDate(attendance.getAttendanceDate());
        saved.setStatus(attendance.getStatus());
        saved = repository.save(saved);
        auditService.record(authentication.getName(), "SAVE_OR_UPDATE", "ATTENDANCE", saved.getId().toString());
        return "redirect:/attendance";
    }
    private void addAttendanceFormOptions(Model model) {
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("courses", courseRepository.findAll());
    }
}
