package com.eventify.api.entities.modules.expensesharing.entities.services;

import com.eventify.api.entities.modules.expensesharing.constants.ShareType;
import com.eventify.api.entities.modules.expensesharing.data.ExpenseSharingModule;
import com.eventify.api.entities.modules.expensesharing.entities.data.PaymentContribution;
import com.eventify.api.entities.modules.expensesharing.entities.data.PaymentContributionRepository;
import com.eventify.api.entities.modules.expensesharing.services.ExpenseSharingService;
import com.eventify.api.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentContributionService {

    @Autowired
    private PaymentContributionRepository paymentContributionRepository;

    @Autowired
    private ExpenseSharingService expenseSharingService;

    public List<PaymentContribution> getAll(UUID expenseSharingId) {
        ExpenseSharingModule expenseSharing = expenseSharingService.getById(expenseSharingId);

        if (expenseSharing == null) {
            throw new EntityNotFoundException("Expense Sharing Module with ID '" + expenseSharingId + "' cannot be found.");
        }

        return paymentContributionRepository.findAllByExpenseSharingModuleId(expenseSharingId);
    }

    public PaymentContribution getById(UUID id) {
        return paymentContributionRepository.getOne(id);
    }

    public PaymentContribution create(UUID expenseSharingId, String title, Double amount, ShareType shareType) throws EntityNotFoundException {
        ExpenseSharingModule expenseSharing = expenseSharingService.getById(expenseSharingId);

        if (expenseSharing == null) {
            throw new EntityNotFoundException("Expense Sharing Module with ID '" + expenseSharingId + "' cannot be found.");
        }

        PaymentContribution.PaymentContributionBuilder newEntity = PaymentContribution.builder()
                .title(title)
                .amount(amount)
                .expenseSharingModule(expenseSharing)
                .shareType(shareType);

        return paymentContributionRepository.save(newEntity.build());
    }

    public void deleteById(UUID id) {
        paymentContributionRepository.deleteById(id);
    }
}
