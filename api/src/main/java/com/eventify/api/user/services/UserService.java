package com.eventify.api.user.services;

import com.eventify.api.user.data.User;
import com.eventify.api.user.data.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(String email, String password, String displayName) {
        User user = User.builder().email(email).password(password).displayName(displayName).build();
        return userRepository.save(user);
    }
}