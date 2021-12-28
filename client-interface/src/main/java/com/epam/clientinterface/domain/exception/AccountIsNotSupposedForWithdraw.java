package com.epam.clientinterface.domain.exception;

public class AccountIsNotSupposedForWithdraw extends RuntimeException {
    public AccountIsNotSupposedForWithdraw(long accountId) {
        super(String.format("Account of id=%d is not supposed for withdraw", accountId));
    }
}
