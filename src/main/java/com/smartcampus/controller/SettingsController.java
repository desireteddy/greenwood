package com.smartcampus.controller;

import com.smartcampus.service.AuditService;
import com.smartcampus.service.SystemSettingsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class SettingsController {
    private final SystemSettingsService settingsService;
    private final AuditService auditService;

    public SettingsController(SystemSettingsService settingsService, AuditService auditService) {
        this.settingsService = settingsService;
        this.auditService = auditService;
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("settings", settingsService.getAll());
        return "settings";
    }

    @PostMapping("/settings")
    public String saveSettings(
            @RequestParam(required = false) String schoolName,
            @RequestParam(required = false) String schoolTagline,
            @RequestParam(required = false) String welcomeTitle,
            @RequestParam(required = false) String welcomeMessage,
            @RequestParam(required = false) String contactEmail,
            @RequestParam(required = false) String contactPhone,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String logoUrl,
            @RequestParam(required = false) String footer,
            @RequestParam(required = false) String academicYear,
            Authentication authentication) {
        Map<String, String> values = new LinkedHashMap<>();
        values.put(SystemSettingsService.SCHOOL_NAME, schoolName);
        values.put(SystemSettingsService.SCHOOL_TAGLINE, schoolTagline);
        values.put(SystemSettingsService.WELCOME_TITLE, welcomeTitle);
        values.put(SystemSettingsService.WELCOME_MESSAGE, welcomeMessage);
        values.put(SystemSettingsService.CONTACT_EMAIL, contactEmail);
        values.put(SystemSettingsService.CONTACT_PHONE, contactPhone);
        values.put(SystemSettingsService.ADDRESS, address);
        values.put(SystemSettingsService.LOGO_URL, logoUrl);
        values.put(SystemSettingsService.FOOTER, footer);
        values.put(SystemSettingsService.ACADEMIC_YEAR, academicYear);
        settingsService.save(values);
        auditService.record(authentication.getName(), "UPDATE", "SYSTEM_SETTINGS", null);
        return "redirect:/settings?saved=true";
    }
}
