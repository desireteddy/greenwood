package com.smartcampus.security;

import com.smartcampus.model.User;
import com.smartcampus.repository.UserRepository;
import com.smartcampus.repository.StudentRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public CustomUserDetailsService(UserRepository userRepository, StudentRepository studentRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            com.smartcampus.model.Student student = studentRepository.findByStudentNumber(username);
            if (student != null) {
                user = userRepository.findByStudentId(student.getId());
            }
        }
        if (user == null) {
            throw new UsernameNotFoundException("Account not found");
        }

        String role = user.getRole() == null ? "" : user.getRole().trim().toUpperCase();
        if (!role.equals("ADMIN") && !role.equals("LECTURER") && !role.equals("STUDENT")) {
            throw new UsernameNotFoundException("Account has an invalid role");
        }
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new UsernameNotFoundException("Account is temporarily locked");
        }

        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(role)
                .accountLocked(false)
                .build();
    }
}
