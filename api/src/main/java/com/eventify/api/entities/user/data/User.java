package com.eventify.api.entities.user.data;

import com.eventify.api.constants.AuthorizationRole;
import com.eventify.api.constants.EventRole;
import com.eventify.api.entities.BaseEntity;
import com.eventify.api.entities.Views;
import com.eventify.api.entities.user.utils.VerificationUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

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
    @JsonView(Views.Me.class)
    @Column(unique = true, nullable = false)
    private String email;

    @NonNull
    @JsonIgnore
    @ToString.Exclude
    @Column(nullable = false)
    private String password;

    @NonNull
    @JsonView(Views.PublicExtended.class)
    @Column(nullable = false)
    private String displayName;

    @NonNull
    @JsonIgnore
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthorizationRole authRole = AuthorizationRole.USER;

    @JsonView(Views.PublicExtended.class)
    @Transient
    private EventRole eventRole;

    @JsonIgnore
    @ToString.Exclude
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date verifiedAt;

    @JsonIgnore
    @ToString.Exclude
    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PRIVATE)
    @NonNull
    @Column(name = "verification_uuid", updatable = false, nullable = false)
    private UUID verificationUUID = UUID.randomUUID();

    // convenience Getter for verificationUUID
    public String retrieveVerificationHash() {
        return VerificationUtil.UUIDtoHash(this.verificationUUID);
    }

    @Builder
    public User(@NonNull String email, @NonNull String password, @NonNull String displayName) {
        super();
        this.email = email;
        this.password = password;
        this.displayName = displayName;
    }
}
