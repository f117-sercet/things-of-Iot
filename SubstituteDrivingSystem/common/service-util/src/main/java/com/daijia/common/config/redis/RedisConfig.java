package com.daijia.common.config.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.lang.reflect.Method;
import java.time.Duration;

/**
 * Description： Redis配置类
 *
 * @author: 段世超
 * @aate: Created in 2024/9/3 9:03
 */
@Configuration
@EnableCaching
public class RedisConfig {

    // 使用默认标签做缓存
    @Bean
   public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }
        @Bean
        @Primary
        public RedisTemplate<Object,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){

            RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
            redisTemplate.setConnectionFactory(redisConnectionFactory);

            // string的序列化方式
            StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
            GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();

            // 序列号key value
            redisTemplate.setKeySerializer(stringRedisSerializer);
            redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
            redisTemplate.setHashKeySerializer(stringRedisSerializer);
            redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);

            redisTemplate.afterPropertiesSet();
            return redisTemplate;
    }
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {

        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        // 解决查询缓存转换异常的问题
        ObjectMapper om = new ObjectMapper();
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        // 配置序列化，过期时间60s
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(600000))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .disableCachingNullValues();
        RedisCacheManager cacheManager = RedisCacheManager
                .builder(factory)
                .cacheDefaults(config)
                .build();
        return cacheManager;
    }
    }
