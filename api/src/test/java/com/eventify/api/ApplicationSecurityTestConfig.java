package com.eventify.api;

import com.eventify.api.entities.user.data.User;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.List;

@TestConfiguration
public class ApplicationSecurityTestConfig {

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        User user = User.builder().email("email@test.de").password("password").displayName("name").build();
        UserDetails userDetails = new MockUserDetailsWrapper(user.getEmail(), user.getPassword(), "ADMIN");
        return new InMemoryUserDetailsManager(List.of(userDetails));
    }
}
