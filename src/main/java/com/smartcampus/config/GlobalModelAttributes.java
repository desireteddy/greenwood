package com.smartcampus.config;

import com.smartcampus.service.SystemSettingsService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {
    private final SystemSettingsService settingsService;

    public GlobalModelAttributes(SystemSettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @ModelAttribute("schoolName")
    public String schoolName() {
        return settingsService.get(SystemSettingsService.SCHOOL_NAME, "Greenwood Eco School");
    }

    @ModelAttribute("schoolTagline")
    public String schoolTagline() {
        return settingsService.get(SystemSettingsService.SCHOOL_TAGLINE, "Learn • Grow • Protect the Planet");
    }

    @ModelAttribute("logoUrl")
    public String logoUrl() {
        return settingsService.get(SystemSettingsService.LOGO_URL, "");
    }

    @ModelAttribute("footerText")
    public String footerText() {
        return settingsService.get(SystemSettingsService.FOOTER, "Greenwood Eco School • Smart Campus");
    }
}
