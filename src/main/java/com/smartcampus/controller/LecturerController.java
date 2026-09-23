package com.smartcampus.controller;

import com.smartcampus.model.Lecturer;
import com.smartcampus.repository.LecturerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.dao.DataIntegrityViolationException;

@Controller
public class LecturerController {
    private final LecturerRepository repository;

    public LecturerController(LecturerRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/lecturers")
    @PreAuthorize("hasRole('ADMIN')")
    public String lecturers(Model model) {
        model.addAttribute("lecturers", repository.findAll());
        model.addAttribute("pageTitle", "Lecturers");
        model.addAttribute("pageSubtitle", "Teaching staff directory");
        return "lecturers";
    }

    @GetMapping("/addLecturer")
    @PreAuthorize("hasRole('ADMIN')")
    public String addLecturerForm(Model model) {
        model.addAttribute("lecturer", new Lecturer());
        model.addAttribute("pageTitle", "Add lecturer");
        model.addAttribute("pageSubtitle", "Create a lecturer record");
        return "lecturer-form";
    }

    @PostMapping("/savelecturers")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveLecturer(@ModelAttribute @NonNull Lecturer lecturer, Model model) {
        String number = lecturer.getLecturerNumber() == null ? "" : lecturer.getLecturerNumber().trim();
        String name = lecturer.getFullName() == null ? "" : lecturer.getFullName().trim();
        if (number.isBlank() || name.isBlank()) {
            model.addAttribute("lecturer", lecturer);
            model.addAttribute("pageTitle", "Add lecturer");
            model.addAttribute("pageSubtitle", "Create a lecturer record");
            model.addAttribute("error", "Lecturer number and full name are required.");
            return "lecturer-form";
        }
        boolean duplicate = repository.findAll().stream()
                .anyMatch(l -> number.equalsIgnoreCase(l.getLecturerNumber())
                        && (lecturer.getId() == null || !l.getId().equals(lecturer.getId())));
        if (duplicate) {
            model.addAttribute("lecturer", lecturer);
            model.addAttribute("pageTitle", "Add lecturer");
            model.addAttribute("pageSubtitle", "Create a lecturer record");
            model.addAttribute("error", "Lecturer number already exists. Use a unique lecturer number.");
            return "lecturer-form";
        }
        lecturer.setLecturerNumber(number);
        lecturer.setFullName(name);
        try {
            repository.save(lecturer);
            return "redirect:/lecturers";
        } catch (DataIntegrityViolationException ex) {
            model.addAttribute("lecturer", lecturer);
            model.addAttribute("pageTitle", "Add lecturer");
            model.addAttribute("pageSubtitle", "Create a lecturer record");
            model.addAttribute("error", "The lecturer could not be saved because the data conflicts with an existing record.");
            return "lecturer-form";
        }
    }
}