package bug.machine

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
data class MachineConfiguration (

    @Value("\${machine.name}")
    val name: String

)
