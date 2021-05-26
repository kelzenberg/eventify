package com.eventify.api.entities.modules.expensesharing.services;

import com.eventify.api.entities.modules.expensesharing.data.ExpenseSharing;
import com.eventify.api.entities.modules.expensesharing.data.ExpenseSharingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ExpenseSharingService {

    @Autowired
    private ExpenseSharingRepository expenseSharingRepository;

    public List<ExpenseSharing> getAllExpenseSharingModules() {
        return expenseSharingRepository.findAll();
    }

    public ExpenseSharing getExpenseSharingModuleById(UUID id) {
        return expenseSharingRepository.findById(id).orElse(null);
    }

    public ExpenseSharing createExpenseSharingModule(String title, String description) {
        ExpenseSharing.ExpenseSharingBuilder newExpenseSharing = ExpenseSharing.builder()
                .title(title)
                .description(description);

        return expenseSharingRepository.save(newExpenseSharing.build());
    }

    public void deleteExpenseSharingModule(UUID id) {
        expenseSharingRepository.deleteById(id);
    }
}
