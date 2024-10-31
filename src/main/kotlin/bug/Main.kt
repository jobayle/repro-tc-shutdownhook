package bug

import org.apache.logging.log4j.LogManager
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan("bug")
class Main

private val logger = LogManager.getLogger()

fun main(args: Array<String>) {
    runApplication<Main>(*args)
    logger.info("Bug application successfully started")
}
