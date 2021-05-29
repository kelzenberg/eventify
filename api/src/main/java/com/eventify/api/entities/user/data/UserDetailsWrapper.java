package com.eventify.api.entities.user.data;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class UserDetailsWrapper implements UserDetails {
    String ROLE_PREFIX = "ROLE_";
    private User user;

    public UserDetailsWrapper(User user) {
        this.user = user;
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(ROLE_PREFIX + user.getRole()));
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
