package com.eventify.api.entities.event.controllers;

import com.eventify.api.auth.exceptions.TokenIsInvalidException;
import com.eventify.api.constants.AuthenticatedPaths;
import com.eventify.api.entities.Views;
import com.eventify.api.entities.event.data.Event;
import com.eventify.api.entities.event.services.EventService;
import com.eventify.api.entities.user.services.UserService;
import com.eventify.api.entities.usereventrole.services.UserEventRoleService;
import com.eventify.api.exceptions.EntityNotFoundException;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserEventRoleService userEventRoleService;

    @GetMapping(AuthenticatedPaths.MY_EVENTS)
    @JsonView(Views.Short.class)
    List<Event> getMyEvents(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        String token = authHeader.split(" ")[1].trim();

        try {
            UUID userId = userService.getByToken(token).getId();
            return eventService.getAllByUserId(userId);
        } catch (TokenIsInvalidException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token is invalid");
        }
    }

    @GetMapping(AuthenticatedPaths.EVENTS)
    List<Event> getAll() {
        return eventService.getAll();
    }

    @GetMapping(AuthenticatedPaths.EVENTS + "/{id}")
    Event getById(@PathVariable UUID id) {
        return eventService.getById(id);
    }

    @PostMapping(AuthenticatedPaths.EVENTS)
    Event create(@Valid @RequestBody EventCreateRequest body) {
        String title = body.getTitle();
        String description = body.getDescription();
        Date startedAt = body.getStartedAt();

        return eventService.create(title, description, startedAt);
    }

    @JsonView(Views.Short.class)
    @PostMapping(AuthenticatedPaths.EVENTS + "/{eventId}/join")
    Event joinById(@PathVariable UUID eventId, @Valid @RequestBody EventJoinRequest body) {
        String email = body.getEmail().trim();

        try {
            return eventService.join(eventId, email);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


    @PostMapping(AuthenticatedPaths.EVENTS + "/{eventId}/leave")
    void leaveById(@PathVariable UUID eventId, @Valid @RequestBody EventJoinRequest body) {
        String email = body.getEmail().trim();

        try {
            eventService.leave(eventId, email);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
