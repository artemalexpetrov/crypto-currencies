package crypto.investments.api.persistency.dao;

import crypto.investments.api.persistency.model.CurrencyInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JdbcCurrencyInfoDao implements CurrencyInfoDao {

    private static final String COLUMN_SYMBOL = "symbol";
    private static final String COLUMN_NORMALIZED_RANGE = "normalized_range";
    private static final String COLUMN_OLDEST_PRICE = "oldest_price";
    private static final String COLUMN_NEWEST_PRICE = "newest_price";
    private static final String COLUMN_MIN_PRICE = "min_price";
    private static final String COLUMN_MAX_PRICE = "max_price";

    private static final String CURRENCY_EXISTS_QUERY
        = "SELECT EXISTS(SELECT symbol FROM currency_prices WHERE symbol=:symbol)::int";

    private static final String CURRENCY_INFO_QUERY
        = "SELECT * FROM currency_price_monthly WHERE symbol=:symbol ORDER BY bucket_monthly DESC LIMIT 1";

    private static final String ALL_CURRENCIES_QUERY = """
        SELECT * FROM (SELECT DISTINCT ON (symbol) * FROM currency_price_monthly
        ORDER BY symbol, bucket_monthly DESC, normalized_range DESC) last_bucket
        ORDER BY last_bucket.normalized_range DESC""";

    private static final String BEST_CURRENCY_FOR_DATE_QUERY
        = "SELECT * FROM currency_price_daily WHERE DATE(bucket_daily)=:date ORDER BY normalized_range DESC LIMIT 1";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public boolean currencyExists(String symbol) {
        Map<String, String> parameters = Map.of(COLUMN_SYMBOL, symbol);
        Boolean result = jdbcTemplate.queryForObject(CURRENCY_EXISTS_QUERY, parameters, Boolean.class);
        return result != null && result;
    }

    @Override
    public CurrencyInfo getCurrencyInfo(String symbol) {
        Map<String, String> parameters = Map.of(COLUMN_SYMBOL, symbol);
        List<CurrencyInfo> result = jdbcTemplate.query(CURRENCY_INFO_QUERY, parameters, this::createInfoFromResultSet);
        return DataAccessUtils.uniqueResult(result);
    }

    @Override
    public List<CurrencyInfo> getAllCurrencyInfo() {
        return jdbcTemplate.query(ALL_CURRENCIES_QUERY, this::createInfoFromResultSet);
    }

    @Override
    public CurrencyInfo getBestCurrencyForDate(LocalDate date) {
        Map<String, Object> parameters = Map.of("date", date);
        List<CurrencyInfo> result = jdbcTemplate.query(BEST_CURRENCY_FOR_DATE_QUERY, parameters, this::createInfoFromResultSet);

        return DataAccessUtils.uniqueResult(result);
    }

    private CurrencyInfo createInfoFromResultSet(ResultSet rs, int rowIndex) throws SQLException {
        return CurrencyInfo.builder()
            .symbol(rs.getString(COLUMN_SYMBOL))
            .normalizedRange(rs.getBigDecimal(COLUMN_NORMALIZED_RANGE))
            .oldestPrice(rs.getBigDecimal(COLUMN_OLDEST_PRICE))
            .newestPrice(rs.getBigDecimal(COLUMN_NEWEST_PRICE))
            .minPrice(rs.getBigDecimal(COLUMN_MIN_PRICE))
            .maxPrice(rs.getBigDecimal(COLUMN_MAX_PRICE))
            .build();
    }
}
