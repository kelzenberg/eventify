package com.eventify.api.entities.usereventrole.services;

import com.eventify.api.constants.EventRole;
import com.eventify.api.entities.usereventrole.data.UserEventRole;
import com.eventify.api.entities.usereventrole.data.UserEventRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserEventRoleService {

    @Autowired
    private UserEventRoleRepository repository;

    public List<UserEventRole> getAll() {
        return repository.findAll();
    }

    public UserEventRole getReferenceById(UUID id) {
        return repository.getOne(id);
    }

    public UserEventRole getById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public List<UserEventRole> getByUserId(UUID userId) {
        return repository.findAllByUserId(userId);
    }

    public List<UserEventRole> getByEventId(UUID userId) {
        return repository.findAllByEventId(userId);
    }

    public UserEventRole create(UUID userId, UUID eventId, EventRole role) {
        UserEventRole.UserEventRoleBuilder newEntity = UserEventRole.builder()
                .userId(userId)
                .eventId(eventId)
                .role(role);

        return repository.save(newEntity.build());
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
