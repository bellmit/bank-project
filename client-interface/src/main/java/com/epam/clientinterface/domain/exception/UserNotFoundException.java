package com.epam.clientinterface.domain.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(long userId) {
        super(String.format("User of id=%d not found", userId));
    }
}
