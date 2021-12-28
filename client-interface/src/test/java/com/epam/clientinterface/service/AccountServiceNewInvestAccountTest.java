package com.epam.clientinterface.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epam.clientinterface.domain.exception.UserNotFoundException;
import com.epam.clientinterface.entity.Account;
import com.epam.clientinterface.entity.User;
import com.epam.clientinterface.enumerated.AccountType;
import com.epam.clientinterface.repository.AccountRepository;
import com.epam.clientinterface.repository.UserRepository;
import com.epam.clientinterface.service.util.RandomGenerate;
import com.epam.clientinterface.util.TestDataFactory;
import java.util.Optional;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountServiceNewInvestAccountTest {

    private final User user = TestDataFactory.getUser();

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    public void shouldReturnNewInvestAccount() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        var accountId = RandomUtils.nextLong();
        var amount = RandomUtils.nextDouble(10000.0, 100000.0);
        var period = RandomUtils.nextInt(2, 6);

        accountService.createInvestAccount(accountId, AccountType.INVEST, amount, period);

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accountCaptor.capture());

        Account saveAccount = accountCaptor.getValue();

        Assertions.assertEquals(user, saveAccount.getUser());
        Assertions.assertEquals(AccountType.INVEST, saveAccount.getType());
        Assertions.assertEquals(20, saveAccount.getNumber().length());
        Assertions.assertEquals(amount, saveAccount.getAmount());
        Assertions.assertEquals(saveAccount.getStartInvest().plusMonths(period), saveAccount.getEndInvest());
    }

    @Test
    public void shouldThrowUserNotFoundIfUserDoesNotExist() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class,
            () -> accountService.createInvestAccount(RandomUtils.nextLong(), AccountType.INVEST,
                RandomUtils.nextDouble(10000.0, 100000.0), RandomUtils.nextInt(2, 6)));
    }

    @Test
    public void shouldReturnDifferentAccounts() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        ArgumentCaptor<Account> argumentCaptor = ArgumentCaptor.forClass(Account.class);

        accountService.createInvestAccount(7L, AccountType.INVEST,
            RandomUtils.nextDouble(10000.0, 100000.0), RandomUtils.nextInt(2, 6));
        accountService.createInvestAccount(7L, AccountType.INVEST,
            RandomUtils.nextDouble(10000.0, 100000.0), RandomUtils.nextInt(2, 6));
        verify(accountRepository, times(2)).save(argumentCaptor.capture());

        Account account1 = argumentCaptor.getAllValues().get(0);
        Account account2 = argumentCaptor.getAllValues().get(1);
        Assertions.assertNotEquals(account1.getNumber(), account2.getNumber());
    }

    @Test
    public void shouldReturnAccountNumber() {
        Assertions.assertEquals(20, RandomGenerate.generateAccountNumber().length());
    }
}
