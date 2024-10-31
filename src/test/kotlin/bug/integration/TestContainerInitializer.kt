package bug.integration

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.ext.ScriptUtils
import org.testcontainers.jdbc.JdbcDatabaseDelegate
import org.testcontainers.utility.DockerImageName

class TestContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    companion object {

        val postgresContainer: PostgreSQLContainer<*> = PostgreSQLContainer<Nothing>("postgres:14-alpine")
            .apply {
                withDatabaseName("bug")
                withUsername("postgres")
                withPassword("password")
            }

        val redis: GenericContainer<*> = GenericContainer(DockerImageName.parse("redis:5.0.3-alpine"))
            .withExposedPorts(6379)

        init {
            redis.start()
            postgresContainer.start()

            ScriptUtils.runInitScript(
                JdbcDatabaseDelegate(postgresContainer, ""),
                "schema.sql"
            )
        }

    }

    private fun getJdbcUrl(): String {
        return postgresContainer.jdbcUrl
    }

    private fun getDbUsername(): String {
        return postgresContainer.username
    }

    private fun getDbPassword(): String {
        return postgresContainer.password
    }

    private fun getRedisHost(): String {
        return redis.host
    }

    private fun getRedisPort(): Int {
        return redis.getMappedPort(6379)
    }

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        System.setProperty("jdbc.url", getJdbcUrl())
        System.setProperty("jdbc.username", getDbUsername())
        System.setProperty("jdbc.password", getDbPassword())
        val redisUrl = String.format("redis://%s:%s", getRedisHost(), getRedisPort())
        System.setProperty("cache-configuration.redisUrl", redisUrl)
    }

}
