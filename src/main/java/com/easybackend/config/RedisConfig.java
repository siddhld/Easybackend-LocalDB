package com.easybackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;

@Configuration
public class RedisConfig {
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        String redisHost = System.getenv("SPRING_REDIS_HOST") != null ? System.getenv("SPRING_REDIS_HOST") : "localhost";
        int redisPort = System.getenv("SPRING_REDIS_PORT") != null ? Integer.parseInt(System.getenv("SPRING_REDIS_PORT")) : 6379;
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(redisHost, redisPort);

        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration);
        factory.afterPropertiesSet();
        try {
            factory.getConnection();
            System.out.println("Successfully connected to Redis");
        } catch (Exception e) {
            System.err.println("Failed to connect to Redis: " + e.getMessage());
            e.printStackTrace();
        }
        return factory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // Set cache expiration time, for example, 10 minutes
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfig)
                .build();
    }
}