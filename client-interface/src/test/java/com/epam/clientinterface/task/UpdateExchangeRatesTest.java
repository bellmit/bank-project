package com.epam.clientinterface.task;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epam.clientinterface.entity.Currency;
import com.epam.clientinterface.entity.ExchangeRate;
import com.epam.clientinterface.repository.ExchangeRateRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.NumberValue;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UpdateExchangeRatesTest {
    @Mock
    private ExchangeRateRepository exchangeRateRepositoryMock;

    @InjectMocks
    private UpdateExchangeRatesTask task;

    @Captor
    private ArgumentCaptor<List<ExchangeRate>> exchangeRates;

    @BeforeAll
    public static void beforeAll() {
        var monetaryConversionsMock = Mockito.mockStatic(MonetaryConversions.class);

        Arrays.stream(Currency.values()).forEach(currency -> {
            var currencyUnitMock = Mockito.mock(CurrencyUnit.class);
            when(currencyUnitMock.getCurrencyCode()).thenReturn(currency.name());
            var currencyConversionMock = mockCurrencyConversionMock(currencyUnitMock);

            monetaryConversionsMock.when(() -> MonetaryConversions.getConversion(currency.name()))
                .thenReturn(currencyConversionMock);
        });
    }

    private static CurrencyConversion mockCurrencyConversionMock(CurrencyUnit currencyUnitMock) {
        var currencyConversionMock = Mockito.mock(CurrencyConversion.class);
        when(currencyConversionMock.getExchangeRate(Mockito.any(MonetaryAmount.class))).then(invocation -> {
            var monetary = (MonetaryAmount) invocation.getArgument(0);
            var exchangeRateMock = Mockito.mock(javax.money.convert.ExchangeRate.class);
            var numberValueMock = Mockito.mock(NumberValue.class);
            lenient().when(numberValueMock.doubleValue()).thenReturn(RandomUtils.nextDouble());
            when(exchangeRateMock.getBaseCurrency()).thenReturn(currencyUnitMock);
            when(exchangeRateMock.getCurrency()).thenReturn(monetary.getCurrency());
            lenient().when(exchangeRateMock.getFactor()).thenReturn(numberValueMock);

            return exchangeRateMock;
        });

        return currencyConversionMock;
    }

    @Test
    public void shouldCollectAllCombinationsOfCurrenciesAndSaveThemWithEmptyRepository() {
        task.execute();

        verify(exchangeRateRepositoryMock).saveAllAndFlush(exchangeRates.capture());

        Assertions.assertEquals(
            Currency.values().length * (Currency.values().length - 1),
            exchangeRates.getValue().size()
        );
    }

    @Test
    public void shouldCollectAllCombinationsOfCurrenciesAndSaveThemWithFullRepository() {
        for (var currencyA : Currency.values()) {
            for (var currencyB : Currency.values()) {
                if (currencyA.equals(currencyB)) {
                    continue;
                }

                when(exchangeRateRepositoryMock.findExchangeRateByCurrencyFromAndCurrencyTo(currencyA, currencyB))
                    .thenReturn(Optional.of(new ExchangeRate((short) 1L, currencyA, currencyB,
                        RandomUtils.nextDouble())));
            }
        }

        task.execute();

        verify(exchangeRateRepositoryMock).saveAllAndFlush(exchangeRates.capture());

        Assertions.assertEquals(
            Currency.values().length * (Currency.values().length - 1),
            exchangeRates.getValue().size()
        );
    }
}
