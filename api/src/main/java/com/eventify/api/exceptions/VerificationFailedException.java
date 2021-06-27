package com.eventify.api.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class VerificationFailedException extends RuntimeException {
    public VerificationFailedException(String message) {
        super(message);
    }
}
