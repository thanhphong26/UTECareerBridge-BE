package com.pn.career.configurations;

import com.pn.career.models.User;
import com.pn.career.repositories.UserRepository;
import com.pn.career.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserRepository userRepository;
    //user's detail object
    @Bean
    public UserDetailsService userDetailsService() {
        return subject -> {
            // Attempt to find user by phone number or email
            User user = userRepository.findUserByPhoneNumber(subject)
                    .orElseGet(() -> userRepository.findUserByEmail(subject)
                            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng có thông tin email/sdt: " + subject)));
            if (!user.isActive()) {
                String reasonBlocked = user.getReasonBlocked() != null ? user.getReasonBlocked() : "Không xác định";
                throw new LockedException("Tài khoản của bạn đã bị khóa. Lý do: " + reasonBlocked);
            }
            return new UserDetailsImpl(user);
        };
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}
