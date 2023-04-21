package crypto.investments.importer.service;

import crypto.investments.importer.exception.ImportResourcesDiscoveryException;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * A strategy to discover resources to import
 */
public interface ImportResourceDiscoverer {

    /**
     * Returns list of discovered resources
     */
    List<Resource> discover() throws ImportResourcesDiscoveryException;
}
