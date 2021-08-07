package ru.itmo.tps.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class CacheConfig {
    @Bean
    fun caffeineConfig(): Caffeine<Any, Any> {
        return Caffeine.newBuilder().expireAfterAccess(Duration.ofSeconds(15L))
    }

    @Bean
    fun cacheManager(caffeine: Caffeine<Any, Any>): CacheManager {
        return CaffeineCacheManager().also { it.setCaffeine(caffeine) }
    }
}