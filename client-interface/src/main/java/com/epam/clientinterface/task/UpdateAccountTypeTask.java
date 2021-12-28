package com.epam.clientinterface.task;

import com.epam.clientinterface.entity.Account;
import com.epam.clientinterface.enumerated.AccountType;
import com.epam.clientinterface.repository.AccountRepository;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateAccountTypeTask {

    private final AccountRepository accountRepository;

    @Scheduled(cron = "${app.task.update-account-type.cron}", zone = "${app.task.zone}")
    public void execute() {
        List<Account> accounts = accountRepository.findInvestAccounts();
        ZonedDateTime date = ZonedDateTime.now();
        if (!accounts.isEmpty()) {
            for (Account account : accounts) {
                if (account.getEndInvest().isBefore(date)) {
                    account.setType(AccountType.DEBIT);
                }
            }
            accountRepository.saveAll(accounts);
        }
    }
}
