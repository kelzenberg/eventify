package com.eventify.api.auth.controllers;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class JwtRequest implements Serializable {
    private String email;
    private String password;
}
