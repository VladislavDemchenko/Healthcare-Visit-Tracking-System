package org.demchenko.conf;

import org.demchenko.exception.CacheClearingException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class CacheCleaner implements ApplicationRunner {
    private final StringRedisTemplate redisTemplate;

    public CacheCleaner(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        Optional.ofNullable(redisTemplate.getConnectionFactory())
                .ifPresentOrElse(factory -> factory.getConnection().flushAll(),
                        () -> {
                            throw new CacheClearingException("Cannot clean cache. ConnectionFactory == null!");
                        });
    }


}
