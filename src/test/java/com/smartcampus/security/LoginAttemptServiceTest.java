package com.smartcampus.security;

import com.smartcampus.model.Student;
import com.smartcampus.model.User;
import com.smartcampus.repository.StudentRepository;
import com.smartcampus.repository.UserRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoginAttemptServiceTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final StudentRepository studentRepository = mock(StudentRepository.class);
    private final LoginAttemptService service = new LoginAttemptService(userRepository, studentRepository);

    @Test
    void resolvesStudentNumberBeforeResettingLoginState() {
        Student student = new Student();
        student.setId(42L);
        student.setStudentNumber("STU-100");

        User user = new User("STU-100", "{bcrypt}hash", "STUDENT");
        user.setStudentId(42L);
        user.setFailedLoginAttempts(3);
        user.setLockedUntil(java.time.LocalDateTime.now().plusMinutes(5));

        when(studentRepository.findByStudentNumber("STU-100")).thenReturn(student);
        when(userRepository.findByStudentId(42L)).thenReturn(user);

        service.success("STU-100");

        assertEquals(0, user.getFailedLoginAttempts());
        assertNull(user.getLockedUntil());
        assertNotNull(user.getLastLoginAt());
        verify(userRepository).save(user);
    }

    @Test
    void supportsSettingUserIdForBeanBinding() {
        User user = new User();
        user.setId(99L);

        assertEquals(99L, user.getId());
    }
}
