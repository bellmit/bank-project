package com.epam.clientinterface.repository;

import com.epam.clientinterface.entity.Currency;
import com.epam.clientinterface.entity.ExchangeRate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Short> {

    Optional<ExchangeRate> findExchangeRateByCurrencyFromAndCurrencyTo(Currency currencyFrom, Currency currencyTo);

    @Query("select er from ExchangeRate er where er.currencyFrom = ?1 and er.currencyTo = ?2")
    Optional<ExchangeRate> findOneByCurrencies(Currency currencyFrom, Currency currencyTo);

    List<ExchangeRate> getExchangeRatesByCurrencyFrom(Currency currencyFrom);
}
