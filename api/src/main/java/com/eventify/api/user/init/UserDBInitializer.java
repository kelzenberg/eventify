package com.eventify.api.user.init;

import com.eventify.api.user.data.User;
import com.eventify.api.user.services.UserService;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class UserDBInitializer {

    @Bean
    CommandLineRunner initUserDatabase(UserService userService) {
        String[][] users = {
                {"admin@test.de", "password123", "Admin"},
                {"test@test.de", "password123", "TestUser1"}
        };

        return args -> {
            if (userService == null) {
                System.err.println("[DEBUG] UserService is null");
                return;
            }

            for (String[] user : users) {
                try {
                    User newUser = userService.createUser(user[0], user[1], user[2]);
                    System.out.println("[DEBUG] Created Users: " + newUser);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }

            List<User> allUsers = userService.getAllUsers();
            System.out.println("[DEBUG] All users: " + allUsers);
        };
    }
}
