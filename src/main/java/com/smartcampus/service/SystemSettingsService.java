package com.smartcampus.service;

import com.smartcampus.model.SystemSetting;
import com.smartcampus.repository.SystemSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class SystemSettingsService {
    public static final String SCHOOL_NAME = "school.name";
    public static final String SCHOOL_TAGLINE = "school.tagline";
    public static final String WELCOME_TITLE = "dashboard.welcomeTitle";
    public static final String WELCOME_MESSAGE = "dashboard.welcomeMessage";
    public static final String CONTACT_EMAIL = "school.contactEmail";
    public static final String CONTACT_PHONE = "school.contactPhone";
    public static final String ADDRESS = "school.address";
    public static final String LOGO_URL = "school.logoUrl";
    public static final String FOOTER = "school.footer";
    public static final String ACADEMIC_YEAR = "academic.currentYear";

    private final SystemSettingRepository repository;

    public SystemSettingsService(SystemSettingRepository repository) {
        this.repository = repository;
    }

    public String get(String key, String fallback) {
        return repository.findBySettingKey(key).map(SystemSetting::getSettingValue).orElse(fallback);
    }

    public Map<String, String> getAll() {
        Map<String, String> values = new LinkedHashMap<>();
        repository.findAll().forEach(s -> values.put(s.getSettingKey(), s.getSettingValue()));
        return values;
    }

    @Transactional
    public void save(Map<String, String> values) {
        values.forEach((key, value) -> {
            if (key == null || key.isBlank()) return;
            String cleaned = value == null ? "" : value.trim();
            if (cleaned.length() > 1000) {
                throw new IllegalArgumentException("System setting value is too long for " + key);
            }
            SystemSetting setting = repository.findBySettingKey(key)
                    .orElseGet(() -> new SystemSetting(key, cleaned));
            setting.setSettingValue(cleaned);
            repository.save(setting);
        });
    }

    @Transactional
    public void seedDefaults() {
        Map<String, String> defaults = new LinkedHashMap<>();
        defaults.put(SCHOOL_NAME, "Greenwood Eco School");
        defaults.put(SCHOOL_TAGLINE, "Learn • Grow • Protect the Planet");
        defaults.put(WELCOME_TITLE, "Welcome back to Greenwood Eco School");
        defaults.put(WELCOME_MESSAGE, "Manage learning, attendance, results and school operations from one place.");
        defaults.put(CONTACT_EMAIL, "admin@greenwoodecoschool.local");
        defaults.put(CONTACT_PHONE, "+256 700 000 000");
        defaults.put(ADDRESS, "Greenwood Eco School, Uganda");
        defaults.put(LOGO_URL, "");
        defaults.put(FOOTER, "Greenwood Eco School • Smart Campus");
        defaults.put(ACADEMIC_YEAR, "2026/2027");
        saveIfMissing(defaults);
    }

    private void saveIfMissing(Map<String, String> defaults) {
        defaults.forEach((key, value) -> repository.findBySettingKey(key).orElseGet(() -> repository.save(new SystemSetting(key, value))));
    }
}
