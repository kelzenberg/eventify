package com.eventify.api.entities.event.controllers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventJoinRequest implements Serializable {

    @NotNull
    @NotEmpty
    @Size(min = 3, max = 64)
    @Email
    private String email;
}
