package com.smartcampus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.dao.DataIntegrityViolationException;

import com.smartcampus.model.Student;
import com.smartcampus.repository.StudentRepository;

@Controller
public class StudentController {
    private final StudentRepository repository;

    public StudentController(StudentRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/students")
    @PreAuthorize("hasRole('ADMIN')")
    public String students(Model model) {
        model.addAttribute("students", repository.findAll());
        model.addAttribute("pageTitle", "Students");
        model.addAttribute("pageSubtitle", "Student directory and records");
        return "students";
    }

    @GetMapping("/addStudent")
    @PreAuthorize("hasRole('ADMIN')")
    public String addStudentForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("pageTitle", "Add student");
        model.addAttribute("pageSubtitle", "Create a student record");
        return "student-form";
    }

    @GetMapping("/editStudent/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editStudent(@PathVariable @NonNull Long id, Model model) {
        Student student = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Student not found"));
        model.addAttribute("student", student);
        model.addAttribute("pageTitle", "Edit student");
        model.addAttribute("pageSubtitle", "Update a student record");
        return "student-form";
    }
    @PostMapping("/deleteStudent/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteStudent(@PathVariable @NonNull Long id, Model model) {
        try {
            repository.deleteById(id);
            repository.flush();
            return "redirect:/students";
        } catch (DataIntegrityViolationException ex) {
            model.addAttribute("students", repository.findAll());
            model.addAttribute("pageTitle", "Students");
            model.addAttribute("pageSubtitle", "Student directory and records");
            model.addAttribute("error", "This student cannot be deleted because academic records or an account still reference the student.");
            return "students";
        }
    }

    @PostMapping("/saveStudent")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveStudent(@ModelAttribute @NonNull Student student, Model model) {
        String number = student.getStudentNumber() == null ? "" : student.getStudentNumber().trim();
        if (number.isBlank() || student.getFirstName() == null || student.getFirstName().isBlank()
                || student.getLastName() == null || student.getLastName().isBlank()
                || student.getProgram() == null || student.getProgram().isBlank()) {
            model.addAttribute("student", student);
            model.addAttribute("pageTitle", student.getId() == null ? "Add student" : "Edit student");
            model.addAttribute("pageSubtitle", "Create or update a student record");
            model.addAttribute("error", "Student number, first name, last name and program are required.");
            return "student-form";
        }
        Student existing = repository.findByStudentNumber(number);
        if (existing != null && (student.getId() == null || !existing.getId().equals(student.getId()))) {
            model.addAttribute("student", student);
            model.addAttribute("pageTitle", student.getId() == null ? "Add student" : "Edit student");
            model.addAttribute("pageSubtitle", "Create or update a student record");
            model.addAttribute("error", "Student number already exists. Use a unique student number.");
            return "student-form";
        }
        student.setStudentNumber(number);
        student.setFirstName(student.getFirstName().trim());
        student.setLastName(student.getLastName().trim());
        student.setProgram(student.getProgram().trim());
        repository.save(student);
        return "redirect:/students";
    }
}