package com.eventify.api.user.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findById(UUID uuid);

    Optional<User> findByEmail(String email);

    List<User> findAll();
}
