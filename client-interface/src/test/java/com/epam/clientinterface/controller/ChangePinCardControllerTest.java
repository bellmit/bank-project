package com.epam.clientinterface.controller;

import com.epam.clientinterface.controller.advice.CustomErrorHandler;
import com.epam.clientinterface.controller.dto.request.ChangePinRequest;
import com.epam.clientinterface.controller.util.JsonHelper;
import com.epam.clientinterface.entity.Card;
import com.epam.clientinterface.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ChangePinCardControllerTest {
    private static final String CHANGEPASSWORD = "/change-password";
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CardService cardService;

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(new CardController(cardService))
            .setControllerAdvice(CustomErrorHandler.class)
            .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void test() throws Exception {
        ChangePinRequest pinRequest = new ChangePinRequest(1L, "1234", "1235");
        Mockito.when(cardService.changePinCode(pinRequest)).thenReturn(new Card());

        mockMvc.perform(MockMvcRequestBuilders.post(CHANGEPASSWORD).contentType(MediaType.APPLICATION_JSON)
                .content(JsonHelper.toJson(objectMapper, pinRequest)))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }
}