package com.eventify.api.entities.user.services;

import com.eventify.api.auth.utils.JwtTokenUtil;
import com.eventify.api.entities.user.data.User;
import com.eventify.api.entities.user.data.UserRepository;
import com.eventify.api.exceptions.EntityAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public User getById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public User getByEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }

    public User getByToken(String token) {
        return repository.findByEmail(jwtTokenUtil.getSubject(token)).orElse(null);
    }

    public User create(String email, String password, String displayName) throws EntityAlreadyExistsException {
        if (getByEmail(email) != null) {
            throw new EntityAlreadyExistsException("User with email '" + email + "' already exists");
        }

        User.UserBuilder newEntity = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .displayName(displayName);

        return repository.save(newEntity.build());
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
