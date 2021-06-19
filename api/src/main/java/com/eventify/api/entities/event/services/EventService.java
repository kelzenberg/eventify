package com.eventify.api.entities.event.services;

import com.eventify.api.constants.EventRole;
import com.eventify.api.entities.event.data.Event;
import com.eventify.api.entities.event.data.EventRepository;
import com.eventify.api.entities.user.data.User;
import com.eventify.api.entities.user.services.UserService;
import com.eventify.api.entities.usereventrole.data.UserEventRole;
import com.eventify.api.entities.usereventrole.services.UserEventRoleService;
import com.eventify.api.exceptions.EntityIsInvalidException;
import com.eventify.api.exceptions.EntityNotFoundException;
import com.eventify.api.exceptions.PermissionsAreInsufficientException;
import com.eventify.api.mail.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
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

    @Autowired
    private MailService mailService;

    public List<Event> getAll() {
        return repository.findAll();
    }

    public Event getReferenceById(UUID id) {
        return repository.getOne(id);
    }

    public Event getById(UUID id) {
        return repository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event with ID '" + id + "' cannot be found."));
    }

    public List<Event> getAllByUserId(UUID userId) {
        List<UserEventRole> userEventRoles = userEventRoleService.getAllByUserId(userId);
        return userEventRoles.stream().map(UserEventRole::getEvent).collect(Collectors.toList());
    }

    public Event create(String title, String description, @Nullable Date startedAt) {
        Event.EventBuilder newEntity = Event.builder()
                .title(title)
                .description(description);

        if (startedAt != null) {
            newEntity.startedAt(startedAt);
        }

        return repository.save(newEntity.build());
    }

    public Event update(UUID id, @Nullable String title, @Nullable String description, @Nullable Date startedAt, @Nullable Date endedAt) {
        Event event = getReferenceById(id);

        if (title != null) {
            event.setTitle(title);
        }
        if (description != null) {
            event.setDescription(description);
        }
        if (startedAt != null) {
            event.setStartedAt(startedAt);
        }
        if (endedAt != null) {
            event.setEndedAt(endedAt);
        }

        return repository.save(event);
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    public Event join(UUID eventId, String email) throws EntityNotFoundException, MessagingException {
        Event event = getById(eventId);
        User user;

        try {
            user = userService.getByEmail(email);
        } catch (EntityNotFoundException e) {
            System.out.println("[DEBUG] User to join, with email " + email + ", was not found. Sending invite email...");
            mailService.sendInvite(email, event.getTitle());
            return null;
        }

        UserEventRole userEventRole;

        try {
            userEventRole = userEventRoleService.getByUserIdAndEventId(user.getId(), event.getId());
        } catch (EntityNotFoundException e) { // user & event exist on their own, but no relationship was found
            userEventRole = userEventRoleService.create(user.getId(), event.getId(), EventRole.ATTENDEE);
        }

        return userEventRole.getEvent();
    }

    public void leave(UUID userId, UUID eventId) throws EntityNotFoundException {

        // TODO: check if last ORGANISER is leaving -> delete Event(?)
        // TODO: check if 'deleteAll...' is possible
        userEventRoleService.deleteByUserIdAndEventId(userId, eventId);
    }

    public void bounce(UUID actorId, UUID userId, UUID eventId) throws EntityNotFoundException {
        if (actorId.equals(userId)) {
            throw new EntityIsInvalidException("The userId to be bounced cannot be the ID of the requesting user. Leave event instead.");
        }

        EventRole actorEventRole = userEventRoleService.getByUserIdAndEventId(actorId, eventId).getRole();

        if (actorEventRole != EventRole.ORGANISER) {
            throw new PermissionsAreInsufficientException("Actor has insufficient permissions to bounce user with ID " + userId);
        }

        // TODO: check if 'deleteAll...' is possible
        userEventRoleService.deleteByUserIdAndEventId(userId, eventId);
    }
}
