package com.epam.clientinterface.service;

import com.epam.clientinterface.domain.exception.AccountIsNotSupposedForExternalTransferException;
import com.epam.clientinterface.domain.exception.AccountNotFoundException;
import com.epam.clientinterface.domain.exception.NotEnoughMoneyException;
import com.epam.clientinterface.domain.exception.UserNotFoundException;
import com.epam.clientinterface.entity.Account;
import com.epam.clientinterface.entity.Transaction;
import com.epam.clientinterface.entity.TransactionAccountData;
import com.epam.clientinterface.entity.User;
import com.epam.clientinterface.enumerated.AccountPlan;
import com.epam.clientinterface.enumerated.AccountType;
import com.epam.clientinterface.enumerated.TransactionOperationType;
import com.epam.clientinterface.enumerated.TransactionState;
import com.epam.clientinterface.repository.AccountRepository;
import com.epam.clientinterface.repository.TransactionRepository;
import com.epam.clientinterface.repository.UserRepository;
import com.epam.clientinterface.service.util.DomainLogicChecker;
import com.epam.clientinterface.service.util.RandomGenerate;
import com.fasterxml.jackson.databind.util.ArrayIterator;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public void internalTransfer(long sourceAccountId, long destinationAccountId, double amount, long userId) {
        var sourceAccount = accountRepository.findAccountByIdWithUser(sourceAccountId, userId).orElseThrow(
            () -> new AccountNotFoundException(sourceAccountId)
        );

        DomainLogicChecker.assertAccountBelongsToUser(sourceAccount, userId);
        DomainLogicChecker.assertAccountIsNotClosed(sourceAccount);
        DomainLogicChecker.assertAccountIsSuitableForWithdraw(sourceAccount);

        var destinationAccount = accountRepository.findAccountByIdWithUser(destinationAccountId, userId).orElseThrow(
            () -> new AccountNotFoundException(destinationAccountId)
        );

        DomainLogicChecker.assertAccountBelongsToUser(destinationAccount, userId);
        DomainLogicChecker.assertAccountIsNotClosed(destinationAccount);

        if (sourceAccount.getAmount() < amount) {
            transactionRepository.save(new Transaction(
                new TransactionAccountData(sourceAccount.getNumber(), false),
                new TransactionAccountData(destinationAccount.getNumber(), false),
                amount,
                TransactionOperationType.INTERNAL_TRANSFER,
                TransactionState.DECLINE
            ));

            throw new NotEnoughMoneyException(sourceAccountId, amount);
        }

        sourceAccount.setAmount(sourceAccount.getAmount() - amount);
        destinationAccount.setAmount(destinationAccount.getAmount() + amount);

        accountRepository.saveAll(new ArrayIterator<>(new Account[] {sourceAccount, destinationAccount}));
        transactionRepository.save(new Transaction(
            new TransactionAccountData(sourceAccount.getNumber(), false),
            new TransactionAccountData(destinationAccount.getNumber(), false),
            amount,
            TransactionOperationType.INTERNAL_TRANSFER,
            TransactionState.SUCCESS
        ));
    }

    public void externalTransfer(long sourceAccountId, String destinationAccountNumber, double amount, long userId) {
        var sourceAccount = accountRepository.findAccountByIdWithUser(sourceAccountId, userId).orElseThrow(
            () -> new AccountNotFoundException(sourceAccountId)
        );

        DomainLogicChecker.assertAccountBelongsToUser(sourceAccount, userId);
        DomainLogicChecker.assertAccountIsNotClosed(sourceAccount);
        DomainLogicChecker.assertAccountIsSuitableForWithdraw(sourceAccount);

        accountRepository.findByNumber(destinationAccountNumber).ifPresent(account -> {
            throw new AccountIsNotSupposedForExternalTransferException(account.getId());
        });

        if (sourceAccount.getAmount() < amount) {
            transactionRepository.save(new Transaction(
                new TransactionAccountData(sourceAccount.getNumber(), false),
                new TransactionAccountData(destinationAccountNumber, true),
                amount,
                TransactionOperationType.EXTERNAL_TRANSFER,
                TransactionState.DECLINE
            ));

            throw new NotEnoughMoneyException(sourceAccountId, amount);
        }

        transactionRepository.save(new Transaction(
            new TransactionAccountData(sourceAccount.getNumber(), false),
            new TransactionAccountData(destinationAccountNumber, true),
            amount,
            TransactionOperationType.EXTERNAL_TRANSFER,
            TransactionState.SUCCESS
        ));
    }

    public void closeAccount(long accountId, long userId) {
        var account = accountRepository.findAccountByIdWithUser(accountId, userId).orElseThrow(
            () -> new AccountNotFoundException(accountId)
        );

        DomainLogicChecker.assertAccountBelongsToUser(account, userId);
        DomainLogicChecker.assertAccountIsNotClosed(account);
        account.close();
        accountRepository.save(account);
    }

    public @NonNull Account createInvestAccount(long userId, @NonNull AccountType type,
                                                double amount, int period) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        String number;
        // TODO: a potentially infinite loop
        do {
            number = RandomGenerate.generateAccountNumber();
        } while (accountRepository.findByNumber(number).isPresent());

        ZonedDateTime startInvest = ZonedDateTime.now();
        ZonedDateTime endInvest = startInvest.plusMonths(period);
        Account account = new Account(user.get(), number, AccountPlan.BASE, amount, AccountType.INVEST, startInvest,
            endInvest);
        return accountRepository.save(account);
    }
}
