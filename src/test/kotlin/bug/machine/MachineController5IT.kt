package bug.machine

import bug.integration.AbstractIntegrationTestBase
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.slot
import org.apache.logging.log4j.LogManager
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertContains
import kotlin.test.assertEquals

@Transactional
class MachineController5IT(
    @Autowired val restTemplate: TestRestTemplate,
) : AbstractIntegrationTestBase() {

    @MockkBean(relaxed = true, relaxUnitFun = true)
    lateinit var machineRepository: MachineRepository

    private val logger = LogManager.getLogger()

    @BeforeEach
    fun setup() {
        every { machineRepository.findAll() } returns listOf(Machine("machine-05"))
        val machineNameSlot = slot<String>()
        every { machineRepository.findByName(capture(machineNameSlot)) } answers {
            if (machineNameSlot.captured == "machine-05") Machine("machine-05")
            else null
        }
        val machineSlot = slot<Machine>()
        every { machineRepository.upsert(capture(machineSlot))} answers { machineSlot.captured }
    }

    @Test
    fun `test listMachines`() {
        val res = restTemplate.getForEntity("/machines/", String::class.java)
        logger.info(res.body)
        assertEquals(HttpStatus.OK, res.statusCode)
        assertContains(res.body!!, "machine-05")
    }

    @Test
    fun `test getMachine`() {
        val res = restTemplate.getForEntity("/machines/machine-05", String::class.java)
        logger.info(res.body)
        assertEquals(HttpStatus.OK, res.statusCode)
        assertContains(res.body!!, "machine-05")
    }

    @Test
    fun `test getMachine not found`() {
        val res = restTemplate.getForEntity("/machines/foo", String::class.java)
        logger.info(res.body)
        assertEquals(HttpStatus.NOT_FOUND, res.statusCode)
    }

}