package com.college.eventmanagement.config;

import com.college.eventmanagement.entity.Role;
import com.college.eventmanagement.entity.User;
import com.college.eventmanagement.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedUsers(UserRepository users, PasswordEncoder encoder) {
        return args -> {
            if (users.findByEmail("admin@ems.com").isEmpty()) {
                User admin = new User();
                admin.setFullName("Admin");
                admin.setEmail("admin@ems.com");
                admin.setPasswordHash(encoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                users.save(admin);
            }
            if (users.findByEmail("org@ems.com").isEmpty()) {
                User organizer = new User();
                organizer.setFullName("Organizer");
                organizer.setEmail("org@ems.com");
                organizer.setPasswordHash(encoder.encode("org12345"));
                organizer.setRole(Role.ORGANIZER);
                users.save(organizer);
            }
        };
    }
}
