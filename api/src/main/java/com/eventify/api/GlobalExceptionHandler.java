package com.eventify.api;

import com.eventify.api.exceptions.*;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * DATA-LEVEL EXCEPTIONS
     */

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleEntityNotFoundException(EntityNotFoundException exception, HttpServletRequest request) {
        return handleResponseStatusException(new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage()), request);
    }

    @ExceptionHandler(value = EntityAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> handleEntityAlreadyExistsException(EntityAlreadyExistsException exception, HttpServletRequest request) {
        return handleResponseStatusException(new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage()), request);
    }

    @ExceptionHandler(value = MessagingException.class)
    public ResponseEntity<ErrorDetails> handleMessagingException(MessagingException exception, HttpServletRequest request) {
        return handleResponseStatusException(new ResponseStatusException(HttpStatus.FORBIDDEN, exception.getMessage()), request);
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDetails> handleDataIntegrityViolationException(DataIntegrityViolationException exception, HttpServletRequest request) {
        return handleResponseStatusException(new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage()), request);
    }

    /**
     * BUSINESS LOGIC AUTHORIZATION EXCEPTIONS
     */

    @ExceptionHandler(value = PermissionsAreInsufficientException.class)
    public ResponseEntity<ErrorDetails> handlePermissionsAreInsufficientException(PermissionsAreInsufficientException exception, HttpServletRequest request) {
        return handleResponseStatusException(new ResponseStatusException(HttpStatus.FORBIDDEN, exception.getMessage()), request);
    }

    /**
     * AUTHENTICATION EXCEPTIONS
     */

    @ExceptionHandler(value = TokenIsInvalidException.class)
    public ResponseEntity<ErrorDetails> handleTokenIsInvalidException(TokenIsInvalidException exception, HttpServletRequest request) {
        return handleResponseStatusException(new ResponseStatusException(HttpStatus.UNAUTHORIZED, exception.getMessage()), request);
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<ErrorDetails> handleBadCredentialsException(BadCredentialsException exception, HttpServletRequest request) {
        return handleResponseStatusException(new ResponseStatusException(HttpStatus.UNAUTHORIZED, exception.getMessage()), request);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ErrorDetails> handleAccessDeniedException(AccessDeniedException exception, HttpServletRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN,
                exception.getMessage()
        );
        return new ResponseEntity<>(errorDetails, errorDetails.getStatusMessage());
    }

    @ExceptionHandler(value = AuthenticationException.class)
    public ResponseEntity<ErrorDetails> handleAuthenticationException(AuthenticationException exception, HttpServletRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED,
                exception.getMessage()
        );
        return new ResponseEntity<>(errorDetails, errorDetails.getStatusMessage());
    }

    /**
     * GLOBAL EXCEPTIONS
     */

    @ExceptionHandler(value = ResponseStatusException.class)
    public ResponseEntity<ErrorDetails> handleResponseStatusException(ResponseStatusException exception, HttpServletRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                exception.getRawStatusCode(),
                exception.getStatus(),
                exception.getMessage()
        );
        return new ResponseEntity<>(errorDetails, errorDetails.getStatusMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception exception, HttpServletRequest request) throws Exception {
        if (AnnotationUtils.findAnnotation(exception.getClass(), ExceptionHandler.class) != null) {
            throw exception;
        }

        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage()
        );

        return new ResponseEntity<>(errorDetails, errorDetails.getStatusMessage());
    }
}
