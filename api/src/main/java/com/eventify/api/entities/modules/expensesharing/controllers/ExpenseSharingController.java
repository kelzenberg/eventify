package com.eventify.api.entities.modules.expensesharing.controllers;

import com.eventify.api.constants.AuthenticatedPaths;
import com.eventify.api.entities.Views;
import com.eventify.api.entities.modules.expensesharing.data.ExpenseSharingModule;
import com.eventify.api.entities.modules.expensesharing.services.ExpenseSharingService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
public class ExpenseSharingController {

    @Autowired
    private ExpenseSharingService service;

    @GetMapping(AuthenticatedPaths.EXPENSE_SHARING)
    @JsonView(Views.PublicExtended.class)
    List<ExpenseSharingModule> getAll() {
        return service.getAll();
    }

    @GetMapping(AuthenticatedPaths.EXPENSE_SHARING + "/{id}")
    @JsonView(Views.PublicExtended.class)
    ExpenseSharingModule getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping(AuthenticatedPaths.EXPENSE_SHARING)
    @JsonView(Views.PublicExtended.class)
    ExpenseSharingModule create(@Valid @RequestBody ExpenseSharingCreateRequest body) {
        String title = body.getTitle();
        String description = body.getDescription();
        UUID eventId = body.getEventId();

        return service.create(title, description, eventId);
    }
}
