package com.epam.clientinterface.domain.exception;

public class AccountIsNotSupposedForCard extends RuntimeException {
    public AccountIsNotSupposedForCard(long accountId) {
        super(String.format("Account of id=%d is not supposed for card", accountId));
    }
}
