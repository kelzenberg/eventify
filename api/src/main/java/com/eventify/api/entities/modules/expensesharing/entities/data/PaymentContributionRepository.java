package com.eventify.api.entities.modules.expensesharing.entities.data;

import com.eventify.api.entities.BaseRepository;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentContributionRepository extends BaseRepository<PaymentContribution> {
    List<PaymentContribution> findAllByExpenseModuleId(@NonNull UUID expenseSharingModule_id);
}
