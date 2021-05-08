package com.eventify.api.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.client.HttpClientErrorException;

public interface UserDetailsService {
    UserDetails loadUserByEmail(String email) throws HttpClientErrorException.NotFound;
}
