package com.eventify.api.handlers.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class VerificationFailedException extends RuntimeException {
    public VerificationFailedException(String message) {
        super(message);
    }
}
