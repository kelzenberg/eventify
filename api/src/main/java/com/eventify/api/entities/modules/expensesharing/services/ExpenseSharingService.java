package com.eventify.api.entities.modules.expensesharing.services;

import com.eventify.api.entities.event.data.Event;
import com.eventify.api.entities.event.services.EventService;
import com.eventify.api.entities.modules.expensesharing.data.ExpenseSharingModule;
import com.eventify.api.entities.modules.expensesharing.data.ExpenseSharingRepository;
import com.eventify.api.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ExpenseSharingService {

    @Autowired
    private ExpenseSharingRepository repository;

    @Autowired
    private EventService eventService;

    public List<ExpenseSharingModule> getAll() {
        return repository.findAll();
    }

    public ExpenseSharingModule getReferenceById(UUID id) {
        return repository.getOne(id);
    }

    public ExpenseSharingModule getById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public ExpenseSharingModule create(String title, String description, UUID eventId) {
        Event event = eventService.getById(eventId);

        if (event == null ) {
            throw new EntityNotFoundException("Event with ID '" + eventId + "' cannot be found.");
        }

        ExpenseSharingModule.ExpenseSharingModuleBuilder newEntity = ExpenseSharingModule.builder()
                .title(title)
                .description(description)
                .event(event);

        return repository.save(newEntity.build());
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
