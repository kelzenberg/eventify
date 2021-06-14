package com.eventify.api.entities.usereventrole.data;

import com.eventify.api.entities.Views;
import com.fasterxml.jackson.annotation.JsonView;
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
    @JsonView(Views.Meta.class)
    @Column(nullable = false)
    private UUID userId;

    @NonNull
    @JsonView(Views.Meta.class)
    @Column(nullable = false)
    private UUID eventId;
}
