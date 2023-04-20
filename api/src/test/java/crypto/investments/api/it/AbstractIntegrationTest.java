package crypto.investments.api.it;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

@SpringBootTest
@ActiveProfiles("it")
public abstract class AbstractIntegrationTest {

    private static final String DATABASE_IMAGE = "timescale/timescaledb-ha:pg15-latest";
    private static final String DOCKER_ENTRYPOINT = "/docker-entrypoint-initdb.d/";
    private static final int DATABASE_STARTUP_TIMEOUT = 300;

    protected static PostgreSQLContainer<?> databaseContainer;

    static {
        DockerImageName dbImage = DockerImageName
            .parse(DATABASE_IMAGE)
            .asCompatibleSubstituteFor("postgres");

        //noinspection resource
        databaseContainer = new PostgreSQLContainer<>(dbImage)
            .withStartupTimeout(Duration.ofSeconds(DATABASE_STARTUP_TIMEOUT));

        databaseContainer.start();
    }

    @DynamicPropertySource
    public static void dynamicPropertiesRegistrar(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> databaseContainer.getJdbcUrl());
        registry.add("spring.datasource.username", () -> databaseContainer.getUsername());
        registry.add("spring.datasource.password", () -> databaseContainer.getPassword());
    }
}
