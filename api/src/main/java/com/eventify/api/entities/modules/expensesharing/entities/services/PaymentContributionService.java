package com.eventify.api.entities.modules.expensesharing.entities.services;

import com.eventify.api.entities.modules.expensesharing.constants.ShareType;
import com.eventify.api.entities.modules.expensesharing.data.ExpenseSharingModule;
import com.eventify.api.entities.modules.expensesharing.entities.controllers.RequestCostShare;
import com.eventify.api.entities.modules.expensesharing.entities.data.CostShare;
import com.eventify.api.entities.modules.expensesharing.entities.data.CostShareRepository;
import com.eventify.api.entities.modules.expensesharing.entities.data.PaymentContribution;
import com.eventify.api.entities.modules.expensesharing.entities.data.PaymentContributionRepository;
import com.eventify.api.entities.modules.expensesharing.services.ExpenseSharingService;
import com.eventify.api.entities.user.data.User;
import com.eventify.api.entities.user.services.UserService;
import com.eventify.api.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentContributionService {

    @Autowired
    private PaymentContributionRepository paymentContributionRepository;

    @Autowired
    private CostShareRepository costShareRepository;

    @Autowired
    private ExpenseSharingService expenseSharingService;

    @Autowired
    private UserService userService;

    public List<PaymentContribution> getAll(UUID expenseSharingId) {
        ExpenseSharingModule expenseSharing = expenseSharingService.getById(expenseSharingId);

        if (expenseSharing == null) {
            throw new EntityNotFoundException("Expense Sharing Module with ID '" + expenseSharingId + "' cannot be found.");
        }

        return paymentContributionRepository.findAllByExpenseModuleId(expenseSharingId);
    }

    public PaymentContribution getReferenceById(UUID id) {
        return paymentContributionRepository.getOne(id);
    }

    public PaymentContribution getById(UUID id) {
        return paymentContributionRepository.findById(id).orElse(null);
    }

    public PaymentContribution create(
            UUID expenseSharingId,
            String title,
            Double amount,
            UUID userId,
            ShareType shareType,
            List<RequestCostShare> shares
    ) throws EntityNotFoundException {
        ExpenseSharingModule expenseModule = expenseSharingService.getById(expenseSharingId);
        User payer = userService.getById(userId);


        if (expenseModule == null) {
            throw new EntityNotFoundException("Expense Sharing Module with ID '" + expenseSharingId + "' cannot be found.");
        }

        if (payer == null) {
            throw new EntityNotFoundException("User with ID '" + userId + "' cannot be found.");
        }

        PaymentContribution newPaymentContribution = PaymentContribution.builder()
                .title(title)
                .amount(amount)
                .payer(payer)
                .expenseModule(expenseModule)
                .shareType(shareType)
                .build();

        PaymentContribution createdPaymentContribution = paymentContributionRepository.save(newPaymentContribution);

        List<CostShare> costShares = new ArrayList<>();
        for (RequestCostShare costShare : shares) {
            User shareHolder = userService.getById(costShare.getUserId());

            CostShare newCostShare = CostShare.builder()
                    .amount(costShare.getAmount())
                    .shareHolder(shareHolder)
                    .paymentContribution(createdPaymentContribution)
                    .build();

            costShares.add(newCostShare);
        }

        costShareRepository.saveAll(costShares);

        return getById(createdPaymentContribution.getId());
    }

    public void deleteById(UUID id) {
        paymentContributionRepository.deleteById(id);
    }
}
