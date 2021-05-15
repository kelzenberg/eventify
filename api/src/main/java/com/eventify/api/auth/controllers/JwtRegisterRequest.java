package com.eventify.api.auth.controllers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JwtRegisterRequest implements Serializable {
    private String email;
    private String password;
    private String displayName;
}
