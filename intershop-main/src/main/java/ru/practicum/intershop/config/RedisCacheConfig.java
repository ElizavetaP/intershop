package ru.practicum.intershop.config;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Конфигурация Redis кеша для товаров.
 * Аналогично примеру из урока.
 */
@Configuration
public class RedisCacheConfig {

    /**
     * Настройка кеша для товаров с JSON сериализацией и TTL 3 минуты.
     */
    @Bean
    public RedisCacheManagerBuilderCustomizer itemsCacheCustomizer() {
        return builder -> builder.withCacheConfiguration(
            "items",                                        // Имя кеша
            RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.of(3, ChronoUnit.MINUTES))  // TTL 3 минуты
                .serializeValuesWith(                          // Сериализация JSON
                    RedisSerializationContext.SerializationPair.fromSerializer(
                        new Jackson2JsonRedisSerializer<>(Object.class)
                    )
                )
        );
    }
}
