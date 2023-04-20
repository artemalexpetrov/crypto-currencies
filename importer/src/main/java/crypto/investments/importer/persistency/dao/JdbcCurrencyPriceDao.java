package crypto.investments.importer.persistency.dao;

import crypto.investments.importer.model.CurrencyPrice;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.util.List;

/**
 * An implementation of {@link CurrencyPriceDao}
 * that utilized JdbcTemplate to perform database
 * operations
 */
@Component
@RequiredArgsConstructor
public class JdbcCurrencyPriceDao implements CurrencyPriceDao {

    private static final int INSERT_BATCH_SIZE = 10000;
    private static final String INSERT_QUERY = "INSERT INTO currency_prices(symbol, price, timestamp) VALUES(?, ?, ?)";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void insertBatch(Iterable<? extends CurrencyPrice> prices) {
        Assert.notNull(prices, "prices must not be null");

        List<? extends CurrencyPrice> batch = IterableUtils.toList(prices);
        jdbcTemplate.batchUpdate(INSERT_QUERY, batch, INSERT_BATCH_SIZE, (ps, currencyPrice) -> {
            ps.setString(1, currencyPrice.symbol());
            ps.setBigDecimal(2, currencyPrice.price());
            ps.setTimestamp(3, Timestamp.valueOf(currencyPrice.timestamp()));
        });
    }
}
