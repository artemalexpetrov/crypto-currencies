package crypto.investments.api.persistency;

import crypto.investments.api.persistency.dao.CurrencyInfoDao;
import crypto.investments.api.persistency.model.CurrencyInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CurrencyInfoRepositoryImpl implements CurrencyInfoRepository {

    private final CurrencyInfoDao currencyInfoDao;

    @Override
    @Cacheable(cacheNames = "currency", key = "#symbol")
    public boolean currencyExists(String symbol) {
        return currencyInfoDao.currencyExists(symbol);
    }

    @Override
    @Cacheable(cacheNames = "currency_info", key = "#symbol")
    public Optional<CurrencyInfo> getCurrencyInfo(String symbol) {
        Assert.notNull(symbol, "Currency symbol must not be null.");
        CurrencyInfo currencyInfo = currencyInfoDao.getCurrencyInfo(symbol);
        return Optional.ofNullable(currencyInfo);
    }

    @Override
    @Cacheable(cacheNames = "currency_info")
    public List<CurrencyInfo> getAllCurrencyInfo() {
        return currencyInfoDao.getAllCurrencyInfo();
    }

    @Override
    @Cacheable(cacheNames = "currency_info", key = "#date.toString()")
    public Optional<CurrencyInfo> getBestCurrencyForDate(LocalDate date) {
        Assert.notNull(date, "date must not be null.");
        CurrencyInfo bestCurrencyForDate = currencyInfoDao.getBestCurrencyForDate(date);
        return Optional.ofNullable(bestCurrencyForDate);
    }
}
