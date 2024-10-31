package bug.machine

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDateTime

data class Machine(
    var name: String,

    var runningStatus: RunningStatus = RunningStatus.OFFLINE,

    var lastUpdate: LocalDateTime = LocalDateTime.now(),

    var lastStartup: LocalDateTime = LocalDateTime.now(),

    @JsonIgnore
    var id: Long? = null,
)
