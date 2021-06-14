package com.eventify.api.entities.event.controllers;

import com.eventify.api.constants.AuthenticatedPaths;
import com.eventify.api.constants.EventRole;
import com.eventify.api.entities.Views;
import com.eventify.api.entities.event.data.Event;
import com.eventify.api.entities.event.services.EventService;
import com.eventify.api.entities.user.services.UserService;
import com.eventify.api.entities.usereventrole.services.UserEventRoleService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
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
    @JsonView(Views.PublicShort.class)
    List<Event> getMyEvents(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        String token = authHeader.split(" ")[1].trim();
        UUID userId = userService.getByToken(token).getId();
        return eventService.getAllByUserId(userId);
    }

    @GetMapping(AuthenticatedPaths.EVENTS)
    @JsonView(Views.PublicExtended.class)
    List<Event> getAll() {
        return eventService.getAll();
    }

    @GetMapping(AuthenticatedPaths.EVENTS + "/{id}")
    @JsonView(Views.PublicExtended.class)
    Event getById(@PathVariable UUID id) {
        return eventService.getById(id);
    }

    @PostMapping(AuthenticatedPaths.EVENTS)
    @JsonView(Views.PublicExtended.class)
    Event create(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @Valid @RequestBody EventCreateRequest body) {
        String token = authHeader.split(" ")[1].trim();
        String title = body.getTitle();
        String description = body.getDescription();
        Date startedAt = body.getStartedAt();

        UUID userId = userService.getByToken(token).getId();

        Event event = eventService.create(title, description, startedAt);
        userEventRoleService.create(userId, event.getId(), EventRole.ORGANISER);

        event.setAmountOfUsers(1); // manual overwrite as attribute is transient
        return event;
    }

    @PostMapping(AuthenticatedPaths.EVENTS + "/{eventId}/join")
    @JsonView(Views.PublicExtended.class)
    Event joinById(@PathVariable UUID eventId, @Valid @RequestBody EventJoinRequest body) throws MessagingException {
        String email = body.getEmail().trim();
        return eventService.join(eventId, email);
    }

    @PostMapping(AuthenticatedPaths.EVENTS + "/{eventId}/leave")
    @JsonView(Views.PublicExtended.class)
    void leaveById(@PathVariable UUID eventId, @Valid @RequestBody EventJoinRequest body) {
        String email = body.getEmail().trim();
        eventService.leave(eventId, email);
    }
}
