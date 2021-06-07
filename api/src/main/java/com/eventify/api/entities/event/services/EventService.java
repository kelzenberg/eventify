package com.eventify.api.entities.event.services;

import com.eventify.api.entities.event.data.Event;
import com.eventify.api.entities.event.data.EventRepository;
import com.eventify.api.entities.usereventrole.data.UserEventRole;
import com.eventify.api.entities.usereventrole.services.UserEventRoleService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository repository;

    @Autowired
    private UserEventRoleService userEventRoleService;

    public List<Event> getAll() {
        return repository.findAll();
    }

    public Event getReferenceById(UUID id) {
        return repository.getOne(id);
    }

    public Event getById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public List<Event> getAllByUserId(UUID userId) {
        List<UserEventRole> userEventRoles = userEventRoleService.getByUserId(userId);

        return userEventRoles.stream().map(userEventRole -> {
            @NonNull Event event = userEventRole.getEvent();
            event.setAmountOfUsers(userEventRoleService.countUserByEventId(event.getId())); // workaround for @Formula on Event.amountOfUsers
            return event;
        }).collect(Collectors.toList());
    }

    public Event create(String title, String description, Date startedAt) {
        Event.EventBuilder newEntity = Event.builder()
                .title(title)
                .description(description);

        if (startedAt != null) {
            newEntity.startedAt(startedAt);
        }

        return repository.save(newEntity.build());
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
