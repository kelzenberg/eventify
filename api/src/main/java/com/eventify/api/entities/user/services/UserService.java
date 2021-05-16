package com.eventify.api.entities.user.services;

import com.eventify.api.entities.user.data.User;
import com.eventify.api.entities.user.data.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail("" + email).orElse(null);
    }

    public User createUser(String email, String password, String displayName) {
        User user = User.builder()
                .email("" + email)
                .password(passwordEncoder.encode(password))
                .displayName("" + displayName)
                .build();
        return userRepository.save(user);
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }
}
