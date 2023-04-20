package crypto.investments.importer.persistency.dao;

import crypto.investments.importer.model.CurrencyPrice;

/**
 * Data-Access Object for currency prices
 */
public interface CurrencyPriceDao {

    /**
     * Inserts prices in batch mode.
     *
     * @param prices must not be {@code null} nor must contain {@code null}
     */
    void insertBatch(Iterable<? extends CurrencyPrice> prices);
}
