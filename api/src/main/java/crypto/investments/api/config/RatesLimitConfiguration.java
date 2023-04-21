package crypto.investments.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class RatesLimitConfiguration {

    /**
     * Redis connection for Bucket4J
     */
    @Bean
    RedisConnection bucket4jConnection(LettuceConnectionFactory connectionFactory) {
        return connectionFactory.getConnection();
    }
}
