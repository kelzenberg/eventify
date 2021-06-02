package com.eventify.api.entities.modules.expensesharing.entities.controllers;

import com.eventify.api.entities.modules.expensesharing.constants.ShareType;
import com.eventify.api.entities.modules.expensesharing.entities.data.CostShare;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

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
    private UUID userId;

    @NotNull
    private ShareType shareType;

    @NotNull
    private List<CostShare> shares;
}
