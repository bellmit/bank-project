package com.epam.clientinterface.controller;

import com.epam.clientinterface.configuration.security.SecurityUtil;
import com.epam.clientinterface.controller.dto.request.NewInvestAccountRequest;
import com.epam.clientinterface.controller.dto.response.NewInvestAccountResponse;
import com.epam.clientinterface.entity.Account;
import com.epam.clientinterface.service.AccountService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    @DeleteMapping("/{accountId}")
    public ResponseEntity<?> closeAccount(@PathVariable long accountId) {
        long userId = SecurityUtil.authUserId();
        this.accountService.closeAccount(accountId, userId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/invest")
    public ResponseEntity<Object> createInvestAccount(@Valid @RequestBody NewInvestAccountRequest request) {
        Account account = accountService.createInvestAccount(request.getUserId(), request.getType(),
            request.getAmount(), request.getPeriod());
        NewInvestAccountResponse response = new NewInvestAccountResponse(HttpStatus.CREATED,
            "InvestAccount has been created",
            account.getNumber(),
            account.getStartInvest(),
            account.getEndInvest());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
