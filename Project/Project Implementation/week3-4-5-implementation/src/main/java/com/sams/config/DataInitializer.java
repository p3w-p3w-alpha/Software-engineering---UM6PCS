package com.sams.config;

import com.sams.entity.User;
import com.sams.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Data initialization configuration
 * Creates the default SUPER_ADMIN account on application startup
 */
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initializeData(UserRepository userRepository,
                                           PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if SUPER_ADMIN already exists
            if (userRepository.findByUsername("superadmin").isEmpty()) {
                // Create SUPER_ADMIN account
                User superAdmin = new User();
                superAdmin.setUsername("superadmin");
                superAdmin.setFirstName("Super");
                superAdmin.setLastName("Administrator");
                superAdmin.setEmail("superadmin@sams.edu");
                superAdmin.setPassword(passwordEncoder.encode("Admin@123"));
                superAdmin.setRole("SUPER_ADMIN");
                superAdmin.setActive(true);

                // Set permissions (all permissions for super admin)
                superAdmin.setPermissions("[\"USER_CREATE\",\"USER_READ\",\"USER_UPDATE\",\"USER_DELETE\"," +
                        "\"COURSE_CREATE\",\"COURSE_UPDATE\",\"COURSE_DELETE\"," +
                        "\"ENROLLMENT_MANAGE\",\"GRADE_MANAGE\",\"ASSIGNMENT_CREATE\",\"ASSIGNMENT_MANAGE\"," +
                        "\"NOTIFICATION_SEND\",\"REPORT_VIEW\",\"SYSTEM_SETTINGS\",\"PAYMENT_APPROVE\"]");

                userRepository.save(superAdmin);

                System.out.println("========================================");
                System.out.println("SUPER ADMIN ACCOUNT CREATED");
                System.out.println("Username: superadmin");
                System.out.println("Password: Admin@123");
                System.out.println("PLEASE CHANGE THE PASSWORD AFTER FIRST LOGIN");
                System.out.println("========================================");
            } else {
                System.out.println("Super admin account already exists");
            }
        };
    }
}
