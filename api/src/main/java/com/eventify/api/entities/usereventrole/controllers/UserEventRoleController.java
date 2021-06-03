package com.eventify.api.entities.usereventrole.controllers;

import com.eventify.api.constants.AdminPaths;
import com.eventify.api.constants.EventRole;
import com.eventify.api.entities.usereventrole.data.UserEventRole;
import com.eventify.api.entities.usereventrole.services.UserEventRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
public class UserEventRoleController {

    @Autowired
    private UserEventRoleService service;

    @GetMapping(AdminPaths.EVENT_ROLES)
    List<UserEventRole> getAll() {
        return service.getAll();
    }

    @PostMapping(AdminPaths.EVENT_ROLES)
    UserEventRole create(@Valid @RequestBody UserEventRoleCreateRequest body) {
        UUID userId = body.getUserId();
        UUID eventId = body.getEventId();
        EventRole role = body.getRole();

        return service.create(userId, eventId, role);
    }
}
