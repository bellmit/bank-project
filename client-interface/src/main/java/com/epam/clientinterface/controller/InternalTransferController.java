package com.epam.clientinterface.controller;

import com.epam.clientinterface.controller.dto.request.InternalTransferRequest;
import com.epam.clientinterface.service.AccountService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InternalTransferController {
    private final AccountService accountService;

    @PostMapping("/transfer/internal")
    public ResponseEntity<?> internalTransfer(@Valid @RequestBody InternalTransferRequest request) {
        this.accountService.transfer(
            request.getSourceAccountId(),
            request.getDestinationAccountId(),
            request.getAmount()
        );

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}