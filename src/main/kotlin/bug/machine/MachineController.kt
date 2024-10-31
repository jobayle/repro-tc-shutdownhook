package bug.machine

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/machines")
class MachineController (
    private val machineRepository: MachineRepository
){

    @GetMapping("/", produces = ["application/json"])
    fun listMachines() : List<Machine> {
        return machineRepository.findAll()
    }

    @GetMapping("/{name}", produces = ["application/json"])
    fun getMachine(@PathVariable name: String) : Machine {
        return machineRepository.findByName(name) ?:
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Machine not found: $name")
    }

}
