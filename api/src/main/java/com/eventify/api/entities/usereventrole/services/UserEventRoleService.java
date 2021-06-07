package com.eventify.api.entities.usereventrole.services;

import com.eventify.api.constants.EventRole;
import com.eventify.api.entities.event.data.Event;
import com.eventify.api.entities.event.services.EventService;
import com.eventify.api.entities.user.data.User;
import com.eventify.api.entities.user.services.UserService;
import com.eventify.api.entities.usereventrole.data.UserEventRole;
import com.eventify.api.entities.usereventrole.data.UserEventRoleId;
import com.eventify.api.entities.usereventrole.data.UserEventRoleRepository;
import com.eventify.api.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserEventRoleService {

    @Autowired
    private UserEventRoleRepository repository;

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    public List<UserEventRole> getAll() {
        return repository.findAll();
    }

    public List<UserEventRole> getByUserId(UUID userId) {
        return repository.findAllByIdUserId(userId);
    }

    public List<UserEventRole> getByEventId(UUID eventId) {
        return repository.findAllByIdEventId(eventId);
    }

    public int countUserByEventId(UUID eventId) {
        return repository.countAllByIdEventId(eventId);
    }

    public UserEventRole create(UUID userId, UUID eventId, EventRole role) throws EntityNotFoundException {
        User user = userService.getById(userId);
        Event event = eventService.getById(eventId);

        if (user == null ) {
            throw new EntityNotFoundException("User with ID '" + userId + "' cannot be found.");
        }

        if (event == null ) {
            throw new EntityNotFoundException("Event with ID '" + eventId + "' cannot be found.");
        }

        UserEventRole.UserEventRoleBuilder newEntity = UserEventRole.builder()
                .id(new UserEventRoleId(user.getId(),event.getId()))
                .user(user)
                .event(event)
                .role(role);

        return repository.save(newEntity.build());
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
