package com.epam.clientinterface.controller;

import static com.epam.clientinterface.util.TestDataFactory.getInvestAccount;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epam.clientinterface.controller.advice.ErrorHandlingAdvice;
import com.epam.clientinterface.domain.exception.UserNotFoundException;
import com.epam.clientinterface.entity.Account;
import com.epam.clientinterface.enumerated.AccountType;
import com.epam.clientinterface.service.AccountService;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class AccountControllerCreateInvestAccountTest {

    private final Account investAccount = getInvestAccount(ZonedDateTime.now(),
        ZonedDateTime.now().plusYears(3));

    private final String requestBody = String.format("{"
            + "\"userId\":\"%d\", "
            + "\"type\":\"%s\", "
            + "\"amount\":\"%d\", "
            + "\"period\":\"%d\"}",
        1, "INVEST", 1000, 5);

    private MockMvc mockMvc;

    @Mock
    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AccountController(accountService))
            .setControllerAdvice(ErrorHandlingAdvice.class)
            .build();
    }

    @Test
    void shouldReturnIsCreatedIfRequestIsValid() throws Exception {
        when(accountService.createInvestAccount(anyLong(), any(AccountType.class), anyDouble(), anyInt()))
            .thenReturn(investAccount);

        mockMvc.perform(post("/accounts/invest")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)).andExpect(status().isCreated());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "{\"userId\": \"-1\", \"typ\":\"INVEST\", \"amount\":\"1000\", \"period\":\"5\"}",
    })
    public void shouldReturnValidationErrorResponseIfRequestIsIncorrect(String requestBody) throws Exception {
        mockMvc.perform(post("/accounts/invest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.type", is("validation")))
            .andExpect(jsonPath("$.status", is(422)))
            .andExpect(jsonPath("$.errors[*].field",
                containsInAnyOrder("userId", "type")
            ))
            .andExpect(jsonPath("$.errors[?(@.field=='userId')].error", hasItem("must be greater than 0")))
            .andExpect(jsonPath("$.errors[?(@.field=='type')].error", hasItem("must not be null")));
    }

    @Test
    public void shouldReturnBadRequestIfRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/accounts/invest")
            .contentType(MediaType.APPLICATION_JSON)
            .content("")).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnNotFoundIfServiceThrowsUserNotFound() throws Exception {
        Mockito.doThrow(UserNotFoundException.class)
            .when(accountService)
            .createInvestAccount(anyLong(), any(AccountType.class), anyDouble(), anyInt());

        mockMvc.perform(post("/accounts/invest")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)).andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnUnsupportedMediaTypeIfContentTypeIsNotJson() throws Exception {
        mockMvc.perform(post("/accounts/invest")
            .contentType(MediaType.TEXT_HTML)
            .content("")).andExpect(status().isUnsupportedMediaType());
    }
}
