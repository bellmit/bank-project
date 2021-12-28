package com.epam.clientinterface.task;

import static com.epam.clientinterface.util.TestDataFactory.getInvestAccounts;
import static org.mockito.Mockito.when;

import com.epam.clientinterface.entity.Account;
import com.epam.clientinterface.enumerated.AccountType;
import com.epam.clientinterface.repository.AccountRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UpdateAccountTypeTest {

    private final List<Account> investAccounts = getInvestAccounts(2, false);

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private UpdateAccountTypeTask updateAccountTypeTask;

    @Test
    public void shouldUpdateAccountType() {
        when(accountRepository.findInvestAccounts()).thenReturn(investAccounts);

        updateAccountTypeTask.execute();

        LocalDate dateNow = LocalDate.now();

        for (Account investAccount : investAccounts) {
            if (dateNow.isAfter(investAccount.getEndInvest().toLocalDate())) {
                Assertions.assertEquals(AccountType.DEBIT, investAccount.getType());
            } else {
                Assertions.assertEquals(AccountType.INVEST, investAccount.getType());
            }
        }
    }
}
