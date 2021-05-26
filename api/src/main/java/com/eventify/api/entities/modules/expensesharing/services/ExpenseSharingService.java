package com.eventify.api.entities.modules.expensesharing.services;

import com.eventify.api.entities.modules.expensesharing.data.ExpenseSharingModule;
import com.eventify.api.entities.modules.expensesharing.data.ExpenseSharingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ExpenseSharingService {

    @Autowired
    private ExpenseSharingRepository expenseSharingRepository;

    public List<ExpenseSharingModule> getAll() {
        return expenseSharingRepository.findAll();
    }

    public ExpenseSharingModule getById(UUID id) {
        return expenseSharingRepository.findById(id).orElse(null);
    }

    public ExpenseSharingModule create(String title, String description) {
        ExpenseSharingModule.ExpenseSharingModuleBuilder newExpenseSharing = ExpenseSharingModule.builder()
                .title(title)
                .description(description);

        return expenseSharingRepository.save(newExpenseSharing.build());
    }

    public void deleteById(UUID id) {
        expenseSharingRepository.deleteById(id);
    }
}
