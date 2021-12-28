package com.epam.clientinterface.util;

import com.epam.clientinterface.entity.Account;
import com.epam.clientinterface.entity.Card;
import com.epam.clientinterface.entity.User;
import com.epam.clientinterface.enumerated.AccountPlan;
import com.epam.clientinterface.enumerated.AccountType;
import com.epam.clientinterface.enumerated.CardPlan;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

public class TestDataFactory {

    private static final String zone = "Europe/Moscow";

    public static Account getDebitAccountBelongsToUser() {
        Account account = getDebitAccount();
        account.setUser(UserTestData.user);
        return account;
    }

    public static Account getClosedDebitAccountBelongsToUser() {
        Account account = getClosedDebitAccount();
        account.setUser(UserTestData.user);
        return account;
    }

    public static Account getInvestAccountBelongsToUser(ZonedDateTime start, ZonedDateTime end) {
        Account account = getInvestAccount(start, end);
        account.setUser(UserTestData.user);
        return account;
    }

    public static Account getAccount(AccountType type) {
        var user = new User();
        var account = new Account(
            RandomUtils.nextLong(),
            RandomStringUtils.randomNumeric(20),
            RandomUtils.nextBoolean(),
            AccountPlan.values()[RandomUtils.nextInt(0, AccountPlan.values().length)],
            RandomUtils.nextDouble(10000.0, 100000.0),
            user,
            new ArrayList<>(),
            null,
            type,
            null,
            null,
            new ArrayList<>()
        );
        user = getUser(account);
        account.setUser(user);

        return account;
    }

    public static Account getDebitAccount() {
        return getAccount(AccountType.DEBIT);
    }

    public static Account getClosedDebitAccount() {
        Account account = getAccount(AccountType.DEBIT);
        account.setClosedAt(ZonedDateTime.now());

        return account;
    }

    public static Account getInvestAccount(ZonedDateTime start, ZonedDateTime end) {
        Account account = getAccount(AccountType.INVEST);
        account.setStartInvest(start);
        account.setEndInvest(end);
        return account;
    }

    public static List<Account> getInvestAccounts(int quantity, boolean flag) {
        List<Account> investAccounts = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            if (flag) {
                investAccounts.add(getInvestAccount(ZonedDateTime.now().minusDays(1),
                    ZonedDateTime.now()));
            } else {
                investAccounts.add(getInvestAccount(between(ZonedDateTime.now().minusDays(5),
                        ZonedDateTime.now().minusDays(3)),
                    between(ZonedDateTime.now().minusDays(5), ZonedDateTime.now().plusDays(5))));
            }
        }
        return investAccounts;
    }

    protected static ZonedDateTime between(ZonedDateTime startInclusive, ZonedDateTime endExclusive) {
        long startEpochDay = startInclusive.toEpochSecond();
        long endEpochDay = endExclusive.toEpochSecond();
        long randomSec = ThreadLocalRandom
            .current()
            .nextLong(startEpochDay, endEpochDay);

        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(randomSec), TimeZone.getTimeZone(zone).toZoneId());
    }

    public static Card getCard() {
        return new Card(
            RandomUtils.nextLong(),
            RandomStringUtils.randomNumeric(16),
            getDebitAccount(),
            RandomStringUtils.randomNumeric(4),
            CardPlan.values()[RandomUtils.nextInt(0, CardPlan.values().length)],
            false,
            ZonedDateTime.now().plusYears(3),
            0
        );
    }

    public static Card getCardWithClosedAccount() {
        return new Card(
            RandomUtils.nextLong(),
            RandomStringUtils.randomNumeric(16),
            getClosedDebitAccountBelongsToUser(),
            RandomStringUtils.randomNumeric(4),
            CardPlan.values()[RandomUtils.nextInt(0, CardPlan.values().length)],
            false,
            ZonedDateTime.now().plusYears(3),
            0
        );
    }

    public static User getUser() {
        return new User(
            RandomUtils.nextLong(),
            RandomStringUtils.randomAlphabetic(4),
            RandomStringUtils.randomAlphabetic(4),
            RandomStringUtils.randomNumeric(7),
            RandomStringUtils.randomAlphabetic(4),
            RandomStringUtils.randomAlphabetic(4),
            RandomStringUtils.randomAlphanumeric(4),
            new ArrayList<>(),
            true,
            0,
            new HashSet<>()
        );
    }

    public static User getUser(Account account) {
        return new User(
            RandomUtils.nextLong(),
            RandomStringUtils.randomAlphabetic(4),
            RandomStringUtils.randomAlphabetic(4),
            RandomStringUtils.randomNumeric(7),
            RandomStringUtils.randomAlphabetic(4),
            RandomStringUtils.randomAlphabetic(4),
            RandomStringUtils.randomAlphanumeric(4),
            List.of(account),
            true,
            0,
            new HashSet<>()
        );
    }
}
