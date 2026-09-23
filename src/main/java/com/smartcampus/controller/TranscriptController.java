package com.smartcampus.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import com.smartcampus.model.User;
import com.smartcampus.repository.UserRepository;
import com.smartcampus.repository.TranscriptRepository;

@Controller
public class TranscriptController {
    private final UserRepository userRepository;
    private final TranscriptRepository transcriptRepository;

    public TranscriptController(UserRepository userRepository, TranscriptRepository transcriptRepository) {
        this.userRepository = userRepository;
        this.transcriptRepository = transcriptRepository;
    }

    @GetMapping("/transcripts")
    @PreAuthorize("isAuthenticated()")
    public String transcripts(Authentication authentication, Model model) {
        User user = userRepository.findByUsername(authentication.getName());
        if (user != null && "STUDENT".equals(user.getRole()) && user.getStudentId() != null) {
            model.addAttribute("transcript", transcriptRepository.findByStudentId(user.getStudentId()).orElse(null));
        } else {
            model.addAttribute("transcript", null);
        }
        model.addAttribute("pageTitle", "Transcript");
        model.addAttribute("pageSubtitle", "Academic performance record");
        return "transcript";
    }
}
