package com.epam.clientinterface.task;

import com.epam.clientinterface.entity.Account;
import com.epam.clientinterface.entity.IrTransaction;
import com.epam.clientinterface.repository.AccountRepository;
import com.epam.clientinterface.repository.IrTransactionRepository;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChargeInterestRateTask {

    private final AccountRepository accountRepository;
    private final IrTransactionRepository irTransactionRepository;

    @Value("${account.interest-rate}")
    private double rate;

    @Scheduled(cron = "${app.task.charge-interest-rate.cron}", zone = "${app.task.zone}")
    public void execute() {
        List<Account> accounts = accountRepository.findInvestAccounts();
        if (!accounts.isEmpty()) {
            for (Account account : accounts) {
                addInterestRate(account);
            }
        }
    }

    protected void addInterestRate(Account account) {
        ZonedDateTime lastDate = getLastDate(account);
        LocalDate dateNow = LocalDate.now();

        while (dateNow.isAfter(lastDate.toLocalDate())) {
            lastDate = lastDate.plusDays(1);
            account.setAmount(account.getAmount() + (account.getAmount() * rate) / 365);
            accountRepository.save(account);
            irTransactionRepository.save(new IrTransaction(account.getAmount(), lastDate, account));
        }
    }

    protected ZonedDateTime getLastDate(Account account) {
        Optional<IrTransaction> lastCharge = irTransactionRepository
            .findFirstByAccountIdOrderByDateDesc(account.getId());
        return lastCharge.map(IrTransaction::getDate).orElseGet(account::getStartInvest);
    }

}
