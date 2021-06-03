package com.eventify.api.entities.usereventrole.data;

import com.eventify.api.constants.EventRole;
import com.eventify.api.entities.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "user_event_roles")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Embeddable
public class UserEventRole extends BaseEntity implements Serializable {

    @NonNull
    @Column(nullable = false)
    private UUID userId;

    @NonNull
    @Column(nullable = false)
    private UUID eventId;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventRole role;

    @Builder
    public UserEventRole(@NonNull UUID userId, @NonNull UUID eventId, @NonNull EventRole role) {
        super();
        this.userId = userId;
        this.eventId = eventId;
        this.role = role;
    }
}
