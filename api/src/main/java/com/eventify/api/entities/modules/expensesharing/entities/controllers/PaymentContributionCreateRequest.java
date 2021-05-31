package com.eventify.api.entities.modules.expensesharing.entities.controllers;

import com.eventify.api.entities.modules.expensesharing.constants.ShareType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentContributionCreateRequest implements Serializable {

    @NotNull
    @NotEmpty
    @Size(min = 3, max = 64)
    private String title;

    @NotNull
    @PositiveOrZero
    @Max(100000)
    private Double amount;

    @NotNull
    private ShareType shareType;
}
