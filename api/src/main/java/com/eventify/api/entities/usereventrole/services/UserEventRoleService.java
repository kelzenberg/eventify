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

import javax.transaction.Transactional;
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

    public List<UserEventRole> getAllByUserId(UUID userId) {
        return repository.findAllByIdUserId(userId);
    }

    public List<UserEventRole> getAllByEventId(UUID eventId) {
        return repository.findAllByIdEventId(eventId);
    }

    public UserEventRole getByUserIdAndEventId(UUID userId, UUID eventId) {
        return repository
                .findByIdUserIdAndIdEventId(userId, eventId)
                .orElseThrow(() -> new EntityNotFoundException("UserEventRole with userId '" + userId + " and eventId " + eventId + "' cannot be found."));
    }

    public UserEventRole create(UUID userId, UUID eventId, EventRole role) throws EntityNotFoundException {
        User user = userService.getById(userId);
        Event event = eventService.getById(eventId);

        UserEventRole.UserEventRoleBuilder newEntity = UserEventRole.builder()
                .id(new UserEventRoleId(user.getId(), event.getId()))
                .user(user)
                .event(event)
                .role(role);

        return repository.save(newEntity.build());
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Transactional
    public void deleteByUserIdAndEventId(UUID userId, UUID eventId) {
        long deletedAmount = repository.deleteByIdUserIdAndIdEventId(userId, eventId);
        System.out.println("[DEBUG] Deleted " + deletedAmount + " UserEventRole (" + userId + ", " + eventId + ")");
    }
}
