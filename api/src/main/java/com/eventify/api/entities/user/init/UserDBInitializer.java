package com.eventify.api.entities.user.init;

import com.eventify.api.entities.user.data.User;
import com.eventify.api.entities.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class UserDBInitializer {

    @Autowired
    Environment env;

    @Bean
    CommandLineRunner initUserDatabase(UserService userService) {
        List<String> environments = Arrays.asList(env.getActiveProfiles());
        System.out.println("[DEBUG] Active Environments: " + environments);

        if (environments.contains("local")) {
            String[][] users = {
                    {"admin@test.de", "password123", "Admin"},
                    {"user1@test.de", "password123", "TestUser1"}
            };

            return args -> {
                if (userService == null) {
                    System.err.println("[DEBUG] UserService is null");
                    return;
                }

                for (String[] user : users) {
                    try {
                        User newUser = userService.createUser(user[0], user[1], user[2]);
                        System.out.println("[DEBUG] Created user: " + newUser.getDisplayName());
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }

                List<User> allUsers = userService.getAllUsers();
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
