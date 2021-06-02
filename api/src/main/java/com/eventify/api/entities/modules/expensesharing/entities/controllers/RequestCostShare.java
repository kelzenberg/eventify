package com.eventify.api.entities.modules.expensesharing.entities.controllers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.io.Serializable;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestCostShare implements Serializable {

    @NotNull
    @PositiveOrZero
    @Max(100000)
    private Double amount;

    @NotNull
    private UUID userId;
}
