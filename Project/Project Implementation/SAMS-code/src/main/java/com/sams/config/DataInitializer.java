package com.sams.config;

import com.sams.entity.User;
import com.sams.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Data initializtion config - runs when app starts up
 * this creates the default super admin so we can actually login lol
 *
 * IMPORTANT: change the default password after first login!!
 * we should probaly move this to a migration script later but this works for now
 */
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initializeData(UserRepository userRepository,
                                           PasswordEncoder passwordEncoder) {
        return args -> {
            // check if we already have a super admin - dont want duplicates
            if (userRepository.findByUsername("superadmin").isEmpty()) {
                // create the super admin account
                // this is basically the god account lol
                User superAdmin = new User();
                superAdmin.setUsername("superadmin");
                superAdmin.setFirstName("Super");
                superAdmin.setLastName("Administrator");
                superAdmin.setEmail("superadmin@sams.edu");
                // TODO: maybe use environment variable for default password?
                superAdmin.setPassword(passwordEncoder.encode("Admin@123"));
                superAdmin.setRole("SUPER_ADMIN");
                superAdmin.setActive(true);

                // give super admin ALL the permissions
                // this is a bit ugly but it works - should refactor someday
                superAdmin.setPermissions("[\"USER_CREATE\",\"USER_READ\",\"USER_UPDATE\",\"USER_DELETE\"," +
                        "\"COURSE_CREATE\",\"COURSE_UPDATE\",\"COURSE_DELETE\"," +
                        "\"ENROLLMENT_MANAGE\",\"GRADE_MANAGE\",\"ASSIGNMENT_CREATE\",\"ASSIGNMENT_MANAGE\"," +
                        "\"NOTIFICATION_SEND\",\"REPORT_VIEW\",\"SYSTEM_SETTINGS\",\"PAYMENT_APPROVE\"]");

                userRepository.save(superAdmin);

                // print this so we know it worked - helpfull for debugging
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
