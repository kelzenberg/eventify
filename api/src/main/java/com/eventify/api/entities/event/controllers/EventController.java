package com.eventify.api.entities.event.controllers;

import com.eventify.api.constants.AuthenticatedPaths;
import com.eventify.api.entities.event.data.Event;
import com.eventify.api.entities.event.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
public class EventController {

    @Autowired
    private EventService eventService;

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
