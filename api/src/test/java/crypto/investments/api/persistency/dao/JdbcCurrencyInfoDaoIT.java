package crypto.investments.api.persistency.dao;

import crypto.investments.api.it.AbstractIntegrationTest;
import crypto.investments.api.persistency.model.CurrencyInfo;
import org.assertj.core.util.BigDecimalComparator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


class JdbcCurrencyInfoDaoIT extends AbstractIntegrationTest {

    private static final List<String> CURRENCIES_SYMBOLS = List.of("BTC", "DOGE", "ETH", "LTC", "XRP");

    @Autowired
    private JdbcCurrencyInfoDao currencyInfoDao;

    @Test
    void testCurrencyExists_whenCurrencyExists_expectTrue() {
        boolean btcExists = currencyInfoDao.currencyExists("BTC");
        assertTrue(btcExists);
    }

    @Test
    void testCurrencyExists_whenCurrencyDoesntExist_expectFalse() {
        boolean btcExists = currencyInfoDao.currencyExists("QTC1");
        assertFalse(btcExists);
    }

    @Test
    void testGetCurrencyInfo_expectDataForTheLastMonthReturned() {
        // when
        CurrencyInfo currencyInfo = currencyInfoDao.getCurrencyInfo("BTC");

        // then
        assertThat(currencyInfo).isNotNull();
        assertEquals("BTC", currencyInfo.getSymbol());
        assertEquals(new BigDecimal("46813.2100"), currencyInfo.getOldestPrice());
        assertEquals(new BigDecimal("38415.7900"), currencyInfo.getNewestPrice());
        assertEquals(new BigDecimal("33276.5900"), currencyInfo.getMinPrice());
        assertEquals(new BigDecimal("47722.6600"), currencyInfo.getMaxPrice());
        assertEquals(new BigDecimal("0.4341"), currencyInfo.getNormalizedRange());
    }

    @Test
    void testGetCurrencyInfo_whenCurrencyDoesntExist_expectNull() {
        CurrencyInfo btcInfo = currencyInfoDao.getCurrencyInfo("QTC");
        assertNull(btcInfo);
    }

    @Test
    void testGetAllCurrencyInfo() {
        // given
        List<BigDecimal> expectedNormalizedRanges = List.of(
            new BigDecimal("0.6384"),
            new BigDecimal("0.5061"),
            new BigDecimal("0.5047"),
            new BigDecimal("0.4652"),
            new BigDecimal("0.4341")
        );

        // when
        List<CurrencyInfo> allCurrencies = currencyInfoDao.getAllCurrencyInfo();

        // then
        assertThat(allCurrencies)
            .hasSize(CURRENCIES_SYMBOLS.size())
            .map(CurrencyInfo::getNormalizedRange)
            .isSortedAccordingTo(new BigDecimalComparator().reversed())
            .containsExactlyElementsOf(expectedNormalizedRanges);
    }

    @Test
    void testGetAllCurrencyInfo_expectCurrenciesInCorrectlySortedOrder() {
        List<CurrencyInfo> allCurrencies = currencyInfoDao.getAllCurrencyInfo();
        assertThat(allCurrencies).hasSize(CURRENCIES_SYMBOLS.size());
    }

    @Test
    void getBestCurrencyForDate() {
        // given
        LocalDate date = LocalDate.of(2022, 1, 1);

        // when
        CurrencyInfo bestCurrency = currencyInfoDao.getBestCurrencyForDate(date);

        // then
        assertEquals("BTC", bestCurrency.getSymbol());
        assertEquals(new BigDecimal("0.0194"), bestCurrency.getNormalizedRange());
    }
}