package com.eventify.api.auth.controllers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@AllArgsConstructor
public class JwtResponse implements Serializable {
    private String token;
}
