package crypto.investments.api.persistency.dao;

import crypto.investments.api.persistency.model.CurrencyInfo;

import java.time.LocalDate;
import java.util.List;

/**
 * Data-Access Object for currency info
 */
public interface CurrencyInfoDao {

    /**
     * Checks if the currency identifies for the specified {@code symbol} exists
     */
    boolean currencyExists(String symbol);

    /**
     * Fetches {@link CurrencyInfo} by the given {@code symbol}
     * from a persistent storage for the latest month
     *
     * @return null if {@link CurrencyInfo} with the specified symbol was not found
     */
    CurrencyInfo getCurrencyInfo(String symbol);

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
     * @return {@code null} if there is no data for the specified {@code date}
     */
    CurrencyInfo getBestCurrencyForDate(LocalDate date);
}
