package com.smartcampus.security;

import com.smartcampus.model.User;
import com.smartcampus.repository.UserRepository;
import com.smartcampus.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoginAttemptService {
    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 15;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public LoginAttemptService(UserRepository userRepository, StudentRepository studentRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
    }

    public void failure(String username) {
        User user = resolveUser(username);
        if (user == null) {
            return;
        }
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);
        if (attempts >= MAX_ATTEMPTS) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCK_MINUTES));
            user.setFailedLoginAttempts(0);
        }
        userRepository.save(user);
    }

    public void success(String username) {
        User user = resolveUser(username);
        if (user == null) {
            return;
        }
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }

    private User resolveUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            var student = studentRepository.findByStudentNumber(username);
            if (student != null) {
                user = userRepository.findByStudentId(student.getId());
            }
        }
        return user;
    }
}
