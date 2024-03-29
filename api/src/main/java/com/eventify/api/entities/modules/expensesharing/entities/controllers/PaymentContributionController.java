package com.eventify.api.entities.modules.expensesharing.entities.controllers;

import com.eventify.api.constants.AuthenticatedPaths;
import com.eventify.api.entities.Views;
import com.eventify.api.entities.modules.expensesharing.constants.ShareType;
import com.eventify.api.entities.modules.expensesharing.entities.data.PaymentContribution;
import com.eventify.api.entities.modules.expensesharing.entities.services.PaymentContributionService;
import com.eventify.api.entities.user.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
public class PaymentContributionController {

    @Autowired
    private PaymentContributionService paymentContributionService;

    @Autowired
    private UserService userService;

    @GetMapping(AuthenticatedPaths.PAYMENT_CONTRIBUTION)
    @JsonView(Views.PublicExtended.class)
    List<PaymentContribution> getAll(@PathVariable UUID expenseSharingId) {
        return paymentContributionService.getAll(expenseSharingId);
    }

    @PostMapping(AuthenticatedPaths.PAYMENT_CONTRIBUTION)
    @ResponseStatus(HttpStatus.CREATED)
    @JsonView(Views.PublicExtended.class)
    PaymentContribution create(@PathVariable UUID expenseSharingId, @Valid @RequestBody PaymentContributionCreateRequest body) {
        String title = body.getTitle();
        Double amount = body.getAmount();
        UUID payerId = body.getUserId();
        ShareType shareType = body.getShareType();
        List<RequestCostShare> shares = body.getShares();
        return paymentContributionService.create(expenseSharingId, title, amount, payerId, shareType, shares);
    }

    @DeleteMapping(AuthenticatedPaths.PAYMENT_CONTRIBUTION + "/{paymentContributionId}")
    @JsonView(Views.PublicExtended.class)
    void delete(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @PathVariable UUID paymentContributionId) {
        String token = authHeader.split(" ")[1].trim();
        UUID actorId = userService.getByToken(token).getId();
        paymentContributionService.deleteById(paymentContributionId, actorId);
    }
}
