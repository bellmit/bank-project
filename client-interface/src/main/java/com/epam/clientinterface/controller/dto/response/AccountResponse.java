package com.epam.clientinterface.controller.dto.response;

import com.epam.clientinterface.enumerated.AccountPlan;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountResponse {

    private final String number;

    private final boolean isDefault;

    private final AccountPlan plan;

    private final double amount;
}
