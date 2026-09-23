package com.smartcampus.controller;

import com.smartcampus.model.User;
import com.smartcampus.repository.UserRepository;
import com.smartcampus.repository.StudentRepository;
import com.smartcampus.web.AccountForm;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import com.smartcampus.service.AuditService;

@Controller
public class AccountController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StudentRepository studentRepository;
    private final AuditService auditService;

    public AccountController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                             StudentRepository studentRepository, AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.studentRepository = studentRepository;
        this.auditService = auditService;
    }

    @GetMapping("/change-password")
    public String changePasswordPage(@RequestParam(name = "firstLogin", defaultValue = "false") boolean firstLogin,
                                     Authentication authentication, Model model) {
        User user = userRepository.findByUsername(authentication.getName());
        boolean forced = user != null && user.isMustChangePassword();
        model.addAttribute("firstLogin", firstLogin || forced);
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(Authentication authentication,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Model model) {
        User user = userRepository.findByUsername(authentication.getName());
        if (user == null) {
            model.addAttribute("error", "Your account could not be found. Please sign in again.");
            model.addAttribute("firstLogin", false);
            return "change-password";
        }

        boolean forced = user.isMustChangePassword();
        model.addAttribute("firstLogin", forced);

        if (currentPassword == null || currentPassword.isBlank()
                || !passwordEncoder.matches(currentPassword, user.getPassword())) {
            model.addAttribute("error", "The current password is incorrect. Please enter the temporary password you were given.");
            return "change-password";
        }

        String validationError = validateNewPassword(currentPassword, newPassword, confirmPassword);
        if (validationError != null) {
            model.addAttribute("error", validationError);
            return "change-password";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setMustChangePassword(false);
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);
        auditService.record(authentication.getName(), forced ? "FIRST_LOGIN_PASSWORD_CHANGE" : "CHANGE_PASSWORD",
                "USER", user.getId().toString());
        return "redirect:/dashboard?passwordChanged=true";
    }

    private String validateNewPassword(String currentPassword, String newPassword, String confirmPassword) {
        if (newPassword == null || newPassword.length() < 8) {
            return "Your new password must contain at least 8 characters.";
        }
        if (newPassword.length() > 128) {
            return "Your new password must not exceed 128 characters.";
        }
        if (!newPassword.equals(confirmPassword)) {
            return "The new password and confirmation do not match.";
        }
        if (newPassword.equals(currentPassword)) {
            return "Your new password must be different from your current password.";
        }
        if (!newPassword.matches(".*[A-Z].*")) {
            return "Your new password must contain at least one uppercase letter.";
        }
        if (!newPassword.matches(".*[a-z].*")) {
            return "Your new password must contain at least one lowercase letter.";
        }
        if (!newPassword.matches(".*\\d.*")) {
            return "Your new password must contain at least one number.";
        }
        return null;
    }

    @GetMapping("/accounts")
    @PreAuthorize("hasRole('ADMIN')")
    public String accounts(Model model) {
        model.addAttribute("accounts", userRepository.findAll());
        return "accounts";
    }

    @GetMapping("/addAccount")
    @PreAuthorize("hasRole('ADMIN')")
    public String addAccount(Model model) {
        model.addAttribute("accountForm", new AccountForm());
        model.addAttribute("students", studentRepository.findAll());
        return "account-form";
    }

    @PostMapping("/saveAccount")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveAccount(AccountForm form, Authentication authentication, Model model) {
        String role = form.getRole() == null ? "" : form.getRole().trim().toUpperCase();
        if (!role.equals("ADMIN") && !role.equals("LECTURER") && !role.equals("STUDENT")) {
            return accountError(model, form, "Select a valid role.");
        }
        if (form.getTemporaryPassword() == null || !isStrongPassword(form.getTemporaryPassword())) {
            return accountError(model, form, "Temporary password must be 8–128 characters and include uppercase, lowercase and a number.");
        }
        if (!form.getTemporaryPassword().equals(form.getConfirmTemporaryPassword())) {
            return accountError(model, form, "Temporary passwords do not match.");
        }

        String username = form.getUsername() == null ? null : form.getUsername().trim();
        Long studentId = null;
        if (role.equals("STUDENT")) {
            if (form.getStudentId() == null) {
                return accountError(model, form, "Select a student account.");
            }
            com.smartcampus.model.Student student = studentRepository.findById(form.getStudentId()).orElse(null);
            if (student == null) {
                return accountError(model, form, "Selected student does not exist.");
            }
            username = student.getStudentNumber();
            studentId = student.getId();
        }
        if (username == null || username.isBlank() || userRepository.findByUsername(username) != null) {
            return accountError(model, form, "Username is required and must be unique.");
        }
        if (role.equals("STUDENT") && userRepository.findByStudentId(studentId) != null) {
            return accountError(model, form, "That student already has an account.");
        }
        if (form.getTemporaryPassword().equalsIgnoreCase(username)) {
            return accountError(model, form, "Temporary password must not be the same as the username or student number.");
        }

        User user = new User();
        user.setUsername(username);
        user.setRole(role);
        user.setStudentId(studentId);
        user.setPassword(passwordEncoder.encode(form.getTemporaryPassword()));
        user.setMustChangePassword(true);
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);
        auditService.record(authentication.getName(), "CREATE", "USER", user.getId().toString());
        return "redirect:/accounts?saved=true";
    }

    @PostMapping("/resetAccount/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String resetAccount(@PathVariable Long id,
                               @RequestParam String temporaryPassword,
                               @RequestParam String confirmTemporaryPassword,
                               Authentication authentication, Model model) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return "redirect:/accounts";
        }
        if (!isStrongPassword(temporaryPassword)) {
            return accountListError(model, "Temporary password must be 8–128 characters and include uppercase, lowercase and a number.");
        }
        if (!temporaryPassword.equals(confirmTemporaryPassword)) {
            return accountListError(model, "Temporary passwords do not match.");
        }
        if (temporaryPassword.equalsIgnoreCase(user.getUsername())) {
            return accountListError(model, "Temporary password must not be the same as the username.");
        }

        user.setPassword(passwordEncoder.encode(temporaryPassword));
        user.setMustChangePassword(true);
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);
        auditService.record(authentication.getName(), "RESET_PASSWORD", "USER", id.toString());
        return "redirect:/accounts?reset=true";
    }

    private boolean isStrongPassword(String password) {
        return password != null && password.length() >= 8 && password.length() <= 128
                && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*\\d.*");
    }

    private String accountError(Model model, AccountForm form, String message) {
        model.addAttribute("accountForm", form);
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("error", message);
        return "account-form";
    }

    private String accountListError(Model model, String message) {
        model.addAttribute("accounts", userRepository.findAll());
        model.addAttribute("error", message);
        return "accounts";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
