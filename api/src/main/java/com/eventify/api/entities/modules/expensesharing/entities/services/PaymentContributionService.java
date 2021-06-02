package com.eventify.api.entities.modules.expensesharing.entities.services;

import com.eventify.api.entities.modules.expensesharing.constants.ShareType;
import com.eventify.api.entities.modules.expensesharing.data.ExpenseSharingModule;
import com.eventify.api.entities.modules.expensesharing.entities.data.PaymentContribution;
import com.eventify.api.entities.modules.expensesharing.entities.data.PaymentContributionRepository;
import com.eventify.api.entities.modules.expensesharing.services.ExpenseSharingService;
import com.eventify.api.entities.user.data.User;
import com.eventify.api.entities.user.services.UserService;
import com.eventify.api.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentContributionService {

    @Autowired
    private PaymentContributionRepository repository;

    @Autowired
    private ExpenseSharingService expenseSharingService;

    @Autowired
    private UserService userService;

    public List<PaymentContribution> getAll(UUID expenseSharingId) {
        ExpenseSharingModule expenseSharing = expenseSharingService.getById(expenseSharingId);

        if (expenseSharing == null) {
            throw new EntityNotFoundException("Expense Sharing Module with ID '" + expenseSharingId + "' cannot be found.");
        }

        return repository.findAllByExpenseModuleId(expenseSharingId);
    }

    public PaymentContribution getReferenceById(UUID id) {
        return repository.getOne(id);
    }

    public PaymentContribution getById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public PaymentContribution create(UUID expenseSharingId, String title, Double amount, UUID userId, ShareType shareType) throws EntityNotFoundException {
        ExpenseSharingModule expenseModuleRef = expenseSharingService.getReferenceById(expenseSharingId);
        User userRef = userService.getReferenceById(userId);

        if (expenseModuleRef == null ) {
            throw new EntityNotFoundException("Expense Sharing Module with ID '" + expenseSharingId + "' cannot be found.");
        }

        if (userRef == null ) {
            throw new EntityNotFoundException("User with ID '" + userId + "' cannot be found.");
        }

        PaymentContribution.PaymentContributionBuilder newEntity = PaymentContribution.builder()
                .title(title)
                .amount(amount)
                .payer(userRef)
                .expenseModule(expenseModuleRef)
                .shareType(shareType);

        return repository.save(newEntity.build());
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
