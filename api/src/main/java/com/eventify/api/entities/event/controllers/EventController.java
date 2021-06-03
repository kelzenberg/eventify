package com.eventify.api.entities.event.controllers;

import com.eventify.api.constants.AuthenticatedPaths;
import com.eventify.api.entities.event.data.Event;
import com.eventify.api.entities.event.services.EventService;
import com.eventify.api.entities.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping(AuthenticatedPaths.MY_EVENTS)
    List<Event> getMe(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        UUID userId = userService.getMe(authHeader).getId();
        return eventService.getMyEvents(userId);
    }

    @GetMapping(AuthenticatedPaths.EVENTS)
    List<Event> getAll() {
        return eventService.getAll();
    }

    @PostMapping(AuthenticatedPaths.EVENTS)
    Event create(@Valid @RequestBody EventCreateRequest body) {
        String title = body.getTitle();
        String description = body.getDescription();
        Date startedAt = body.getStartedAt();

        return eventService.create(title, description, startedAt);
    }
}
