package com.epam.clientinterface.service.util;

import com.epam.clientinterface.domain.exception.AccountIsClosedException;
import com.epam.clientinterface.domain.exception.AccountIsNotSupposedForCard;
import com.epam.clientinterface.domain.exception.AccountIsNotSupposedForWithdraw;
import com.epam.clientinterface.domain.exception.AccountNotFoundException;
import com.epam.clientinterface.entity.Account;

public class DomainLogicChecker {
    public static void assertAccountIsNotClosed(Account account) {
        if (account.isClosed()) {
            throw new AccountIsClosedException(account.getId());
        }
    }

    public static void assertAccountIsSuitableForWithdraw(Account account) {
        if (account.isInvest()) {
            throw new AccountIsNotSupposedForWithdraw(account.getId());
        }
    }

    public static void assertAccountIsSuitableForCard(Account account) {
        if (account.isInvest()) {
            throw new AccountIsNotSupposedForCard(account.getId());
        }
    }

    public static void assertAccountBelongsToUser(Account account, long userId) {
        if (account.getUser().getId() != userId) {
            throw new AccountNotFoundException(account.getId());
        }
    }
}
