package com.eventify.api.auth.controllers;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class JwtRegisterRequest implements Serializable {
    private String email;
    private String password;
    private String displayName;
}
