package com.eventify.api.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class ErrorDetails {
    private Date timestamp;
    private int statusCode;
    private HttpStatus statusMessage;
    private String message;
}
