package com.eventify.api.entities.modules.expensesharing.entities.controllers;

import com.eventify.api.constants.AuthenticatedPaths;
import com.eventify.api.entities.modules.expensesharing.constants.ShareType;
import com.eventify.api.entities.modules.expensesharing.entities.data.PaymentContribution;
import com.eventify.api.entities.modules.expensesharing.entities.services.PaymentContributionService;
import com.eventify.api.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
public class PaymentContributionController {

    @Autowired
    private PaymentContributionService service;

    @GetMapping(AuthenticatedPaths.PAYMENT_CONTRIBUTION)
    List<PaymentContribution> getAll(@PathVariable UUID expenseSharingId) {
        return service.getAll(expenseSharingId);
    }

    @PostMapping(AuthenticatedPaths.PAYMENT_CONTRIBUTION)
    PaymentContribution create(@PathVariable UUID expenseSharingId, @Valid @RequestBody PaymentContributionCreateRequest body) {
        String title = body.getTitle();
        Double amount = body.getAmount();
        ShareType shareType = body.getShareType();
        UUID userId = body.getUserId();
        List<RequestCostShare> shares = body.getShares();

        try {
            return service.create(expenseSharingId, title, amount, userId, shareType, shares);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
