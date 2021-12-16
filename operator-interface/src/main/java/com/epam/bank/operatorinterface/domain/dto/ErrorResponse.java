package com.epam.bank.operatorinterface.domain.dto;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {
    private final String type;
    private final int status;

    public ErrorResponse(@NonNull String type, @NonNull HttpStatus status) {
        this.type = type;
        this.status = status.value();
    }
}
