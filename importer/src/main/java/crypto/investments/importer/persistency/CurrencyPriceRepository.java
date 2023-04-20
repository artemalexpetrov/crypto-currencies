package crypto.investments.importer.persistency;

import crypto.investments.importer.model.CurrencyPrice;

/**
 * Repository responsible for persistence of currency prices
 */
public interface CurrencyPriceRepository {

    /**
     * Saves all the given prices
     * @param prices must not be {@code null} nor must contain {@code null}
     */
    void saveAll(Iterable<? extends CurrencyPrice> prices);
}
