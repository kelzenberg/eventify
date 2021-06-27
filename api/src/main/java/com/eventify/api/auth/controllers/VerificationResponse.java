package com.eventify.api.auth.controllers;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Getter
@AllArgsConstructor
public class VerificationResponse implements Serializable {
    private Date verifiedAt;
}
