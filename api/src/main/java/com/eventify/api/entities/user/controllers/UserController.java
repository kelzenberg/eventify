package com.eventify.api.entities.user.controllers;

import com.eventify.api.constants.AdminPaths;
import com.eventify.api.constants.AuthenticatedPaths;
import com.eventify.api.entities.Views;
import com.eventify.api.entities.user.data.User;
import com.eventify.api.entities.user.services.UserService;
import com.eventify.api.exceptions.TokenIsInvalidException;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping(AuthenticatedPaths.ME)
    @JsonView(Views.Me.class)
    User getMe(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        String token = authHeader.split(" ")[1].trim();

        try {
            return service.getByToken(token);
        } catch (TokenIsInvalidException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is invalid");
        }
    }

    @GetMapping(AdminPaths.USERS)
    @JsonView(Views.PublicExtended.class)
    List<User> getAll() {
        return service.getAll();
    }

}
