package crypto.investments.api.service;

import crypto.investments.api.exception.CurrencyDoesntExistException;
import crypto.investments.api.exception.InsufficientCurrencyDataException;
import crypto.investments.api.model.BestCurrencyInfoRequest;
import crypto.investments.api.model.CurrencyInfoDto;
import crypto.investments.api.model.CurrencyInfoRequest;

import java.util.List;

/**
 *
 */
public interface CurrencyInfoService {

    /**
     * Returns {@link CurrencyInfoDto} for the currency specified in {@code request}.
     * By default, returns info for a last known month period.
     *
     * @throws CurrencyDoesntExistException if currency doesn't exist
     * @throws InsufficientCurrencyDataException if there are no sufficient data to calculate info for currency
     */
    CurrencyInfoDto getCurrencyInfo(CurrencyInfoRequest request);

    /**
     * Returns {@link CurrencyInfoDto} for each known currency.
     * By default, the info is calculated for the last known month period,
     * and the list is sorted by normalized price from the greatest to lowest.
     */
    List<CurrencyInfoDto> getAllCurrencies();

    /**
     * Returns {@link CurrencyInfoDto} for the best currency for the date specified in {@code request}
     * The best currency is a currency with a max normalized price.
     *
     * @throws InsufficientCurrencyDataException if there are no sufficient data to find best currency for the specified period
     */
    CurrencyInfoDto getBestCurrencyForDate(BestCurrencyInfoRequest request);
}
