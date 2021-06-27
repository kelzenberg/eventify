package com.eventify.api.entities.user.init;

import com.eventify.api.entities.user.data.User;
import com.eventify.api.entities.user.services.UserService;
import com.eventify.api.handlers.exceptions.EntityAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class UserDBInitializer {

    @Autowired
    Environment env;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Bean
    CommandLineRunner initUserDatabase(UserService userService) {
        List<String> environments = Arrays.asList(env.getActiveProfiles());
        System.out.println("[DEBUG] Active Environments: " + environments);

        if (environments.contains("local")) {
            String[][] users = {
                    {adminEmail, adminPassword, "Admin"},
                    {"user1@test.de", "password123", "TestUser1"}
            };

            return args -> {
                if (userService == null) {
                    System.err.println("[DEBUG] UserService is null");
                    return;
                }

                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("INIT_USER_DATABASE", null));

                for (String[] user : users) {
                    try {
                        User newUser = userService.create(user[0], user[1], user[2]);
                        userService.verify(newUser.retrieveVerificationHash());
                        System.out.println("[DEBUG] Created user: " + newUser.getDisplayName());
                    } catch (EntityAlreadyExistsException | DataIntegrityViolationException e) {
                        System.out.println("[DEBUG] User already exists: " + user[0]);
                    }
                }

                jdbcTemplate.update("UPDATE users SET auth_role='ADMIN' WHERE email=?;", adminEmail);

                List<User> allUsers = userService.getAll();
                System.out.println("[DEBUG] All users: " + allUsers.stream().map(user -> "(" +
                        user.getId() + ", " +
                        user.getDisplayName() + ", " +
                        user.getEmail() + ")"
                ).collect(Collectors.toList()));
            };
        }

        return args -> {
            // environment is not local
        };
    }
}
