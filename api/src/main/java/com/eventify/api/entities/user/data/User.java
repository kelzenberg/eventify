package com.eventify.api.entities.user.data;

import com.eventify.api.constants.AuthorizationRole;
import com.eventify.api.constants.EventRole;
import com.eventify.api.entities.BaseEntity;
import com.eventify.api.entities.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.persistence.*;
import java.nio.ByteBuffer;
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
    @Setter(AccessLevel.PRIVATE)
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date verifiedAt;

    @JsonIgnore
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    @NonNull
    @Column(updatable = false, nullable = false)
    private UUID verificationHash = UUID.randomUUID();

    public String getVerificationHash() {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(this.verificationHash.getMostSignificantBits());
        buffer.putLong(this.verificationHash.getLeastSignificantBits());
        return Base64.encodeBase64URLSafeString(buffer.array());
    }

    @Builder
    public User(@NonNull String email, @NonNull String password, @NonNull String displayName) {
        super();
        this.email = email;
        this.password = password;
        this.displayName = displayName;
    }

    public boolean verificationHashIsValid(String verificationHash) {
        byte[] bytes = Base64.decodeBase64(verificationHash);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return this.verificationHash.equals(new UUID(buffer.getLong(), buffer.getLong()));
    }
}
