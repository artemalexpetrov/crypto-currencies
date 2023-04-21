package crypto.investments.api.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Simplest cache eviction strategy possible.
 * In the real life application I'd rather implement
 * event-based mechanism to evict cache after import
 * TODO: implement something better
 */
@Slf4j
@Component
public class CacheEvictionScheduler {

    @Scheduled(fixedRateString = "${cache.eviction-rate}")
    @CacheEvict(cacheNames = {"currency", "currency_info"}, allEntries = true)
    public void evictCurrenciesCache() {
        log.info("Currencies cache eviction");
    }
}
