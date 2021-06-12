package com.eventify.api.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TokenIsInvalidException extends RuntimeException {
    public TokenIsInvalidException(String message) {
        super("Token is invalid. " + message);
    }
}
