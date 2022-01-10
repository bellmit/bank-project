package com.epam.bank.operatorinterface.domain.dto;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class ErrorResponse {
    private final String type;

    public ErrorResponse(@NonNull String type) {
        this.type = type;
    }
}
