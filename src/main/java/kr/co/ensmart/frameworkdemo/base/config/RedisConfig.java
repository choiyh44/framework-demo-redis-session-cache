package kr.co.ensmart.frameworkdemo.base.config;

import java.time.Duration;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;

import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import lombok.Getter;
import lombok.Setter;

/**
 * @author choiyh44
 * @version 1.0
 * @since 2022. 2. 18.
 *
 */
@Configuration
@ConfigurationProperties(prefix = "redis-list")
@Getter
@Setter
public class RedisConfig {
    private RedisProperties redis1;
    private RedisProperties redis2;

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(ClientResources.class)
    public DefaultClientResources lettuceClientResources() {
        return DefaultClientResources.create();
    }

    @Bean(name = "stringRedisTemplate1")
    @Primary
    public StringRedisTemplate stringRedisTemplate1(
            @Qualifier("redis1LettuceConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean(name = "redis1LettuceConnectionFactory")
    @Primary
    public LettuceConnectionFactory redis1LettuceConnectionFactory(ClientResources clientResources) {
        RedisStandaloneConfiguration redis1StandaloneConfiguration = getStandaloneConfig(redis1);
        LettuceClientConfiguration clientConfig = getLettuceClientConfiguration(clientResources, redis1);
        return new LettuceConnectionFactory(redis1StandaloneConfiguration, clientConfig);
    }

    @Bean(name = "stringRedisTemplate2")
    public StringRedisTemplate stringRedisTemplate2(
            @Qualifier("redis2LettuceConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean(name = "redis2LettuceConnectionFactory")
    public LettuceConnectionFactory redis2LettuceConnectionFactory(ClientResources clientResources) {
        RedisStandaloneConfiguration redis1StandaloneConfiguration = getStandaloneConfig(redis2);
        LettuceClientConfiguration clientConfig = getLettuceClientConfiguration(clientResources, redis2);
        return new LettuceConnectionFactory(redis1StandaloneConfiguration, clientConfig);
    }

    /**
     * redis standalone config
     *
     *@ param redisproperties redis configuration parameters
     * @return RedisStandaloneConfiguration
     */
    private RedisStandaloneConfiguration getStandaloneConfig(RedisProperties redisProperties) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisProperties.getHost());
        config.setPort(redisProperties.getPort());
        config.setPassword(RedisPassword.of(redisProperties.getPassword()));
        config.setDatabase(redisProperties.getDatabase());
        return config;
    }

    /**
     *Build lettucleclientconfiguration
     *
     * @param clientResources clientResources
     * @param redisProperties redisProperties
     * @return LettuceClientConfiguration
     */
    private LettuceClientConfiguration getLettuceClientConfiguration(ClientResources clientResources,
                                                                     RedisProperties redisProperties) {
        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder =
                createBuilder(redisProperties.getLettuce().getPool());
        if (redisProperties.isSsl()) {
            builder.useSsl();
        }
        if (redisProperties.getTimeout() != null) {
            builder.commandTimeout(redisProperties.getTimeout());
        }
        if (redisProperties.getLettuce() != null) {
            RedisProperties.Lettuce lettuce = redisProperties.getLettuce();
            if (lettuce.getShutdownTimeout() != null
                    && !lettuce.getShutdownTimeout().isZero()) {
                builder.shutdownTimeout(
                        redisProperties.getLettuce().getShutdownTimeout());
            }
        }
        builder.clientResources(clientResources);
        return builder.build();
    }

    /**
     *Create lettucleclientconfigurationbuilder
     *
     *@ param pool connection pool configuration
     * @return LettuceClientConfigurationBuilder
     */
    private LettuceClientConfiguration.LettuceClientConfigurationBuilder createBuilder(RedisProperties.Pool pool) {
        if (pool == null) {
            return LettuceClientConfiguration.builder();
        }
        return LettucePoolingClientConfiguration.builder()
                .poolConfig(getPoolConfig(pool));
    }

    /**
     * pool config
     *
     *@ param properties redis parameter configuration
     * @return GenericObjectPoolConfig
     */
    private GenericObjectPoolConfig getPoolConfig(RedisProperties.Pool properties) {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(properties.getMaxActive());
        config.setMaxIdle(properties.getMaxIdle());
        config.setMinIdle(properties.getMinIdle());
        if (properties.getMaxWait() != null) {
            config.setMaxWait(Duration.ofMillis(properties.getMaxWait().toMillis()));
        }
        return config;
    }

}
