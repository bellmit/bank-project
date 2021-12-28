package com.epam.clientinterface.service;

import com.epam.clientinterface.controller.dto.request.ChangePinRequest;
import com.epam.clientinterface.domain.exception.CardNotFoundException;
import com.epam.clientinterface.domain.exception.ChangePinException;
import com.epam.clientinterface.entity.Account;
import com.epam.clientinterface.entity.Card;
import com.epam.clientinterface.entity.User;
import com.epam.clientinterface.enumerated.AccountPlan;
import com.epam.clientinterface.enumerated.AccountType;
import com.epam.clientinterface.enumerated.CardPlan;
import com.epam.clientinterface.repository.AccountRepository;
import com.epam.clientinterface.repository.CardRepository;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Optional;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChangePinCardServiceTest {
    private ChangePinRequest changePinRequest;
    private Account testAccount;

    private CardService cardService;

    @Mock
    private CardRepository cardRepositoryMock;

    @Mock
    private AccountRepository accountRepositoryMock;

    @BeforeEach
    public void setUp() {
        changePinRequest = new ChangePinRequest(RandomUtils.nextLong(), "1234");
        testAccount = new Account(RandomUtils.nextLong(), RandomStringUtils.random(10), true,
            AccountPlan.BASE, RandomUtils.nextDouble(), new User(), new ArrayList<>(), ZonedDateTime.now(),
            AccountType.DEBIT, null, null, new ArrayList<>());
        cardService = new CardServiceImpl(cardRepositoryMock, accountRepositoryMock);
    }

    @Test
    public void shouldThrowsCardNotFoundException() {
        Mockito.when(cardRepositoryMock.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        CardNotFoundException thrownException = Assertions.assertThrows(CardNotFoundException.class,
            () -> cardService.changePinCode(changePinRequest));

        Assertions.assertEquals(String.format("Card with id %s not found", changePinRequest.getCardId()),
            thrownException.getMessage());
    }

    @Test
    public void shouldThrowsChangePinException() {
        Card testCard = new Card(1L, RandomStringUtils.random(10), testAccount, "1111",
            CardPlan.BASE, false, ZonedDateTime.now(), 3);

        Mockito.when(cardRepositoryMock.findById(Mockito.anyLong())).thenReturn(Optional.of(testCard));

        ChangePinException thrownException = Assertions.assertThrows(ChangePinException.class,
            () -> cardService.changePinCode(changePinRequest));

        Assertions.assertEquals("You have already changed your pin for 3 times! Try again tomorrow!",
            thrownException.getMessage());
    }

    @Test
    public void shouldChangePinIfHaveAttempts() {
        Card testCard = new Card(1L, RandomStringUtils.random(10), testAccount, "1111",
            CardPlan.BASE, false, ZonedDateTime.now(), 2);
        ArgumentCaptor<Card> cardArgumentCaptor = ArgumentCaptor.forClass(Card.class);

        Mockito.when(cardRepositoryMock.findById(Mockito.anyLong())).thenReturn(Optional.of(testCard));

        cardService.changePinCode(changePinRequest);

        Mockito.verify(cardRepositoryMock).save(cardArgumentCaptor.capture());

        Assertions.assertEquals(3, cardArgumentCaptor.getValue().getPinCounter());
    }
}