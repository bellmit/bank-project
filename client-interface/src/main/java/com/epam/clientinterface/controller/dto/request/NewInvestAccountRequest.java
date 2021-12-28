package com.epam.clientinterface.controller.dto.request;

import com.epam.clientinterface.enumerated.AccountType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class NewInvestAccountRequest {
    @Positive
    private long userId;

    @NotNull
    private AccountType type;

    @Positive
    private double amount;

    @Positive
    private int period;
}
