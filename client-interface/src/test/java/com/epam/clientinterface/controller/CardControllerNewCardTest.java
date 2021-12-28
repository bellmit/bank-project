package com.epam.clientinterface.controller;

import static com.epam.clientinterface.util.TestDataFactory.getCard;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epam.clientinterface.domain.exception.AccountIsNotSupposedForCard;
import com.epam.clientinterface.domain.exception.AccountNotFoundException;
import com.epam.clientinterface.entity.Card;
import com.epam.clientinterface.enumerated.CardPlan;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

@ExtendWith(MockitoExtension.class)
public class CardControllerNewCardTest extends AbstractControllerTest {

    private final String requestBody = String.format("{\"plan\":\"%s\"}", "BASE");
    private final String uri = "/account/%d/releaseCard";

    @Test
    void shouldReturnIsCreatedIfRequestIsValid() throws Exception {
        Card card = getCard();
        when(cardServiceMock.releaseCard(anyLong(), any(CardPlan.class), anyLong())).thenReturn(card);

        send(MediaType.APPLICATION_JSON, requestBody, HttpMethod.POST, String.format(uri, card.getId()))
            .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "{\"plan\":null}",
        "{}"
    })
    public void shouldReturnValidationErrorResponseIfRequestIsIncorrect(String requestBody) throws Exception {
        send(MediaType.APPLICATION_JSON, requestBody, HttpMethod.POST, String.format(uri, 1))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.type", is("validation")))
            .andExpect(jsonPath("$.status", is(422)))
            .andExpect(jsonPath(
                "$.errors[?(@.field=='plan')].error",
                containsInAnyOrder("must not be null")
            ));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "{\"plan\":\"BAS\"}",
        "{\"plan\":\"BASE\"",
        "{\"type\":\"BASE\"}",
        ""
    })
    public void shouldReturnBadRequestIfRequestIsInvalid(String requestBody) throws Exception {
        send(MediaType.APPLICATION_JSON, requestBody, HttpMethod.POST, String.format(uri, 1))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnNotFoundIfServiceThrowsAccountNotFound() throws Exception {
        Mockito.doThrow(AccountNotFoundException.class)
            .when(cardServiceMock)
            .releaseCard(anyLong(), any(CardPlan.class), anyLong());

        send(MediaType.APPLICATION_JSON, requestBody, HttpMethod.POST, String.format(uri, 2))
            .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnBadRequestIfServiceThrowsAccountIsNotSupposedForCard() throws Exception {
        Mockito.doThrow(AccountIsNotSupposedForCard.class)
            .when(cardServiceMock)
            .releaseCard(anyLong(), any(CardPlan.class), anyLong());

        send(MediaType.APPLICATION_JSON, requestBody, HttpMethod.POST, String.format(uri, 2))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnUnsupportedMediaTypeIfContentTypeIsNotJson() throws Exception {
        send(MediaType.TEXT_HTML, "", HttpMethod.POST, String.format(uri, 1))
            .andExpect(status().isUnsupportedMediaType());
    }
}
