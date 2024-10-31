package bug.machine

import org.apache.logging.log4j.LogManager
import org.springframework.boot.context.event.ApplicationFailedEvent
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
final class ApplicationStatus(
    private val machineRepository: MachineRepository,
    private val machineConfiguration: MachineConfiguration,
) {

    private val logger = LogManager.getLogger()

    init {
        logger.info("UPSERTING MACHINE CONFIGURATION STATUS=STARTING")

        val machine = Machine(machineConfiguration.name)
        machine.runningStatus = RunningStatus.STARTING
        machine.lastStartup = LocalDateTime.now()
        machine.lastUpdate = LocalDateTime.now()
        machineRepository.upsert(machine)
    }

    @EventListener
    fun handleStartupSuccess(ctxStatus: ApplicationReadyEvent) {
        logger.info("UPDATING MACHINE CONFIGURATION STATUS=ONLINE")
        machineRepository.updateRunningStatus(machineConfiguration.name, RunningStatus.ONLINE)
    }

    @EventListener
    fun handleStartupFailure(ctxStatus: ApplicationFailedEvent) {
        logger.info("UPDATING MACHINE CONFIGURATION STATUS=FAILED")
        machineRepository.updateRunningStatus(machineConfiguration.name, RunningStatus.FAILED)
    }

    @EventListener
    fun handleShutdown(ctxStatus: ContextClosedEvent) {
        logger.info("UPDATING MACHINE CONFIGURATION STATUS=SHUTDOWN")
        machineRepository.updateRunningStatus(machineConfiguration.name, RunningStatus.OFFLINE)
    }

}
