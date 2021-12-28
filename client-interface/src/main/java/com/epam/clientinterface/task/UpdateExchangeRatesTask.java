package com.epam.clientinterface.task;

import com.epam.clientinterface.entity.Currency;
import com.epam.clientinterface.entity.ExchangeRate;
import com.epam.clientinterface.repository.ExchangeRateRepository;
import java.util.ArrayList;
import java.util.List;
import javax.money.Monetary;
import javax.money.convert.MonetaryConversions;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UpdateExchangeRatesTask {
    private final ExchangeRateRepository exchangeRateRepository;

    @Scheduled(cron = "${app.task.update-exchange-rates.cron}", zone = "${app.task.zone}")
    public void execute() {
        this.updateExchangeRates(this.gatherExchangeRates());
    }

    private List<javax.money.convert.ExchangeRate> gatherExchangeRates() {
        var exchangeRates = new ArrayList<javax.money.convert.ExchangeRate>();
        for (var currencyFrom : Currency.values()) {
            var currItem = Monetary.getDefaultAmountFactory().setCurrency(currencyFrom.name())
                .setNumber(1).create();
            for (var currencyTo : Currency.values()) {
                if (currencyFrom.equals(currencyTo)) {
                    continue;
                }
                exchangeRates.add(MonetaryConversions.getConversion(currencyTo.name()).getExchangeRate(currItem));
            }
        }

        return exchangeRates;
    }

    @Transactional
    void updateExchangeRates(List<javax.money.convert.ExchangeRate> exchangeRates) {
        var exchangeRateEntities = new ArrayList<ExchangeRate>();
        for (var exchangeRate : exchangeRates) {
            var currencyFrom = Currency.valueOf(exchangeRate.getBaseCurrency().getCurrencyCode());
            var currencyTo = Currency.valueOf(exchangeRate.getCurrency().getCurrencyCode());

            exchangeRateEntities.add(this.exchangeRateRepository.findExchangeRateByCurrencyFromAndCurrencyTo(
                currencyFrom, currencyTo
            ).orElseGet(() -> new ExchangeRate(currencyFrom, currencyTo, exchangeRate.getFactor().doubleValue())));
        }

        this.exchangeRateRepository.saveAllAndFlush(exchangeRateEntities);
    }
}
