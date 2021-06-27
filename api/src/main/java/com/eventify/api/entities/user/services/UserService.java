package com.eventify.api.entities.user.services;

import com.eventify.api.auth.utils.JwtTokenUtil;
import com.eventify.api.entities.user.data.User;
import com.eventify.api.entities.user.data.UserRepository;
import com.eventify.api.entities.user.utils.VerificationUtil;
import com.eventify.api.handlers.exceptions.EntityAlreadyExistsException;
import com.eventify.api.handlers.exceptions.EntityNotFoundException;
import com.eventify.api.handlers.exceptions.TokenIsInvalidException;
import com.eventify.api.handlers.exceptions.VerificationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public List<User> getAll() {
        return repository.findAll();
    }

    public User getReferenceById(UUID id) {
        return repository.getOne(id);
    }

    public User getById(UUID id) throws TokenIsInvalidException {
        return repository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID '" + id + "' cannot be found."));
    }

    public User getByEmail(String email) throws TokenIsInvalidException {
        return repository
                .findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email '" + email + "' cannot be found."));
    }

    public User getByToken(String token) throws TokenIsInvalidException {
        return repository
                .findByEmail(jwtTokenUtil.getSubject(token))
                .orElseThrow(() -> new EntityNotFoundException("User with token '" + token + "' cannot be found."));
    }

    public User create(String email, String password, String displayName) throws EntityAlreadyExistsException {
        if (repository.findByEmail(email).isPresent()) {
            throw new EntityAlreadyExistsException("User with email '" + email + "' already exists");
        }

        User.UserBuilder newEntity = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .displayName(displayName);

        return repository.save(newEntity.build());
    }

    public Date verify(String verificationHash) throws EntityNotFoundException, VerificationFailedException {
        User user = repository
                .findByVerificationUUID(VerificationUtil.hashToUUID(verificationHash))
                .orElseThrow(() -> new EntityNotFoundException("Verification hash does not belong to any user."));

        if (user.getVerifiedAt() != null) {
            throw new VerificationFailedException("User is already verified.");
        }

        user.setVerifiedAt(new Date());
        repository.save(user);

        return user.getVerifiedAt();
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
