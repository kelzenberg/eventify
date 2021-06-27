package com.eventify.api.handlers.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PermissionsAreInsufficientException extends RuntimeException {
    public PermissionsAreInsufficientException(String message) {
        super();
    }
}
