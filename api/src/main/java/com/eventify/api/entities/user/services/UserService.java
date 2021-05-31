package com.eventify.api.entities.user.services;

import com.eventify.api.auth.exceptions.TokenIsInvalidException;
import com.eventify.api.auth.utils.JwtTokenUtil;
import com.eventify.api.entities.user.data.User;
import com.eventify.api.entities.user.data.UserRepository;
import com.eventify.api.exceptions.EntityAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(UUID id) {
        return userRepository.getOne(id);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User getMe(String authHeader) {
        String token = authHeader.split(" ")[1].trim();

        try {
            return userRepository
                    .findByEmail(jwtTokenUtil.getSubject(token))
                    .orElse(null);
        } catch (TokenIsInvalidException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token is invalid");
        }
    }

    public User create(String email, String password, String displayName) throws EntityAlreadyExistsException {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EntityAlreadyExistsException("User with email '" + email + "' already exists");
        }

        User newUser = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .displayName(displayName)
                .build();

        return userRepository.save(newUser);
    }

    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }
}
