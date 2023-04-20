package crypto.investments.importer.service;

import crypto.investments.importer.config.ImportProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * An implementation of {@link ImportedResourceHandler} that
 * moves files to a target folder specified in {@link ImportProperties}.
 * Supports only `file` resources.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ImportedResourceMovingHandler extends ImportedResourceHandler {

    private final ImportProperties importProperties;

    @Override
    void handleResource(String resourceUri) {
        File file = getFileFromResourceUri(resourceUri);
        if (file != null) {
            Path targetLocation = getTargetLocationForFile(file);
            log.info("Moving imported resource {} to {}", file.getAbsolutePath(), targetLocation);
            moveFile(file.toPath(), targetLocation);
        }
    }

    private void moveFile(Path src, Path target) {
        try {
            Files.createDirectories(target.getParent());
            Files.move(src, target);
        } catch (IOException e) {
            log.error("Unable to move file %s to %s".formatted(src, target), e);
        }
    }

    private Path getTargetLocationForFile(File file) {
        return Path.of(importProperties.getImportedFilesLocation(), file.getName()).toAbsolutePath();
    }

    private File getFileFromResourceUri(String resourceUri) {
        if (resourceUri.startsWith(ResourceUtils.FILE_URL_PREFIX)) {
            String filePath = resourceUri.substring(ResourceUtils.FILE_URL_PREFIX.length());
            return new File(filePath);
        }

        log.warn("Unsupported resource URI: {}", resourceUri);
        return null;
    }
}
