package crypto.investments.importer.persistency;

import crypto.investments.importer.model.CurrencyPrice;
import crypto.investments.importer.persistency.dao.CurrencyPriceDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


@Slf4j
@Repository
@Transactional
@RequiredArgsConstructor
public class CurrencyPriceRepositoryImpl implements CurrencyPriceRepository {

    private final CurrencyPriceDao priceDao;

    @Override
    public void saveAll(Iterable<? extends CurrencyPrice> prices) {
        Assert.notNull(prices, "prices must not be null");
        priceDao.insertBatch(prices);
    }
}
