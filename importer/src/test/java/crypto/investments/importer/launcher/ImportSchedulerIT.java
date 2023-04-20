package crypto.investments.importer.launcher;

import crypto.investments.importer.config.ImportProperties;
import crypto.investments.importer.it.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeastOnce;

class ImportSchedulerIT extends AbstractIntegrationTest {

    public static final int SCHEDULE_TASK_AWAIT_GAP_SECONDS = 5;

    @SpyBean
    private ImportScheduler scheduler;

    @Autowired
    private ImportProperties importProperties;

    @Test
    void importCurrencyValues() {
        // given
        Duration pollingRate = Duration.ofMillis(importProperties.getPollingRate());
        Duration awaitTimeout = pollingRate.plusSeconds(SCHEDULE_TASK_AWAIT_GAP_SECONDS);

        // then
        await()
            .atMost(awaitTimeout)
            .untilAsserted(() -> Mockito.verify(scheduler, atLeastOnce()).importCurrencyPrices());
    }
}