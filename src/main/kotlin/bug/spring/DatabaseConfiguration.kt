package bug.spring

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.TransactionManager
import javax.sql.DataSource


@Configuration
class DatabaseConfiguration(
    @Value("\${jdbc.url}") val jdbcUrl: String,
    @Value("\${jdbc.username}") val jdbcUsername: String,
    @Value("\${jdbc.password}") val jdbcPassword: String,
    @Value("\${jdbc.maximumPoolSize:10}") val maximumPoolSize: Int,
    @Value("\${jdbc.leakDetectionThreshold:3000}") val leakDetectionThreshold: Long,
) {

    @Bean
    fun readWriteDataSource(): DataSource {
        val dataSource = PGSimpleDataSource()
        dataSource.setURL(jdbcUrl)
        dataSource.user = jdbcUsername
        dataSource.password = jdbcPassword
        return connectionPoolDataSource(dataSource)
    }

    protected fun hikariConfig(dataSource: DataSource?): HikariConfig? {
        val hikariConfig = HikariConfig()
        hikariConfig.maximumPoolSize = maximumPoolSize
        hikariConfig.dataSource = dataSource
        hikariConfig.isAutoCommit = false
        hikariConfig.leakDetectionThreshold = leakDetectionThreshold
        return hikariConfig
    }

    protected fun connectionPoolDataSource(dataSource: DataSource): HikariDataSource {
        return HikariDataSource(hikariConfig(dataSource))
    }

    @Bean
    fun namedParameterJdbcOperations(dataSource: DataSource): NamedParameterJdbcOperations {
        return NamedParameterJdbcTemplate(dataSource)
    }

    @Bean
    fun transactionManager(dataSource: DataSource): TransactionManager {
        return DataSourceTransactionManager(dataSource)
    }

}
