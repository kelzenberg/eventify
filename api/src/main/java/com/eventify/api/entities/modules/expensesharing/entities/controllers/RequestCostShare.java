package com.eventify.api.entities.modules.expensesharing.entities.controllers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestCostShare implements Serializable {

    @NotNull
    private UUID userId;

    @NotNull
    @PositiveOrZero
    @Max(100000)
    private double amount;
}
