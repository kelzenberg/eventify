package com.eventify.api.entities.user.controllers;

import com.eventify.api.constants.AuthenticatedPaths;
import com.eventify.api.entities.user.data.User;
import com.eventify.api.entities.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(AuthenticatedPaths.USERS)
    List<User> all() {
        return userService.getAllUsers();
    }

    @GetMapping(AuthenticatedPaths.USERS + "/me")
    User me(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        return userService.getMe(authHeader);
    }
}
