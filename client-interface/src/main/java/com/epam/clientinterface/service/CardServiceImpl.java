package com.epam.clientinterface.service;

import static com.epam.clientinterface.service.util.RandomGenerate.generateCardNumber;
import static com.epam.clientinterface.service.util.RandomGenerate.generatePinCode;

import com.epam.clientinterface.controller.dto.request.ChangePinRequest;
import com.epam.clientinterface.domain.exception.AccountNotFoundException;
import com.epam.clientinterface.domain.exception.CardNotFoundException;
import com.epam.clientinterface.domain.exception.ChangePinException;
import com.epam.clientinterface.entity.Account;
import com.epam.clientinterface.entity.Card;
import com.epam.clientinterface.enumerated.CardPlan;
import com.epam.clientinterface.repository.AccountRepository;
import com.epam.clientinterface.repository.CardRepository;
import com.epam.clientinterface.service.util.DomainLogicChecker;
import com.epam.clientinterface.service.util.PinValidator;
import java.time.ZonedDateTime;
import javax.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional //can't mock proxy
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;

    @Override
    public Card changePinCode(ChangePinRequest pinRequest) {
        Card card = cardRepository.findById(pinRequest.getCardId()).orElse(null);
        if (card == null) {
            throw new CardNotFoundException(pinRequest.getCardId());
        }

        PinValidator.validatePinCode(card, pinRequest);

        Integer pinCounter = card.getPinCounter();

        if (pinCounter < 3) {
            card.setPinCounter(pinCounter + 1);
            card.setPinCode(pinRequest.getNewPin());
            return cardRepository.save(card);
        } else {
            throw new ChangePinException();
        }
    }

    @Override
    @Scheduled(cron = "${droppincounter.cron.expression}")
    public void dropPinCounter() {
        cardRepository.dropPinCounter();
    }

    @Override
    public @NonNull Card releaseCard(@NonNull Long accountId, @NonNull CardPlan plan, long userId) {

        Account account = accountRepository.findAccountByIdWithUser(accountId, userId)
            .orElseThrow(() -> new AccountNotFoundException(accountId));

        DomainLogicChecker.assertAccountIsNotClosed(account);
        DomainLogicChecker.assertAccountIsSuitableForCard(account);

        String pinCode = generatePinCode();
        String number;
        // TODO: a potentially infinite loop
        do {
            number = generateCardNumber();
        } while (cardRepository.findByNumber(number).isPresent());

        Card card = new Card(account, number, pinCode, plan, false, ZonedDateTime.now().plusYears(3));
        return cardRepository.save(card);
    }

    @Override
    public @NonNull Card blockCard(@Positive Long cardId, long userId) {
        Card card = this.cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException(cardId));

        checkIfCardBelongsToUser(card, userId);
        DomainLogicChecker.assertAccountIsNotClosed(card.getAccount());

        card.setBlocked(true);
        return cardRepository.save(card);
    }

    private void checkIfCardBelongsToUser(Card card, long userId) {
        if (card.getAccount().getUser().getId() != userId) {
            throw new CardNotFoundException(card.getId());
        }
    }
}
