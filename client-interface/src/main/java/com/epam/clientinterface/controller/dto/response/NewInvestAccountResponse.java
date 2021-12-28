package com.epam.clientinterface.controller.dto.response;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewInvestAccountResponse {
    private int status;
    private String description;
    private String number;
    private String startInvest;
    private String endInvest;

    public NewInvestAccountResponse(HttpStatus status, String description, String number, ZonedDateTime startInvest,
                                    ZonedDateTime endInvest) {
        this.status = status.value();
        this.description = description;
        this.number = number;
        this.startInvest = DateTimeFormatter.ISO_DATE_TIME.format(startInvest);
        this.endInvest = DateTimeFormatter.ISO_DATE_TIME.format(endInvest);
    }
}
