package crypto.investments.api.persistency;

import crypto.investments.api.persistency.model.CurrencyInfo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository responsible for currency information
 */
public interface CurrencyInfoRepository {

    /**
     * Checks if the currency identifies for the specified {@code symbol} exists
     */
    boolean currencyExists(String symbol);

    /**
     * Fetches {@link CurrencyInfo} by the given {@code symbol}
     * from a persistent storage for the latest month
     *
     * @return {@link Optional#empty()} if currency with the specified code was not found
     */
    Optional<CurrencyInfo> getCurrencyInfo(String symbol);

    /**
     * Fetches {@link CurrencyInfo} for all known currencies for the latest month
     *
     * @return empty list is there is no data
     */
    List<CurrencyInfo> getAllCurrencyInfo();

    /**
     * Fetches {@link CurrencyInfo} with the max normalized range
     * for the specified date
     *
     * @return {@link Optional#empty()} if currency with the specified date
     */
    Optional<CurrencyInfo> getBestCurrencyForDate(LocalDate date);
}
