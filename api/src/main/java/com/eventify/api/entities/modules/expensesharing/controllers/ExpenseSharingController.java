package com.eventify.api.entities.modules.expensesharing.controllers;

import com.eventify.api.constants.AuthenticatedPaths;
import com.eventify.api.entities.modules.expensesharing.data.ExpenseSharingModule;
import com.eventify.api.entities.modules.expensesharing.services.ExpenseSharingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class ExpenseSharingController {

    @Autowired
    private ExpenseSharingService expenseSharingService;

    @GetMapping(AuthenticatedPaths.EXPENSE_SHARING)
    List<ExpenseSharingModule> getAll() {
        return expenseSharingService.getAll();
    }

    @PostMapping(AuthenticatedPaths.EXPENSE_SHARING)
    ExpenseSharingModule create(@Valid @RequestBody ExpenseSharingCreateRequest body) {
        String title = body.getTitle();
        String description = body.getDescription();

        return expenseSharingService.create(title, description);
    }
}
