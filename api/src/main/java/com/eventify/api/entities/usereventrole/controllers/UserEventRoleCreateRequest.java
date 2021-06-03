package com.eventify.api.entities.usereventrole.controllers;

import com.eventify.api.constants.EventRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserEventRoleCreateRequest implements Serializable {

    @NotNull
    private UUID userId;

    @NotNull
    private UUID eventId;

    @NotNull
    private EventRole role;
}
