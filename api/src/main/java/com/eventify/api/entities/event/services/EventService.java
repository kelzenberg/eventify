package com.eventify.api.entities.event.services;

import com.eventify.api.constants.EventRole;
import com.eventify.api.entities.event.data.Event;
import com.eventify.api.entities.event.data.EventRepository;
import com.eventify.api.entities.user.data.User;
import com.eventify.api.entities.user.services.UserService;
import com.eventify.api.entities.usereventrole.data.UserEventRole;
import com.eventify.api.entities.usereventrole.services.UserEventRoleService;
import com.eventify.api.exceptions.EntityNotFoundException;
import com.eventify.api.mail.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Event join(UUID eventId, String email) throws EntityNotFoundException, MessagingException {
        try {
            User user = userService.getByEmail(email);

            // TODO: check Role differences (otherwise produces two UER entries)
            UserEventRole userEventRole = userEventRoleService.create(user.getId(), eventId, EventRole.ATTENDEE);
            return userEventRole.getEvent();
        } catch (EntityNotFoundException e) {
            mailService.sendInvite(email, eventId);
            return null;
        }
    }

    public void leave(UUID userId, UUID eventId) throws EntityNotFoundException {

        // TODO: check if last ORGANISER is leaving -> delete Event(?)
        // TODO: check if 'deleteAll...' is possible

        userEventRoleService.deleteByUserIdAndEventId(userId, eventId);
    }

    public void bounce(UUID actorId, UUID userId, UUID eventId) throws EntityNotFoundException {

        // TODO: check if actorId is Organiser
        // TODO: check if 'deleteAll...' is possible

        userEventRoleService.deleteByUserIdAndEventId(userId, eventId);
    }
}
