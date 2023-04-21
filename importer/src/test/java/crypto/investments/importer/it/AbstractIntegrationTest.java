package crypto.investments.importer.it;

import lombok.SneakyThrows;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

@SpringBootTest
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    private static final String DATABASE_IMAGE = "postgres:15-alpine";
    private static final int DATABASE_STARTUP_TIMEOUT = 300;

    protected static PostgreSQLContainer<?> databaseContainer;
    protected static Path tempDirectory = createTempDirectory();

    static {
        //noinspection resource
        databaseContainer = new PostgreSQLContainer<>(DATABASE_IMAGE)
            .withStartupTimeout(Duration.ofSeconds(DATABASE_STARTUP_TIMEOUT));

        databaseContainer.start();
    }

    @DynamicPropertySource
    public static void dynamicPropertiesRegistrar(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> databaseContainer.getJdbcUrl());
        registry.add("spring.datasource.username", () -> databaseContainer.getUsername());
        registry.add("spring.datasource.password", () -> databaseContainer.getPassword());

        registry.add("import.import-pattern", () -> tempDirectory + "import/*.csv");
        registry.add("import.imported-files-location", () -> tempDirectory.resolve("imported").toString());
    }

    @SneakyThrows
    private static Path createTempDirectory() {
        Path directory = Files.createTempDirectory("crypto-investments");
        directory.toFile().deleteOnExit();

        return directory;
    }
}
