package com.eventify.api.entities.event.services;

import com.eventify.api.constants.EventRole;
import com.eventify.api.entities.event.data.Event;
import com.eventify.api.entities.event.data.EventRepository;
import com.eventify.api.entities.user.data.User;
import com.eventify.api.entities.user.services.UserService;
import com.eventify.api.entities.usereventrole.data.UserEventRole;
import com.eventify.api.entities.usereventrole.services.UserEventRoleService;
import com.eventify.api.exceptions.EntityNotFoundException;
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
    private UserService userService;

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

        return userEventRoles.stream().map(UserEventRole::getEvent).collect(Collectors.toList());
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

    public Event join(UUID eventId, String email) throws EntityNotFoundException {
        User user = userService.getByEmail(email);

        if (user == null) {
            throw new EntityNotFoundException("User with email '" + email + "' cannot be found."); // TODO: temporary, needs to trigger email sending
        }

        UserEventRole userEventRole = userEventRoleService.create(user.getId(), eventId, EventRole.ATTENDEE);
        return userEventRole.getEvent();
    }

    public void leave(UUID eventId, String email) throws EntityNotFoundException {
        User user = userService.getByEmail(email);

        if (user == null) {
            throw new EntityNotFoundException("User with email '" + email + "' cannot be found."); // TODO: temporary, needs to trigger email sending
        }

        userEventRoleService.deleteByUserIdAndEventId(user.getId(), eventId);
    }
}
