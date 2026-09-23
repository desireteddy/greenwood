package com.smartcampus.config;

import com.smartcampus.repository.UserRepository;
import com.smartcampus.security.FirstLoginFilter;
import com.smartcampus.security.LoginAttemptService;
import com.smartcampus.security.LoginFailureHandler;
import com.smartcampus.security.LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, UserRepository userRepository,
                                            LoginAttemptService loginAttemptService) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(new LoginSuccessHandler(loginAttemptService, userRepository))
                        .failureHandler(new LoginFailureHandler(loginAttemptService))
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll())
                .exceptionHandling(exception -> exception.accessDeniedPage("/access-denied"))
                .addFilterAfter(new FirstLoginFilter(userRepository), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
