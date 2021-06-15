package com.eventify.api.entities.modules.expensesharing.entities.controllers;

import com.eventify.api.constants.AuthenticatedPaths;
import com.eventify.api.entities.Views;
import com.eventify.api.entities.modules.expensesharing.constants.ShareType;
import com.eventify.api.entities.modules.expensesharing.entities.data.PaymentContribution;
import com.eventify.api.entities.modules.expensesharing.entities.services.PaymentContributionService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
public class PaymentContributionController {

    @Autowired
    private PaymentContributionService service;

    @GetMapping(AuthenticatedPaths.PAYMENT_CONTRIBUTION)
    @JsonView(Views.PublicExtended.class)
    List<PaymentContribution> getAll(@PathVariable UUID expenseSharingId) {
        return service.getAll(expenseSharingId);
    }

    @PostMapping(AuthenticatedPaths.PAYMENT_CONTRIBUTION)
    @JsonView(Views.PublicExtended.class)
    PaymentContribution create(@PathVariable UUID expenseSharingId, @Valid @RequestBody PaymentContributionCreateRequest body) {
        String title = body.getTitle();
        Double amount = body.getAmount();
        ShareType shareType = body.getShareType();
        UUID userId = body.getUserId();
        List<RequestCostShare> shares = body.getShares();

        return service.create(expenseSharingId, title, amount, userId, shareType, shares);
    }
}
