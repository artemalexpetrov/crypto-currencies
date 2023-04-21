package crypto.investments.api.it;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

@SpringBootTest
@ActiveProfiles("it")
public abstract class AbstractIntegrationTest {

    private static final String REDIS_IMAGE = "redis:5.0.3-alpine";
    private static final String DATABASE_IMAGE = "timescale/timescaledb-ha:pg15-latest";

    private static final int REDIS_PORT = 6379;
    private static final int DATABASE_STARTUP_TIMEOUT = 300;
    private static final int REDIS_STARTUP_TIMEOUT = 60;

    protected static PostgreSQLContainer<?> databaseContainer;
    protected static GenericContainer<?> redisContainer;

    static {
        //noinspection resource
        databaseContainer = new PostgreSQLContainer<>(DockerImageName
            .parse(DATABASE_IMAGE)
            .asCompatibleSubstituteFor("postgres"))
            .withStartupTimeout(Duration.ofSeconds(DATABASE_STARTUP_TIMEOUT));

        //noinspection resource
        redisContainer = new GenericContainer<>(DockerImageName.parse(REDIS_IMAGE))
            .withExposedPorts(REDIS_PORT)
            .withStartupTimeout(Duration.ofSeconds(REDIS_STARTUP_TIMEOUT));

        databaseContainer.start();
        redisContainer.start();
    }

    @DynamicPropertySource
    public static void dynamicPropertiesRegistrar(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> databaseContainer.getJdbcUrl());
        registry.add("spring.datasource.username", () -> databaseContainer.getUsername());
        registry.add("spring.datasource.password", () -> databaseContainer.getPassword());

        registry.add("spring.data.redis.host", () -> redisContainer.getHost());
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(REDIS_PORT));
    }
}
