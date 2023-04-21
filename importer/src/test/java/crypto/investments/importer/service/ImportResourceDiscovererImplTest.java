package crypto.investments.importer.service;

import crypto.investments.importer.config.ImportProperties;
import crypto.investments.importer.exception.ImportResourcesDiscoveryException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImportResourceDiscovererImplTest {

    @Mock
    private ImportProperties propertiesMock;

    @InjectMocks
    private ImportResourceDiscovererImpl discoverer;

    @TempDir
    private Path tempDirectory;

    @Test
    @SneakyThrows
    void testDiscover() {
        // given
        String importPattern = ResourceUtils.FILE_URL_PREFIX + tempDirectory + "/*.csv";
        when(propertiesMock.getImportPattern()).thenReturn(importPattern);

        Path file1 = createResourceFile();
        Path file2 = createResourceFile();

        // when
        List<Resource> resources = discoverer.discover();

        // then
        assertThat(resources)
            .map(resource -> resource.getFile().toPath())
            .containsAll(List.of(file1, file2));
    }

    @Test
    @SneakyThrows
    void testDiscover_whenNoFiles_expectEmptyList() {
        // given
        String importPattern = ResourceUtils.FILE_URL_PREFIX + tempDirectory + "/*.csv";
        when(propertiesMock.getImportPattern()).thenReturn(importPattern);

        // when
        List<Resource> resources = discoverer.discover();

        // then
        assertThat(resources).isEmpty();
    }

    @Test
    void testDiscover_whenUnsupportedLocationIsGiven_expectDiscoveryException() {
        // given
        String importPattern = tempDirectory + "/*.csv";
        when(propertiesMock.getImportPattern()).thenReturn(importPattern);

        // when / then
        assertThrows(ImportResourcesDiscoveryException.class, () -> discoverer.discover());
    }

    @SneakyThrows
    private Path createResourceFile() {
        return Files.createTempFile(tempDirectory, "import-file", ".csv");
    }
}