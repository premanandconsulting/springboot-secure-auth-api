package com.premanand.secureauth.config;

import com.premanand.secureauth.user.entity.Role;
import com.premanand.secureauth.user.entity.User;
import com.premanand.secureauth.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(UserRepository userRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User user = new User();
                user.setUsername("admin");
                user.setEmail("admin@test.com");
                user.setPassword(passwordEncoder.encode("Admin@123"));
                user.setRoles(Set.of(Role.ADMIN));
                userRepository.save(user);
            }
        };
    }
}
