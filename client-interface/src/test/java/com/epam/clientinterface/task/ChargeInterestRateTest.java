package com.epam.clientinterface.task;

import static com.epam.clientinterface.util.TestDataFactory.getInvestAccounts;
import static org.mockito.Mockito.when;

import com.epam.clientinterface.entity.Account;
import com.epam.clientinterface.repository.AccountRepository;
import com.epam.clientinterface.repository.IrTransactionRepository;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class ChargeInterestRateTest {

    private final double rate = 0.016;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private IrTransactionRepository irTransactionRepository;

    @InjectMocks
    private ChargeInterestRateTask chargeInterestRateTask;

    private final List<Account> investAccounts = getInvestAccounts(2, true);

    @BeforeEach
    public void init() {
        ReflectionTestUtils.setField(chargeInterestRateTask, "rate", rate);
    }

    @Test
    public void shouldAddInterestRate() {
        when(accountRepository.findInvestAccounts()).thenReturn(investAccounts);

        double amount1 = investAccounts.get(0).getAmount();
        double amount2 = investAccounts.get(1).getAmount();

        chargeInterestRateTask.execute();

        Assertions.assertEquals(amount1 + (amount1 * rate) / 365, investAccounts.get(0).getAmount());
        Assertions.assertEquals(amount2 + (amount2 * rate) / 365, investAccounts.get(1).getAmount());
    }
}
