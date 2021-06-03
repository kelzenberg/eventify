package com.eventify.api.entities.usereventrole.data;

import com.eventify.api.entities.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserEventRoleRepository extends BaseRepository<UserEventRole> {
    List<UserEventRole> findAllByUserId(UUID userId);
    List<UserEventRole> findAllByEventId(UUID eventId);
}
