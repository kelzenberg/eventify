package com.eventify.api.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EntityIsInvalidException extends RuntimeException {
    public EntityIsInvalidException(String message) {
        super(message);
    }
}
