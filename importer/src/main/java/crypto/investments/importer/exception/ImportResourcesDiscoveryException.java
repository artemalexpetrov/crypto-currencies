package crypto.investments.importer.exception;

import crypto.investments.importer.service.ImportResourceDiscoverer;

/**
 * An exception that is used in {@link ImportResourceDiscoverer}.
 */
public class ImportResourcesDiscoveryException extends Exception {

    public ImportResourcesDiscoveryException(String message) {
        super(message);
    }

    public ImportResourcesDiscoveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
