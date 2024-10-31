package bug.spring

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cache-configuration")
data class CacheProperties(
    @Value("\${redisUrl}") val redisUrl: String,
    val cache: Map<String, CacheConfiguration>?,
    @Value("\${enabled}") val enabled: Boolean,
) {
    data class CacheConfiguration(
        val name: String,
        val ttl: Long?,
        val maxIdleTime: Long?
    )
}
