package com.epam.clientinterface.service;

import static com.epam.clientinterface.util.TestDataFactory.getClosedDebitAccountBelongsToUser;
import static com.epam.clientinterface.util.TestDataFactory.getDebitAccount;
import static com.epam.clientinterface.util.TestDataFactory.getDebitAccountBelongsToUser;
import static com.epam.clientinterface.util.TestDataFactory.getInvestAccountBelongsToUser;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.epam.clientinterface.domain.exception.AccountIsClosedException;
import com.epam.clientinterface.domain.exception.AccountIsNotSupposedForWithdraw;
import com.epam.clientinterface.domain.exception.AccountNotFoundException;
import com.epam.clientinterface.domain.exception.NotEnoughMoneyException;
import com.epam.clientinterface.repository.AccountRepository;
import com.epam.clientinterface.repository.TransactionRepository;
import com.epam.clientinterface.repository.UserRepository;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountServiceInternalTransferTest {
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepositoryMock;

    @Mock
    private TransactionRepository transactionRepositoryMock;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void beforeEach() {
        this.accountService = new AccountService(this.accountRepositoryMock, this.transactionRepositoryMock,
            this.userRepository);
    }

    @Test
    void shouldReturnNothingIfAccountsExistAndThereIsEnoughMoney() {
        var sourceAccountFixture = getDebitAccountBelongsToUser();
        var destinationAccountFixture = getDebitAccountBelongsToUser();

        when(accountRepositoryMock.findAccountByIdWithUser(anyLong(), anyLong()))
            .thenReturn(Optional.of(sourceAccountFixture))
            .thenReturn(Optional.of(destinationAccountFixture));

        accountService.internalTransfer(sourceAccountFixture.getId(),
            destinationAccountFixture.getId(),
            RandomUtils.nextDouble(1000.0, 10000.0), 1);
    }

    @Test
    void shouldThrowAccountNotFoundIfTheSourceAccountDoesNotExist() {
        Assertions.assertThrows(
            AccountNotFoundException.class,
            () -> this.accountService.internalTransfer(RandomUtils.nextLong(),
                RandomUtils.nextLong(),
                RandomUtils.nextDouble(1000.0, 10000.0),  1)
        );
    }

    @Test
    void shouldThrowAccountNotFoundIfTheDestinationAccountDoesNotExist() {
        var sourceAccountFixture = getDebitAccount();

        Assertions.assertThrows(
            AccountNotFoundException.class,
            () -> this.accountService.internalTransfer(sourceAccountFixture.getId(),
                RandomUtils.nextLong(),
                RandomUtils.nextDouble(1000.0, 10000.0), 1)
        );
    }

    @Test
    void shouldThrowNotEnoughMoneyIfSourceAccountDoesNotHaveEnoughMoney() {
        var sourceAccountFixture = getDebitAccountBelongsToUser();
        var destinationAccountFixture = getDebitAccountBelongsToUser();

        when(this.accountRepositoryMock.findAccountByIdWithUser(anyLong(), anyLong()))
            .thenReturn(Optional.of(sourceAccountFixture))
            .thenReturn(Optional.of(destinationAccountFixture));

        Assertions.assertThrows(
            NotEnoughMoneyException.class,
            () -> this.accountService.internalTransfer(
                sourceAccountFixture.getId(),
                destinationAccountFixture.getId(),
                RandomUtils.nextDouble(100000.0, 1000000.0), 1
            )
        );
    }

    @Test
    void shouldThrowAccountIsClosedIfSourceAccountIsClosed() {
        var sourceAccountFixture = getClosedDebitAccountBelongsToUser();
        var destinationAccountFixture = getDebitAccountBelongsToUser();

        when(accountRepositoryMock.findAccountByIdWithUser(anyLong(), anyLong()))
            .thenReturn(Optional.of(sourceAccountFixture))
            .thenReturn(Optional.of(destinationAccountFixture));

        Assertions.assertThrows(
            AccountIsClosedException.class,
            () -> this.accountService.internalTransfer(
                sourceAccountFixture.getId(),
                destinationAccountFixture.getId(),
                RandomUtils.nextDouble(1000.0, 10000.0), 1
            )
        );
    }

    @Test
    void shouldThrowAccountIsClosedIfDestinationAccountIsClosed() {
        var sourceAccountFixture = getDebitAccountBelongsToUser();
        var destinationAccountFixture = getClosedDebitAccountBelongsToUser();

        when(accountRepositoryMock.findAccountByIdWithUser(anyLong(), anyLong()))
            .thenReturn(Optional.of(sourceAccountFixture))
            .thenReturn(Optional.of(destinationAccountFixture));

        Assertions.assertThrows(
            AccountIsClosedException.class,
            () -> accountService.internalTransfer(
                sourceAccountFixture.getId(),
                destinationAccountFixture.getId(),
                RandomUtils.nextDouble(1000.0, 10000.0), 1
            )
        );
    }

    @Test
    void shouldThrowAccountNotFound_IfAccountDoesNotBelongToUsers() {
        Assertions.assertThrows(AccountNotFoundException.class,
            () -> accountService.internalTransfer(getDebitAccount().getId(), getDebitAccount().getId(),
                100, 1));
    }

    @Test
    public void shouldThrowAccountIsNotSupposedForWithdraw() {
        var sourceAccountFixture = getInvestAccountBelongsToUser(ZonedDateTime.now(),
            ZonedDateTime.now().plusYears(1));
        var destinationAccountFixture = getDebitAccount();
        when(accountRepositoryMock.findAccountByIdWithUser(anyLong(), anyLong()))
            .thenReturn(Optional.of(sourceAccountFixture));

        Assertions.assertThrows(
            AccountIsNotSupposedForWithdraw.class,
            () -> accountService.internalTransfer(
                sourceAccountFixture.getId(), destinationAccountFixture.getId(),
                RandomUtils.nextDouble(1000.0, 10000.0),
                sourceAccountFixture.getUser().getId()
            )
        );
    }
}
