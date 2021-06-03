package com.eventify.api.entities.usereventrole.data;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class UserEventRoleId implements Serializable {

    @NonNull
    @Column(nullable = false)
    private UUID userId;

    @NonNull
    @Column(nullable = false)
    private UUID eventId;
}
