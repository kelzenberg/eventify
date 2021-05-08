package com.eventify.api.user.controllers;

import com.eventify.api.user.data.User;
import com.eventify.api.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    List<User> all() {
        return userService.getAllUsers();
    }
}
