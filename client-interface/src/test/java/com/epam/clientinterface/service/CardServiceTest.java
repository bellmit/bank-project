package com.epam.clientinterface.service;

import static com.epam.clientinterface.util.TestDataFactory.getCard;
import static com.epam.clientinterface.util.TestDataFactory.getClosedDebitAccountBelongsToUser;
import static com.epam.clientinterface.util.TestDataFactory.getDebitAccountBelongsToUser;
import static com.epam.clientinterface.util.TestDataFactory.getInvestAccountBelongsToUser;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epam.clientinterface.domain.exception.AccountIsClosedException;
import com.epam.clientinterface.domain.exception.AccountIsNotSupposedForCard;
import com.epam.clientinterface.domain.exception.AccountNotFoundException;
import com.epam.clientinterface.domain.exception.CardNotFoundException;
import com.epam.clientinterface.entity.Account;
import com.epam.clientinterface.entity.Card;
import com.epam.clientinterface.enumerated.CardPlan;
import com.epam.clientinterface.repository.AccountRepository;
import com.epam.clientinterface.repository.CardRepository;
import com.epam.clientinterface.service.util.RandomGenerate;
import com.epam.clientinterface.util.TestDataFactory;
import com.epam.clientinterface.util.UserTestData;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    private final Account debitAccount = getDebitAccountBelongsToUser();
    private final Account investAccount = getInvestAccountBelongsToUser(ZonedDateTime.now(),
        ZonedDateTime.now().plusYears(3));

    @InjectMocks
    private CardServiceImpl cardService;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private AccountRepository accountRepository;

    @Test
    void shouldReturnNewCardIfAccountIsExist() {
        when(accountRepository.findAccountByIdWithUser(anyLong(), anyLong()))
            .thenReturn(Optional.of(debitAccount));

        ZonedDateTime dateTimeBefore = ZonedDateTime.now().plusYears(3);
        cardService.releaseCard(1L, CardPlan.BASE, UserTestData.user.getId());
        ZonedDateTime dateTimeAfter = ZonedDateTime.now().plusYears(3);

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
        Assertions.assertEquals(saveCard.getAccount(), debitAccount);
        Assertions.assertEquals(saveCard.getPlan(), CardPlan.BASE);
        Assertions.assertEquals(16, saveCard.getNumber().length());
        Assertions.assertEquals(4, saveCard.getPinCode().length());
    }

    @Test
    void shouldThrowAccountNotFoundIfAccountDoesNotExist() {
        Assertions.assertThrows(AccountNotFoundException.class,
            () -> cardService.releaseCard(2L, CardPlan.BASE, 52));
    }

    @Test
    public void shouldThrowAccountIsNotSupposedForCard() {
        when(accountRepository.findAccountByIdWithUser(anyLong(), anyLong())).thenReturn(Optional.of(investAccount));

        Assertions.assertThrows(AccountIsNotSupposedForCard.class,
            () -> cardService.releaseCard(investAccount.getId(), CardPlan.BASE,  investAccount.getUser().getId()));
    }

    @Test
    public void shouldReturnDifferentCards() {
        when(accountRepository.findAccountByIdWithUser(1L, UserTestData.user.getId()))
            .thenReturn(Optional.of(getDebitAccountBelongsToUser()));
        ArgumentCaptor<Card> cardCaptor = ArgumentCaptor.forClass(Card.class);

        cardService.releaseCard(1L, CardPlan.BASE, UserTestData.user.getId());
        cardService.releaseCard(1L, CardPlan.BASE, UserTestData.user.getId());
        verify(cardRepository, times(2)).save(cardCaptor.capture());

        Card card1 = cardCaptor.getAllValues().get(0);
        Card card2 = cardCaptor.getAllValues().get(1);
        Assertions.assertNotEquals(card1.getNumber(), card2.getNumber());
    }

    @Test
    void shouldThrowAccountIsClosedIfAccountIsClosedWhenReleaseNewCard() {
        Account acc = getClosedDebitAccountBelongsToUser();
        when(accountRepository.findAccountByIdWithUser(anyLong(), anyLong())).thenReturn(Optional.of(acc));

        Assertions.assertThrows(
            AccountIsClosedException.class,
            () -> cardService.releaseCard(acc.getId(), CardPlan.BASE, acc.getUser().getId())
        );
    }

    @Test
    void shouldReturnCardNumber() {
        Assertions.assertEquals(16, RandomGenerate.generateCardNumber().length());
    }

    @Test
    void shouldReturnPinCode() {
        Assertions.assertEquals(4, RandomGenerate.generatePinCode().length());
    }

    @Test
    public void shouldBlockCardIfCardIsExist() {

        Card testCard = getCard();
        when(cardRepository.findById(anyLong())).thenReturn(Optional.of(testCard));

        cardService.blockCard(testCard.getId(), testCard.getAccount().getUser().getId());

        ArgumentCaptor<Card> cardCaptor = ArgumentCaptor.forClass(Card.class);
        verify(cardRepository).save(cardCaptor.capture());

        Card blockedCard = cardCaptor.getValue();
        verify(cardRepository).save(blockedCard);
        Assertions.assertTrue(blockedCard.isBlocked());
    }

    @Test
    public void shouldThrowCardNotFoundIfCardDoesNotExist() {
        when(cardRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(CardNotFoundException.class,
            () -> cardService.blockCard(1L, UserTestData.user.getId()));
    }

    @Test
    public void shouldThrowAccountIsClosedIfAccountIsClosedWhenBlockCard() {
        var cardFixture = TestDataFactory.getCardWithClosedAccount();

        when(this.cardRepository.findById(anyLong())).thenReturn(Optional.of(cardFixture));

        Assertions.assertThrows(AccountIsClosedException.class,
            () -> cardService.blockCard(cardFixture.getId(), 1));
    }
}