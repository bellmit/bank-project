package com.epam.bank.operatorinterface.controller.advice;

import com.epam.bank.operatorinterface.domain.dto.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ErrorHandlingAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        ErrorResponse response = new ErrorResponse("badCredentials", HttpStatus.FORBIDDEN);
        return handleExceptionInternal(ex, response, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        ErrorResponse response = new ErrorResponse("emailNotFound", HttpStatus.FORBIDDEN);
        return handleExceptionInternal(ex, response, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }
}
