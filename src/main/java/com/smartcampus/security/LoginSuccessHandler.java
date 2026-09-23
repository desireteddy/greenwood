package com.smartcampus.security;

import com.smartcampus.model.User;
import com.smartcampus.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

/** Routes a user directly to the required first-login password change when needed. */
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final LoginAttemptService loginAttemptService;
    private final UserRepository userRepository;

    public LoginSuccessHandler(LoginAttemptService loginAttemptService, UserRepository userRepository) {
        this.loginAttemptService = loginAttemptService;
        this.userRepository = userRepository;
        setDefaultTargetUrl("/dashboard");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        loginAttemptService.success(authentication.getName());

        User user = userRepository.findByUsername(authentication.getName());
        if (user != null && user.isMustChangePassword()) {
            response.sendRedirect(request.getContextPath() + "/change-password?firstLogin=true");
            return;
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
