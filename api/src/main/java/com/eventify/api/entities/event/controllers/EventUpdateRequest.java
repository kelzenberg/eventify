package com.eventify.api.entities.event.controllers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventUpdateRequest implements Serializable {

    @Size(min = 3, max = 64)
    private String title;

    @Size(min = 1, max = 1000)
    private String description;

    @FutureOrPresent
    private Date startedAt;

    @FutureOrPresent
    private Date endedAt;
}
