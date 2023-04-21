package crypto.investments.api.persistency;

import crypto.investments.api.it.AbstractIntegrationTest;
import crypto.investments.api.persistency.dao.CurrencyInfoDao;
import crypto.investments.api.persistency.model.CurrencyInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class CurrencyInfoRepositoryImplIT extends AbstractIntegrationTest {

    private static final String CURRENCY_CACHE_NAME = "currency";
    private static final String CURRENCY_INFO_CACHE_NAME = "currency_info";

    @Autowired
    private CacheManager cacheManager;

    @SpyBean
    private CurrencyInfoDao daoSpy;

    @Autowired
    private CurrencyInfoRepository repository;

    @BeforeEach
    void setUp() {
        resetCache(CURRENCY_CACHE_NAME);
        resetCache(CURRENCY_INFO_CACHE_NAME);
    }

    @Test
    void testCurrencyExistsCaching_expectValueCached() {
        // given
        String btcSymbol = "BTC";

        // when
        assertCurrencyCacheNotExist(btcSymbol);
        boolean btcExist = repository.currencyExists(btcSymbol);

        // then
        Cache.ValueWrapper btcExistsCache = getCachedCurrencyValue(btcSymbol);
        assertEquals(btcExist, btcExistsCache.get());
    }

    @Test
    void testCurrencyExists_expectCachedValueOnConsequenceCalls() {
        // given
        String btcSymbol = "BTC";
        assertCurrencyCacheNotExist(btcSymbol);

        // when
        repository.currencyExists(btcSymbol);
        repository.currencyExists(btcSymbol);

        // then
        verify(daoSpy, Mockito.times(1)).currencyExists(btcSymbol);
        verifyNoMoreInteractions(daoSpy);
    }

    @Test
    void testGetCurrencyInfo_expectValueCached() {
        // given
        String btcSymbol = "BTC";

        // when
        assertCurrencyInfoCacheNotExist(btcSymbol);
        Optional<CurrencyInfo> btcInfo = repository.getCurrencyInfo(btcSymbol);

        // then
        Cache.ValueWrapper btcInfoCache = getCachedCurrencyInfoValue(btcSymbol);

        assertThat(btcInfo).isPresent();
        assertThat(btcInfoCache.get())
            .isInstanceOf(CurrencyInfo.class)
            .isEqualTo(btcInfo.get());
    }

    @Test
    void testGetCurrencyInfo_expectCachedValueOnConsequenceCalls() {
        // given
        String btcSymbol = "BTC";
        assertCurrencyInfoCacheNotExist(btcSymbol);

        // when
        repository.getCurrencyInfo(btcSymbol);
        repository.getCurrencyInfo(btcSymbol);

        // then
        verify(daoSpy, Mockito.times(1)).getCurrencyInfo(btcSymbol);
        verifyNoMoreInteractions(daoSpy);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testGetAllCurrencyInfo_expectValueCached() {
        // given

        // when
        List<CurrencyInfo> allCurrencies = repository.getAllCurrencyInfo();

        // then
        Cache.ValueWrapper allCurrenciesCachedValue = getCachedCurrencyInfoValue(new SimpleKey());
        assertThat(allCurrenciesCachedValue).isNotNull();

        List<CurrencyInfo> allCurrenciesCached = (List<CurrencyInfo>) allCurrenciesCachedValue.get();
        assertThat(allCurrenciesCached)
            .hasSize(allCurrencies.size())
            .containsAll(allCurrencies);

    }

    @Test
    void testGetBestCurrencyForDate_expectValueCached() {
        // given
        LocalDate date = LocalDate.of(2022, 1, 1);
        assertCurrencyInfoCacheNotExist(date);

        // when
        Optional<CurrencyInfo> bestCurrencyInfo = repository.getBestCurrencyForDate(date);

        // then
        Cache.ValueWrapper bestCurrencyInfoCache = getCachedCurrencyInfoValue(date);

        assertThat(bestCurrencyInfo).isPresent();
        assertThat(bestCurrencyInfoCache.get())
            .isInstanceOf(CurrencyInfo.class)
            .isEqualTo(bestCurrencyInfo.get());
    }

    @Test
    void testGetBestCurrencyForDate_expectCachedValueOnConsequenceCalls() {
        // given
        LocalDate date = LocalDate.of(2022, 1, 1);
        assertCurrencyInfoCacheNotExist(date);

        // when
        repository.getBestCurrencyForDate(date);
        repository.getBestCurrencyForDate(date);

        // then
        verify(daoSpy, Mockito.times(1)).getBestCurrencyForDate(date);
        verifyNoMoreInteractions(daoSpy);
    }


    private void assertCurrencyCacheNotExist(Object key) {
        assertCacheNotExist(CURRENCY_CACHE_NAME, key);
    }

    private void assertCurrencyInfoCacheNotExist(Object key) {
        assertCacheNotExist(CURRENCY_INFO_CACHE_NAME, key);
    }

    private void assertCacheNotExist(String cacheName, Object key) {
        Cache currenciesCache = getCache(cacheName);
        assertNull(currenciesCache.get(key.toString()));
    }

    private Cache.ValueWrapper getCachedCurrencyValue(Object key) {
        return getCachedValue(CURRENCY_CACHE_NAME, key);
    }

    private Cache.ValueWrapper getCachedCurrencyInfoValue(Object key) {
        return getCachedValue(CURRENCY_INFO_CACHE_NAME, key);
    }

    private Cache.ValueWrapper getCachedValue(String cacheName, Object key) {
        Cache currenciesCache = getCache(cacheName);
        return currenciesCache.get(key.toString());
    }

    private void resetCache(String cacheName) {
        Cache currenciesCache = getCache(cacheName);
        currenciesCache.clear();
    }

    private Cache getCache(String cacheName) {
        Cache currenciesCache = cacheManager.getCache(cacheName);
        assertNotNull(currenciesCache);
        return currenciesCache;
    }
}