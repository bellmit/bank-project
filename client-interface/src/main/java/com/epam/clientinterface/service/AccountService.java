package com.epam.clientinterface.service;

import com.epam.clientinterface.domain.exception.AccountIsClosedException;
import com.epam.clientinterface.domain.exception.AccountIsNotSupposedForExternalTransferException;
import com.epam.clientinterface.domain.exception.AccountNotFoundException;
import com.epam.clientinterface.domain.exception.NotEnoughMoneyException;
import com.epam.clientinterface.entity.Account;
import com.epam.clientinterface.entity.Transaction;
import com.epam.clientinterface.entity.TransactionAccountData;
import com.epam.clientinterface.entity.TransactionOperationType;
import com.epam.clientinterface.entity.TransactionState;
import com.epam.clientinterface.repository.AccountRepository;
import com.epam.clientinterface.repository.TransactionRepository;
import com.fasterxml.jackson.databind.util.ArrayIterator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public void internalTransfer(long sourceAccountId, long destinationAccountId, double amount) {
        var sourceAccount = this.accountRepository.findById(sourceAccountId).orElseThrow(
            () -> new AccountNotFoundException(sourceAccountId)
        );
        var destinationAccount = this.accountRepository.findById(destinationAccountId).orElseThrow(
            () -> new AccountNotFoundException(destinationAccountId)
        );

        if (sourceAccount.getAmount() < amount) {
            this.transactionRepository.save(new Transaction(
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

        this.accountRepository.saveAll(new ArrayIterator<>(new Account[] {sourceAccount, destinationAccount}));
        this.transactionRepository.save(new Transaction(
            new TransactionAccountData(sourceAccount.getNumber(), false),
            new TransactionAccountData(destinationAccount.getNumber(), false),
            amount,
            TransactionOperationType.INTERNAL_TRANSFER,
            TransactionState.SUCCESS
        ));
    }

    public void externalTransfer(long sourceAccountId, String destinationAccountNumber, double amount) {
        var sourceAccount = this.accountRepository.findById(sourceAccountId).orElseThrow(
            () -> new AccountNotFoundException(sourceAccountId)
        );
        this.accountRepository.findByNumber(destinationAccountNumber).ifPresent(account -> {
            throw new AccountIsNotSupposedForExternalTransferException(account.getId());
        });

        if (sourceAccount.getAmount() < amount) {
            this.transactionRepository.save(new Transaction(
                new TransactionAccountData(sourceAccount.getNumber(), false),
                new TransactionAccountData(destinationAccountNumber, true),
                amount,
                TransactionOperationType.EXTERNAL_TRANSFER,
                TransactionState.DECLINE
            ));

            throw new NotEnoughMoneyException(sourceAccountId, amount);
        }

        this.transactionRepository.save(new Transaction(
            new TransactionAccountData(sourceAccount.getNumber(), false),
            new TransactionAccountData(destinationAccountNumber, true),
            amount,
            TransactionOperationType.EXTERNAL_TRANSFER,
            TransactionState.SUCCESS
        ));
    }

    public void closeAccount(long accountId) {
        var account = this.accountRepository.findById(accountId).orElseThrow(
            () -> new AccountNotFoundException(accountId)
        );

        if (account.isClosed()) {
            throw new AccountIsClosedException(accountId);
        }

        account.close();

        this.accountRepository.save(account);
    }
}
