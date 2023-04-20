package crypto.investments.importer.persistency.dao;

import crypto.investments.importer.it.AbstractIntegrationTest;
import crypto.investments.importer.model.CurrencyPrice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class JdbcCurrencyPriceDaoIT extends AbstractIntegrationTest {

    private static final String SELECT_QUERY = "SELECT * FROM currency_prices";

    @Autowired
    private JdbcCurrencyPriceDao priceDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void insertBatch() {
        // given
        CurrencyPrice btcPrice = createCurrencyPrice("BTC", BigDecimal.TEN);
        CurrencyPrice ltcPrice = createCurrencyPrice("LTC", BigDecimal.ONE);
        List<CurrencyPrice> givenPrices = List.of(btcPrice, ltcPrice);

        // when
        priceDao.insertBatch(givenPrices);

        // then
        List<CurrencyPrice> fetchedPrices = fetchPrices();

        assertThat(fetchedPrices).hasSize(givenPrices.size());
    }

    private List<CurrencyPrice> fetchPrices() {
        return jdbcTemplate.query(SELECT_QUERY, (rs, i) -> CurrencyPrice.builder()
            .timestamp(rs.getTimestamp("timestamp").toLocalDateTime())
            .price(rs.getBigDecimal("price"))
            .symbol(rs.getString("symbol"))
            .build());
    }

    private CurrencyPrice createCurrencyPrice(String symbol, BigDecimal price) {
        return CurrencyPrice.builder()
            .timestamp(LocalDateTime.now())
            .symbol(symbol)
            .price(price)
            .build();
    }
}