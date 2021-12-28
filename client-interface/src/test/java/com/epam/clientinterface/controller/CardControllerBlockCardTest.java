package com.epam.clientinterface.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epam.clientinterface.domain.exception.CardNotFoundException;
import com.epam.clientinterface.entity.Card;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

@ExtendWith(MockitoExtension.class)
public class CardControllerBlockCardTest extends AbstractControllerTest {

    private final String uri = "/card/%d/blockCard";

    @Test
    public void shouldReturnIsOkIfRequestIsValid() throws Exception {
        when(super.cardServiceMock.blockCard(anyLong(), anyLong())).thenReturn(new Card());

        send(MediaType.APPLICATION_JSON, "", HttpMethod.PATCH, String.format(uri, 1))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnNotFoundIfServiceThrowsCardNotFound() throws Exception {
        doThrow(new CardNotFoundException(1L))
            .when(this.cardServiceMock)
            .blockCard(anyLong(), anyLong());

        send(MediaType.APPLICATION_JSON, "", HttpMethod.PATCH, String.format(uri, 1))
            .andExpect(status().isNotFound());
    }
}
