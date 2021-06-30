package com.eventify.api.entities.usereventrole.controllers;

import com.eventify.api.constants.AdminPaths;
import com.eventify.api.constants.EventRole;
import com.eventify.api.entities.Views;
import com.eventify.api.entities.usereventrole.data.UserEventRole;
import com.eventify.api.entities.usereventrole.services.UserEventRoleService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
public class UserEventRoleController {

    @Autowired
    private UserEventRoleService service;

    @GetMapping(AdminPaths.EVENT_ROLES)
    @JsonView(Views.PublicExtended.class)
    List<UserEventRole> getAll() {
        return service.getAll();
    }

    @GetMapping(AdminPaths.EVENT_ROLES + "/user/{userId}")
    @JsonView(Views.PublicExtended.class)
    List<UserEventRole> getAllByUserId(@PathVariable UUID userId) {
        return service.getAllByUserId(userId);
    }

    @GetMapping(AdminPaths.EVENT_ROLES + "/event/{eventId}")
    @JsonView(Views.PublicExtended.class)
    List<UserEventRole> getAllByEventId(@PathVariable UUID eventId) {
        return service.getAllByEventId(eventId);
    }

    @PostMapping(AdminPaths.EVENT_ROLES)
    @ResponseStatus(HttpStatus.CREATED)
    @JsonView(Views.PublicExtended.class)
    UserEventRole create(@Valid @RequestBody UserEventRoleCreateRequest body) {
        UUID userId = body.getUserId();
        UUID eventId = body.getEventId();
        EventRole role = body.getRole();

        return service.create(userId, eventId, role);
    }
}
