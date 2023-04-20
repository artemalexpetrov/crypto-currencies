package crypto.investments.importer.service;

import crypto.investments.importer.exception.ImportResourcesDiscoveryException;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

/**
 * A strategy to discover resources to import
 */
public interface ImportResourceDiscoverer {

    /**
     * Returns list of discovered resources
     * TODO: not sure if custom exception is needed here
     */
    List<Resource> discover() throws ImportResourcesDiscoveryException;
}
