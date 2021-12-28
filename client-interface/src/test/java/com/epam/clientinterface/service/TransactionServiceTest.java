package com.epam.clientinterface.service;

import com.epam.clientinterface.domain.exception.AccountNotFoundException;
import com.epam.clientinterface.entity.Transaction;
import com.epam.clientinterface.entity.TransactionAccountData;
import com.epam.clientinterface.enumerated.TransactionOperationType;
import com.epam.clientinterface.enumerated.TransactionState;
import com.epam.clientinterface.repository.AccountRepository;
import com.epam.clientinterface.repository.TransactionRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepositoryMock;

    @Mock
    private AccountRepository accountRepositoryMock;

    private TransactionService transactionService;

    @BeforeEach
    public void setUp() {
        transactionService = new TransactionService(transactionRepositoryMock, accountRepositoryMock);
    }

    @Test
    public void shouldThrowsAccountNotFoundException() {
        Mockito.when(accountRepositoryMock.findAccountsByUserId(Mockito.anyLong()))
            .thenReturn(new LinkedList<>());

        Long userId = RandomUtils.nextLong();
        String accountNumber = RandomStringUtils.random(10);

        Exception exception = Assertions.assertThrows(AccountNotFoundException.class,
            () -> transactionService.readTransactions(userId, accountNumber));

        Assertions.assertEquals(String.format("User with id=%d don't have account with number=%s",
                userId,
                accountNumber),
            exception.getMessage()
        );
    }

    @Test
    public void shouldReturnListOfTransactions() {
        Transaction transaction = new Transaction(RandomUtils.nextLong(),
            new TransactionAccountData(RandomStringUtils.random(10), RandomUtils.nextBoolean()),
            new TransactionAccountData(RandomStringUtils.random(10), RandomUtils.nextBoolean()),
            RandomUtils.nextDouble(),
            LocalDateTime.now(),
            TransactionOperationType.INTERNAL_TRANSFER,
            TransactionState.SUCCESS);

        Long userId = RandomUtils.nextLong();
        String accountNumber = RandomStringUtils.random(10);

        Mockito.when(accountRepositoryMock.findAccountsByUserId(userId)).thenReturn(Arrays.asList(accountNumber));
        Mockito.when(transactionRepositoryMock.getTransactionsByNumber(accountNumber))
            .thenReturn(Arrays.asList(transaction));

        List<Transaction> result = transactionService.readTransactions(userId, accountNumber);

        Assertions.assertEquals(Arrays.asList(transaction), result);
    }
}