package crypto.investments.importer.service;


import crypto.investments.importer.config.ImportProperties;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ResourceUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImportedResourceMovingHandlerTest {

    public static final String FILE_CONTENT_DIGEST_ALGORITHM = "MD5";
    public static final String IMPORTED_FILES_DIRECTORY = "imported";
    @Mock
    private ImportProperties properties;

    @InjectMocks
    private ImportedResourceMovingHandler handler;

    @TempDir
    private Path tempDirectory;

    private Path importedFilesDirectory;

    @BeforeEach
    void setUp() {
        importedFilesDirectory = tempDirectory.resolve(IMPORTED_FILES_DIRECTORY);
        when(properties.getImportedFilesLocation()).thenReturn(importedFilesDirectory.toString());
    }

    @Test
    void testHandleResource_expectFileMoved() {
        // given
        Path resourcePath = createFile();
        Path expectedTargetPath = importedFilesDirectory.resolve(resourcePath.getFileName());
        String resourceUri = getFileResourceUri(resourcePath);

        // when
        handler.handleResource(resourceUri);

        // then
        assertThat(resourcePath).doesNotExist();
        assertThat(expectedTargetPath).exists();
    }

    @Test
    void testHandleResource_expectFileDigestDidntChange() {
        // given
        Path resourcePath = createFile();
        Path expectedTargetPath = importedFilesDirectory.resolve(resourcePath.getFileName());
        String resourceUri = getFileResourceUri(resourcePath);
        byte[] expectedDigest = getFileDigest(resourcePath);

        // when
        handler.handleResource(resourceUri);

        // then
        assertThat(expectedTargetPath)
            .exists()
            .hasDigest(FILE_CONTENT_DIGEST_ALGORITHM, expectedDigest);
    }

    @Test
    void testHandleResource_whenFileDoesntExist_expectNoException() {
        // given
        Path resourcePath = tempDirectory.resolve("no-file.csv");
        String resourceUri = getFileResourceUri(resourcePath);

        // when / then
        Assertions.assertDoesNotThrow(() -> handler.handleResource(resourceUri));
    }

    @SneakyThrows
    private Path createFile() {
        Path file = Files.createTempFile(tempDirectory, "import-file", ".csv");
        Files.writeString(file, "Stub");

        return file;
    }

    @SneakyThrows
    private byte[] getFileDigest(Path file) {
        MessageDigest digest = MessageDigest.getInstance(FILE_CONTENT_DIGEST_ALGORITHM);
        byte[] bytes = Files.readAllBytes(file);
        return digest.digest(bytes);
    }

    private String getFileResourceUri(Path path) {
        return ResourceUtils.FILE_URL_PREFIX + path.toAbsolutePath();
    }
}