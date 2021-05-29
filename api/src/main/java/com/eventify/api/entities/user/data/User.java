package com.eventify.api.entities.user.data;

import com.eventify.api.constants.AuthorizationRole;
import com.eventify.api.entities.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
// Users is reserved keyword. Fix by manually escaping: https://stackoverflow.com/a/50222377
@Table(name = "\"users\"")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class User extends BaseEntity {

    @NonNull
    @Column(unique = true, nullable = false)
    private String email;

    @NonNull
    @JsonIgnore
    @ToString.Exclude
    @Column(nullable = false)
    private String password;

    @NonNull
    @Column(nullable = false)
    private String displayName;

    @Setter(AccessLevel.PRIVATE)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    AuthorizationRole role;

    @Builder
    public User(@NonNull String email, @NonNull String password, @NonNull String displayName) {
        super();
        this.email = email;
        this.password = password;
        this.displayName = displayName;
        this.role = AuthorizationRole.USER;
    }
}
