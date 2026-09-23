package com.smartcampus.security;

import com.smartcampus.model.User;
import com.smartcampus.repository.StudentRepository;
import com.smartcampus.repository.UserRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomUserDetailsServiceTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final StudentRepository studentRepository = mock(StudentRepository.class);
    private final CustomUserDetailsService service =
            new CustomUserDetailsService(userRepository, studentRepository);

    @Test
    void normalizesValidRole() {
        User user = new User("student01", "{bcrypt}hash", "student");
        when(userRepository.findByUsername("student01")).thenReturn(user);

        assertEquals("ROLE_STUDENT", service.loadUserByUsername("student01")
                .getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void rejectsInvalidRole() {
        User user = new User("bad", "{bcrypt}hash", "UNKNOWN");
        when(userRepository.findByUsername("bad")).thenReturn(user);

        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,
                () -> service.loadUserByUsername("bad"));
    }
}
