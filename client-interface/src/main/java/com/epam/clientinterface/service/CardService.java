package com.epam.clientinterface.service;

import com.epam.clientinterface.controller.dto.request.ChangePinRequest;
import com.epam.clientinterface.entity.Card;
import com.epam.clientinterface.enumerated.CardPlan;
import javax.validation.constraints.Positive;
import lombok.NonNull;
import org.springframework.scheduling.annotation.Scheduled;

public interface CardService {
    Card changePinCode(ChangePinRequest pinRequest);

    void dropPinCounter();

    @NonNull Card releaseCard(@NonNull Long accountId, @NonNull CardPlan plan, long userId);

    @NonNull Card blockCard(@Positive Long cardId, long userId);
}

