package crypto.investments.importer.launcher;

import crypto.investments.importer.exception.ImportResourcesDiscoveryException;
import crypto.investments.importer.service.ImportResourceDiscoverer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * A component that starts import job periodically
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ImportScheduler {

    private final ImportJobLauncher jobLauncher;
    private final ImportResourceDiscoverer resourcesDiscoverer;

    @Scheduled(fixedDelayString = "${import.polling-rate:10000}")
    public void importCurrencyPrices() {
        try {
            log.debug("Scheduled import started");
            List<Resource> resources = resourcesDiscoverer.discover();
            startJobForResources(resources);
        } catch (ImportResourcesDiscoveryException e) {
            log.error("Unable to start importing job", e);
        }
    }

    private void startJobForResources(Collection<Resource> resources) {
        for (Resource file : resources) {
            startJobForFile(file);
        }
    }

    private void startJobForFile(Resource resource) {
        try {
            log.info("Starting import job for the file " + resource.getDescription());
            jobLauncher.launch(resource);
        } catch (JobExecutionException e) {
            log.error("Unable to start job for the file " + resource.getDescription(), e);
        }
    }
}
