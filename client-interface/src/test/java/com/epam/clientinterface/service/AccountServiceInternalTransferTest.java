package com.epam.clientinterface.service;

import static org.mockito.Mockito.when;

import com.epam.clientinterface.domain.exception.AccountNotFoundException;
import com.epam.clientinterface.domain.exception.NotEnoughMoneyException;
import com.epam.clientinterface.entity.Account;
import com.epam.clientinterface.entity.User;
import com.epam.clientinterface.repository.AccountRepository;
import com.epam.clientinterface.repository.TransactionRepository;
import java.util.ArrayList;
import java.util.Optional;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountServiceInternalTransferTest {
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepositoryMock;

    @Mock
    private TransactionRepository transactionRepositoryMock;

    @BeforeEach
    public void beforeEach() {
        this.accountService = new AccountService(this.accountRepositoryMock, this.transactionRepositoryMock);
    }

    @Test
    public void shouldReturnNothingIfAccountsExistAndThereIsEnoughMoney() {
        when(this.accountRepositoryMock.findById(1L)).thenReturn(Optional.of(this.getAccountFixture(1L)));
        when(this.accountRepositoryMock.findById(2L)).thenReturn(Optional.of(this.getAccountFixture(2L)));

        this.accountService.internalTransfer(1L, 2L, 1000.00);
    }

    @Test
    public void shouldThrowAccountNotFoundIfTheSourceAccountDoesNotExist() {
        when(this.accountRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(
            AccountNotFoundException.class,
            () -> this.accountService.internalTransfer(1L, 2L, 1000.00)
        );
    }

    @Test
    public void shouldThrowAccountNotFoundIfTheDestinationAccountDoesNotExist() {
        when(this.accountRepositoryMock.findById(1L)).thenReturn(Optional.of(this.getAccountFixture(1L)));
        when(this.accountRepositoryMock.findById(2L)).thenReturn(Optional.empty());

        Assertions.assertThrows(
            AccountNotFoundException.class,
            () -> this.accountService.internalTransfer(1L, 2L, 1000.00)
        );
    }

    @Test
    public void shouldThrowNotEnoughMoneyIfSourceAccountDoesNotHaveEnoughMoney() {
        when(this.accountRepositoryMock.findById(1L)).thenReturn(Optional.of(this.getAccountFixture(1L)));
        when(this.accountRepositoryMock.findById(2L)).thenReturn(Optional.of(this.getAccountFixture(2L)));

        Assertions.assertThrows(
            NotEnoughMoneyException.class,
            () -> this.accountService.internalTransfer(1L, 2L, 100000.00)
        );
    }

    private Account getAccountFixture(long id) {
        return new Account(
            id,
            RandomStringUtils.randomNumeric(20),
            RandomUtils.nextBoolean(),
            Account.Plan.values()[RandomUtils.nextInt(0, Account.Plan.values().length)],
            RandomUtils.nextDouble(10000.0, 100000.0),
            new User(),
            new ArrayList<>(),
            null
        );
    }
}
