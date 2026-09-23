package com.smartcampus.controller;

import com.smartcampus.model.Course;
import com.smartcampus.repository.CourseRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.dao.DataIntegrityViolationException;

@Controller
public class CourseController {

    private final CourseRepository repository;

    public CourseController(CourseRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/courses")
    @PreAuthorize("isAuthenticated()")
    public String courses(Model model) {
        model.addAttribute("courses", repository.findAll());
        model.addAttribute("pageTitle", "Courses");
        model.addAttribute("pageSubtitle", "Academic course catalogue");
        return "courses";
    }

    @GetMapping("/addCourse")
    @PreAuthorize("hasRole('ADMIN')")
    public String addCourseForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("pageTitle", "Add course");
        model.addAttribute("pageSubtitle", "Create an academic course");
        return "course-form";
    }

    @PostMapping("/saveCourses")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveCourses(@ModelAttribute @NonNull Course course, Model model) {
        String code = course.getCourseCode() == null ? "" : course.getCourseCode().trim().toUpperCase();
        String name = course.getCourseName() == null ? "" : course.getCourseName().trim();
        if (code.isBlank() || name.isBlank() || course.getCreditUnits() <= 0) {
            model.addAttribute("course", course);
            model.addAttribute("pageTitle", "Add course");
            model.addAttribute("pageSubtitle", "Create an academic course");
            model.addAttribute("error", "Course code, course name and positive credit units are required.");
            return "course-form";
        }
        boolean duplicate = repository.findAll().stream()
                .anyMatch(c -> code.equalsIgnoreCase(c.getCourseCode())
                        && (course.getId() == null || !c.getId().equals(course.getId())));
        if (duplicate) {
            model.addAttribute("course", course);
            model.addAttribute("pageTitle", "Add course");
            model.addAttribute("pageSubtitle", "Create an academic course");
            model.addAttribute("error", "Course code already exists. Use a unique course code.");
            return "course-form";
        }
        course.setCourseCode(code);
        course.setCourseName(name);
        try {
            repository.save(course);
            return "redirect:/courses";
        } catch (DataIntegrityViolationException ex) {
            model.addAttribute("course", course);
            model.addAttribute("pageTitle", "Add course");
            model.addAttribute("pageSubtitle", "Create an academic course");
            model.addAttribute("error", "The course could not be saved because the data conflicts with an existing record.");
            return "course-form";
        }
    }
}