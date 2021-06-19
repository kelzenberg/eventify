package com.eventify.api.entities.modules.expensesharing.entities.services;

import com.eventify.api.entities.modules.expensesharing.constants.ShareType;
import com.eventify.api.entities.modules.expensesharing.data.ExpenseSharingModule;
import com.eventify.api.entities.modules.expensesharing.entities.controllers.RequestCostShare;
import com.eventify.api.entities.modules.expensesharing.entities.data.CostShare;
import com.eventify.api.entities.modules.expensesharing.entities.data.CostShareRepository;
import com.eventify.api.entities.modules.expensesharing.entities.data.PaymentContribution;
import com.eventify.api.entities.modules.expensesharing.entities.data.PaymentContributionRepository;
import com.eventify.api.entities.modules.expensesharing.services.ExpenseSharingService;
import com.eventify.api.entities.modules.expensesharing.utils.ExpenseSharingUtil;
import com.eventify.api.entities.user.data.User;
import com.eventify.api.entities.user.services.UserService;
import com.eventify.api.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Autowired
    private ExpenseSharingUtil expenseSharingUtil;

    public List<PaymentContribution> getAll(UUID expenseSharingId) {
        return paymentContributionRepository.findAllByExpenseModuleId(expenseSharingId);
    }

    public PaymentContribution getReferenceById(UUID id) {
        return paymentContributionRepository.getOne(id);
    }

    public PaymentContribution getById(UUID id) {
        return paymentContributionRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment contribution with ID '" + id + "' cannot be found."));
    }

    public PaymentContribution create(
            UUID expenseSharingId,
            String title,
            Double amount,
            UUID userId,
            ShareType shareType,
            List<RequestCostShare> shares
    ) throws EntityNotFoundException {
        User payer = userService.getById(userId);
        ExpenseSharingModule expenseModule = expenseSharingService.getById(expenseSharingId);
        List<RequestCostShare> validatedShares = expenseSharingUtil.validateShares(shareType, amount, shares);

        PaymentContribution.PaymentContributionBuilder newPayment = PaymentContribution.builder()
                .title(title)
                .amount(amount)
                .payer(payer)
                .expenseModule(expenseModule)
                .shareType(shareType);
        PaymentContribution createdPayment = paymentContributionRepository.save(newPayment.build());

        List<CostShare> createdCostShares = validatedShares.stream().map(costShare -> {
            User shareHolder = userService.getById(costShare.getUserId());
            return CostShare.builder()
                    .amount(costShare.getAmount())
                    .shareHolder(shareHolder)
                    .paymentContribution(createdPayment)
                    .build();
        }).collect(Collectors.toList());

        List<CostShare> createdShares = costShareRepository.saveAll(createdCostShares);
        createdPayment.setShares(createdShares); // manual overwrite to prevent another DB call

        return createdPayment;
    }

    public void deleteById(UUID id) {
        paymentContributionRepository.deleteById(id);
    }
}
