package bug.spring

import org.redisson.Redisson
import org.redisson.config.Config
import org.redisson.spring.cache.CacheConfig
import org.redisson.spring.cache.RedissonSpringCacheManager
import org.springframework.cache.CacheManager
import org.springframework.cache.support.NoOpCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CacheConfiguration(
    val cacheProperties: CacheProperties,
) {

    @Bean
    fun cacheManager(): CacheManager {
        if (!cacheProperties.enabled)
            return NoOpCacheManager()

        val redissonConfig = Config()
        redissonConfig.useSingleServer().address = cacheProperties.redisUrl
        redissonConfig.useSingleServer().connectionMinimumIdleSize = 4
        redissonConfig.useSingleServer().connectionPoolSize = 4

        // This is where the issue is..., default values increased to exacerbate the problem
        redissonConfig.useSingleServer().retryAttempts = 5 // defaults to 3
        redissonConfig.useSingleServer().retryInterval = 3000 // defaults to 1500
        // -----
        val redisson = Redisson.create(redissonConfig)

        // Configure each cache (ttl of entries in cache, unrelated to connection to redis)
        val cacheConfig: MutableMap<String, CacheConfig> = mutableMapOf()
        if (cacheProperties.cache != null) {
            cacheProperties.cache!!.values.forEach {
                val ttl = (it.ttl ?: 24) * 60 * 1000
                val maxIdleTime = (it.maxIdleTime ?: 12) * 60 * 1000

                cacheConfig[it.name] = CacheConfig(ttl, maxIdleTime)
            }
        }
        val redissonSpringCacheManager = RedissonSpringCacheManager(redisson, cacheConfig)

        return redissonSpringCacheManager
    }

}
