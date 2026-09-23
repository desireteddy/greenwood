package com.smartcampus.security;

import com.smartcampus.model.User;
import com.smartcampus.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Enforces the first-login password change without breaking the change-password
 * page itself or its static assets.
 */
public class FirstLoginFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;

    public FirstLoginFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();

        // Never intercept the password page, authentication endpoints, or static assets.
        // The previous implementation intercepted /css/** during first login, which
        // could make the password page appear unstyled or partially broken.
        if (isExemptPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            User user = userRepository.findByUsername(authentication.getName());
            if (user != null && user.isMustChangePassword()) {
                response.sendRedirect(request.getContextPath() + "/change-password?firstLogin=true");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isExemptPath(String path) {
        return path == null
                || path.equals("/change-password")
                || path.equals("/login")
                || path.equals("/logout")
                || path.equals("/error")
                || path.equals("/favicon.ico")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/webjars/");
    }
}
