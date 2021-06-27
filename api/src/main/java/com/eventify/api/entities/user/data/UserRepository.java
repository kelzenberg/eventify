package com.eventify.api.entities.user.data;

import com.eventify.api.entities.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends BaseRepository<User> {
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationUUID(UUID verificationHash);
}
