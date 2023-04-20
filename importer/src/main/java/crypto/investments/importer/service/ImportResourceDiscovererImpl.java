package crypto.investments.importer.service;

import crypto.investments.importer.config.ImportProperties;
import crypto.investments.importer.exception.ImportResourcesDiscoveryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * A strategy to discover resources to import with {@link ResourcePatternResolver}
 * from location specified in {@link ImportProperties#getImportPattern()}
 */
@Slf4j
@Component
public class ImportResourceDiscovererImpl implements ImportResourceDiscoverer {

    private final ImportProperties importProperties;
    private final ResourcePatternResolver resourcesResolver;

    public ImportResourceDiscovererImpl(ImportProperties importProperties) {
        this.resourcesResolver = new PathMatchingResourcePatternResolver();
        this.importProperties = importProperties;
    }

    @Override
    public List<Resource> discover() throws ImportResourcesDiscoveryException {
        String location = importProperties.getImportPattern();
        return resolveResources(location);
    }

    private List<Resource> resolveResources(String location) throws ImportResourcesDiscoveryException {
        try {
            Resource[] resources = resourcesResolver.getResources(location);
            return Arrays.asList(resources);
        } catch (IOException e) {
            throw new ImportResourcesDiscoveryException("Unable to discover import resources", e);
        }
    }

}
