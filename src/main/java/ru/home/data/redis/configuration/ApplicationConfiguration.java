package ru.home.data.redis.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;
import org.springframework.data.redis.core.convert.MappingConfiguration;
import org.springframework.data.redis.core.index.IndexConfiguration;
import org.springframework.data.redis.core.index.IndexDefinition;
import org.springframework.data.redis.core.index.SimpleIndexDefinition;
import org.springframework.data.redis.core.mapping.RedisMappingContext;
import ru.home.data.redis.entities.Registrator;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Zaets Dmitriy on 24.03.2017.
 */
@Configuration
public class ApplicationConfiguration {
    @Autowired
    @Bean
    RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory connectionFactory) {

        RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        return template;
    }

    @Bean
    public RedisMappingContext keyValueMappingContext() {
        return new RedisMappingContext(
                new MappingConfiguration(new MyIndexConfiguration(), new MyKeyspaceConfiguration()));
    }

    public static class MyKeyspaceConfiguration extends KeyspaceConfiguration {

        @Override
        protected Iterable<KeyspaceSettings> initialConfiguration() {
            return Collections.singleton(new KeyspaceSettings(Registrator.class, "registrators"));
        }
    }

    public static class MyIndexConfiguration extends IndexConfiguration {

        @Override
        protected Iterable<IndexDefinition> initialConfiguration() {
            return Arrays.asList(new SimpleIndexDefinition("registrators", "version"), new SimpleIndexDefinition("registrators", "lastMessageId"));
        }
    }
}
