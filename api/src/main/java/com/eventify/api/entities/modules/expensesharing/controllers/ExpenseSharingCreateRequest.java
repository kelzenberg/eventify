package com.eventify.api.entities.modules.expensesharing.controllers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseSharingCreateRequest implements Serializable {

    @NotNull
    @NotEmpty
    @Size(min = 3, max = 64)
    private String title;

    @NotNull
    @NotEmpty
    @Size(min = 1, max = 1000)
    private String description;
}
