package com.eventify.api;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class MockUserDetailsWrapper implements UserDetails {
    String ROLE_PREFIX = "ROLE_";
    String username;
    String password;
    String role;

    public MockUserDetailsWrapper(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(ROLE_PREFIX + role));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // temporarily until User class implements these attributes
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // temporarily until User class implements these attributes
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // temporarily until User class implements these attributes
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
