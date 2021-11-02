package com.epam.bank.clientinterface.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epam.clientinterface.domain.exception.AccountNotFoundException;
import com.epam.clientinterface.entity.Account;
import com.epam.clientinterface.entity.Card;
import com.epam.clientinterface.entity.CardPlan;
import com.epam.clientinterface.entity.User;
import com.epam.clientinterface.repository.AccountRepository;
import com.epam.clientinterface.repository.CardRepository;
import com.epam.clientinterface.service.CardService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    private CardService cardService;
    private final Account account = new Account(1L, "", true, Account.Plan.BASE,
        1000, new User(), new ArrayList<>());

    @Mock
    private CardRepository cardRepository;

    @Mock
    private AccountRepository accountRepository;

    @BeforeEach
    public void setUp() {
        cardService = new CardService(cardRepository, accountRepository);
    }

    @Test
    public void shouldReturnNewCardIfAccountIsExist() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        LocalDateTime dateTimeBefore = LocalDateTime.now().plusYears(3);
        cardService.releaseCard(1L, CardPlan.BASE);
        LocalDateTime dateTimeAfter = LocalDateTime.now().plusYears(3);

        ArgumentCaptor<Card> cardCaptor = ArgumentCaptor.forClass(Card.class);
        verify(cardRepository).save(cardCaptor.capture());

        Card saveCard = cardCaptor.getValue();
        Assertions.assertTrue(
            dateTimeBefore.isBefore(saveCard.getExpirationDate())
                || dateTimeBefore.isEqual(saveCard.getExpirationDate())
        );
        Assertions.assertTrue(
            dateTimeAfter.isAfter(saveCard.getExpirationDate())
                || dateTimeAfter.isEqual(saveCard.getExpirationDate())
        );
        Assertions.assertEquals(saveCard.getAccount(), account);
        Assertions.assertEquals(saveCard.getPlan(), CardPlan.BASE);
        Assertions.assertEquals(saveCard.getNumber().length(), 16);
        Assertions.assertEquals(saveCard.getPinCode().length(), 4);
    }

    @Test
    public void shouldThrowAccountNotFoundIfAccountDoesNotExist() {
        when(accountRepository.findById(2L)).thenReturn(Optional.empty());

        Assertions.assertThrows(AccountNotFoundException.class,
            () -> cardService.releaseCard(2L, CardPlan.BASE));
    }

    @Test
    public void shouldReturnDifferentCards() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        ArgumentCaptor<Card> cardCaptor = ArgumentCaptor.forClass(Card.class);

        cardService.releaseCard(1L, CardPlan.BASE);
        cardService.releaseCard(1L, CardPlan.BASE);
        verify(cardRepository, times(2)).save(cardCaptor.capture());

        Card card1 = cardCaptor.getAllValues().get(0);
        Card card2 = cardCaptor.getAllValues().get(1);
        Assertions.assertNotEquals(card1.getNumber(), card2.getNumber());
    }

    @Test
    public void shouldReturnCardNumber() {
        Assertions.assertEquals(16, cardService.generate(16).length());
    }

    @Test
    public void shouldReturnPinCode() {
        Assertions.assertEquals(4, cardService.generate(4).length());
    }

}
