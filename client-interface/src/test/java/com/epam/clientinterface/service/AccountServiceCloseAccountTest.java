package com.epam.clientinterface.service;

import static com.epam.clientinterface.util.TestDataFactory.getDebitAccountBelongsToUser;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epam.clientinterface.domain.exception.AccountIsClosedException;
import com.epam.clientinterface.domain.exception.AccountNotFoundException;
import com.epam.clientinterface.entity.Account;
import com.epam.clientinterface.repository.AccountRepository;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountServiceCloseAccountTest {
    @Mock
    private AccountRepository accountRepositoryMock;

    @InjectMocks
    private AccountService accountService;

    @Captor
    private ArgumentCaptor<Account> accountCaptor;

    @Test
    void shouldReturnNothingIfAccountExists() {
        var accountFixture = getDebitAccountBelongsToUser();
        when(accountRepositoryMock.findAccountByIdWithUser(anyLong(), anyLong()))
            .thenReturn(Optional.of(accountFixture));

        accountService.closeAccount(accountFixture.getId(), accountFixture.getUser().getId());

        verify(accountRepositoryMock).save(accountCaptor.capture());
        Assertions.assertNotNull(accountCaptor.getValue().getClosedAt());
    }

    @Test
    void shouldThrowAccountNotFoundIfAccountDoesNotExist() {
        Assertions.assertThrows(AccountNotFoundException.class, () -> this.accountService.closeAccount(1L, 1L));
    }

    @Test
    void shouldThrowAccountIsClosedIfAccountIsClosed() {
        var accountFixture = getDebitAccountBelongsToUser();
        accountFixture.close();

        when(accountRepositoryMock.findAccountByIdWithUser(anyLong(), anyLong()))
            .thenReturn(Optional.of(accountFixture));

        Assertions.assertThrows(AccountIsClosedException.class, () -> accountService.closeAccount(1L, 1L));
    }
}
