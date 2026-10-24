package com.jpbazaar.cache;

import com.jpbazaar.config.RedisConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class RedisConfigTest {

    @Test
    @DisplayName("Should verify Redis cache region names")
    void shouldVerifyCacheNames() {
        assertThat(RedisConfig.CACHE_PRODUCTS).isEqualTo("products");
        assertThat(RedisConfig.CACHE_CATEGORY).isEqualTo("category");
        assertThat(RedisConfig.CACHE_CATEGORIES).isEqualTo("categories");
    }

    @Test
    @DisplayName("Should create RedisTemplate with configured serializers")
    void shouldConfigureRedisTemplate() {
        RedisConfig redisConfig = new RedisConfig();
        RedisConnectionFactory connectionFactory = mock(RedisConnectionFactory.class);

        RedisTemplate<String, Object> template = redisConfig.redisTemplate(connectionFactory);
        assertThat(template).isNotNull();
        assertThat(template.getKeySerializer()).isNotNull();
        assertThat(template.getValueSerializer()).isNotNull();
        assertThat(template.getHashKeySerializer()).isNotNull();
        assertThat(template.getHashValueSerializer()).isNotNull();
    }

    @Test
    @DisplayName("Should configure RedisCacheManager with customized TTL per cache region")
    void shouldConfigureRedisCacheManager() {
        RedisConfig redisConfig = new RedisConfig();
        RedisConnectionFactory connectionFactory = mock(RedisConnectionFactory.class);

        RedisCacheManager cacheManager = redisConfig.cacheManager(connectionFactory);
        assertThat(cacheManager).isNotNull();
    }
}
