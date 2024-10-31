package bug.machine

import bug.integration.AbstractIntegrationTestBase
import org.apache.logging.log4j.LogManager
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertContains
import kotlin.test.assertEquals

@TestPropertySource(
    properties = [
        "machine.name = machine-04"
    ]
)
@Transactional
class MachineController4IT(
    @Autowired val restTemplate: TestRestTemplate,
) : AbstractIntegrationTestBase() {

    private val logger = LogManager.getLogger()

    @Test
    fun `test listMachines`() {
        val res = restTemplate.getForEntity("/machines/", String::class.java)
        logger.info(res.body)
        assertEquals(HttpStatus.OK, res.statusCode)
        assertContains(res.body!!, "machine-04")
    }

    @Test
    fun `test getMachine`() {
        val res = restTemplate.getForEntity("/machines/machine-04", String::class.java)
        logger.info(res.body)
        assertEquals(HttpStatus.OK, res.statusCode)
        assertContains(res.body!!, "machine-04")
    }

    @Test
    fun `test getMachine not found`() {
        val res = restTemplate.getForEntity("/machines/foo", String::class.java)
        logger.info(res.body)
        assertEquals(HttpStatus.NOT_FOUND, res.statusCode)
    }

}