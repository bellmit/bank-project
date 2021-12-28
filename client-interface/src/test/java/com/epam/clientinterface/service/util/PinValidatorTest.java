package com.epam.clientinterface.service.util;

import com.epam.clientinterface.controller.dto.request.ChangePinRequest;
import com.epam.clientinterface.domain.exception.IncorrectPinException;
import com.epam.clientinterface.entity.Account;
import com.epam.clientinterface.entity.Card;
import com.epam.clientinterface.enumerated.CardPlan;
import java.time.ZonedDateTime;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PinValidatorTest {
    private ChangePinRequest testChangePinRequest;
    private Card testCard;

    @BeforeEach
    public void beforeEach() {
        testChangePinRequest = new ChangePinRequest(RandomUtils.nextLong(), "2222");
        testCard = new Card(new Account(), RandomStringUtils.random(10), "1111",
            CardPlan.BASE, false, ZonedDateTime.now());
    }

    @Test
    public void differentPins() {
        testChangePinRequest.setNewPin("1111");
        IncorrectPinException thrownException = Assertions.assertThrows(IncorrectPinException.class,
            () -> PinValidator.validatePinCode(testCard, testChangePinRequest));

        Assertions.assertEquals("Pin is incorrect: New pin must be different", thrownException.getMessage());
    }

    @Test
    public void pinWithNumbersOnly() {
        testChangePinRequest.setNewPin("a111");
        IncorrectPinException thrownException = Assertions.assertThrows(IncorrectPinException.class,
            () -> PinValidator.validatePinCode(testCard, testChangePinRequest));

        Assertions.assertEquals("Pin is incorrect: Pin must contains four numbers only", thrownException.getMessage());
    }

    @Test
    public void pinWith4Numbers() {
        testChangePinRequest.setNewPin("111");
        IncorrectPinException thrownException = Assertions.assertThrows(IncorrectPinException.class,
            () -> PinValidator.validatePinCode(testCard, testChangePinRequest));

        Assertions.assertEquals("Pin is incorrect: Pin must contains four numbers only", thrownException.getMessage());
    }

    @Test
    public void pinWith5Numbers() {
        testChangePinRequest.setNewPin("11111");
        IncorrectPinException thrownException = Assertions.assertThrows(IncorrectPinException.class,
            () -> PinValidator.validatePinCode(testCard, testChangePinRequest));

        Assertions.assertEquals("Pin is incorrect: Pin must contains four numbers only", thrownException.getMessage());
    }
}