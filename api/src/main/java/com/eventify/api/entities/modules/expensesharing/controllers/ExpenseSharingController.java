package com.eventify.api.entities.modules.expensesharing.controllers;

import com.eventify.api.constants.AuthenticatedPaths;
import com.eventify.api.entities.modules.expensesharing.data.ExpenseSharingModule;
import com.eventify.api.entities.modules.expensesharing.services.ExpenseSharingService;
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
    List<ExpenseSharingModule> getAll() {
        return service.getAll();
    }

    @GetMapping(AuthenticatedPaths.EXPENSE_SHARING + "/{id}")
    ExpenseSharingModule getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping(AuthenticatedPaths.EXPENSE_SHARING)
    ExpenseSharingModule create(@Valid @RequestBody ExpenseSharingCreateRequest body) {
        String title = body.getTitle();
        String description = body.getDescription();
        UUID eventId = body.getEventId();

        return service.create(title, description, eventId);
    }
}
