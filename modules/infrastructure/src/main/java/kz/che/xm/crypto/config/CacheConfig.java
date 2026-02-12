package kz.che.xm.crypto.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

import static com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL;
import static com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator.instance;
import static org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig;
import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer;

@Configuration
public class CacheConfig {
    private static final int IP_DB = 0;
    private static final int CACHE_DB = 1;
    @Value("${spring.data.redis.host}")
    private String redisHost;
    @Value("${spring.data.redis.port}")
    private int redisPort;
    @Value("${spring.data.redis.password}")
    private String redisPassword;

    @Bean
    @ConfigurationProperties(prefix = "spring.data.redis")
    public RedisStandaloneConfiguration redisStandaloneConfiguration() {
        return new RedisStandaloneConfiguration();
    }

    @Bean
    public LettuceConnectionFactory ipFilterCFactory(RedisStandaloneConfiguration base) {
        RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration(redisHost, redisPort);
        conf.setUsername(base.getUsername());
        conf.setPassword(redisPassword);
        conf.setDatabase(IP_DB);
        return new LettuceConnectionFactory(conf);
    }

    @Bean
    @Primary
    public LettuceConnectionFactory cacheCFactory(RedisStandaloneConfiguration base) {
        RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration(redisHost, redisPort);
        conf.setUsername(base.getUsername());
        conf.setPassword(redisPassword);
        conf.setDatabase(CACHE_DB);
        return new LettuceConnectionFactory(conf);
    }

    @Bean
    public StringRedisTemplate ipFilterRedisTemplate(@Qualifier("ipFilterCFactory") RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    @Bean
    public RedisTemplate<String, Object> cacheRedisTemplate(@Qualifier("cacheCFactory") RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return defaultCacheConfig()
                .serializeKeysWith(fromSerializer(new StringRedisSerializer()))
                .prefixCacheNameWith("cache::")
                .disableCachingNullValues()
                .entryTtl(Duration.ofSeconds(30));
    }

    @Bean
    ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.activateDefaultTyping(instance, NON_FINAL);
        return mapper;
    }

    @Bean
    @Primary
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory,
                                   @Qualifier("redisObjectMapper") ObjectMapper redisObjectMapper) {
        ObjectMapper mapper = redisObjectMapper.copy().findAndRegisterModules();
        RedisSerializationContext.SerializationPair<Object> valueSerializer =
                fromSerializer(new GenericJackson2JsonRedisSerializer(mapper));
        RedisCacheConfiguration config = defaultCacheConfig().serializeValuesWith(valueSerializer)
                .serializeKeysWith(fromSerializer(new StringRedisSerializer()));
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }
}

