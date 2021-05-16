package com.eventify.api.entities.user.data;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class UserDetailsWrapper extends User implements UserDetails {

    public UserDetailsWrapper(User user) {
        super(user);
    }

    @Override
    public String getUsername() {
        return super.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
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
        return true; // temporarily until User class implements these attributes
    }
}
