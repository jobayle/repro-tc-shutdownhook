package bug.machine

import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.ResultSet
import java.time.LocalDateTime

@Repository
@Transactional(readOnly = true)
class MachineRepository(
    val jdbcTemplate: JdbcTemplate,
) {

    companion object {
        const val TABLE_NAME = "machine"

        // ISEx: No primary or single unique constructor found for class bug.machine.Machine
        //val MAPPER = DataClassRowMapper(Machine::class.java) // why?? 😒

        val MAPPER = RowMapper { rs: ResultSet, _: Int ->
            Machine(
                rs.getString("name"),
                RunningStatus.valueOf(rs.getString("running_status")),
                rs.getTimestamp("last_update").toLocalDateTime(),
                rs.getTimestamp("last_startup").toLocalDateTime(),
                rs.getLong("id")
            )
        }
    }

    @Transactional(readOnly = false)
    @Cacheable("machine-by-name", key = "#machine.name")
    fun upsert(machine: Machine): Machine {
        val existingMachine = findByName(machine.name)
        return if (existingMachine != null) {
            existingMachine.lastStartup = LocalDateTime.now()
            existingMachine.lastUpdate = existingMachine.lastStartup
            existingMachine.runningStatus = machine.runningStatus

            jdbcTemplate.update("""UPDATE $TABLE_NAME
                | SET running_status = ?,
                |     last_startup = now(),
                |     last_update = now()
                | WHERE name = ?""".trimMargin(),
                machine.runningStatus.name,
                machine.name)
            existingMachine
        } else {
            machine.lastStartup = LocalDateTime.now()
            machine.lastUpdate = machine.lastStartup
            val key = SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns("id")
                .executeAndReturnKey(mapOf(
                    "name" to machine.name,
                    "running_status" to machine.runningStatus.name,
                    "last_startup" to machine.lastStartup,
                    "last_update" to machine.lastUpdate
                ))
            machine.id = key.toLong()
            machine
        }
    }

    @CacheEvict("machine-by-name", key = "#name")
    @Transactional(readOnly = false)
    fun updateRunningStatus(name: String, status: RunningStatus) {
        jdbcTemplate.update("UPDATE $TABLE_NAME SET running_status = ?, last_update = now() WHERE name = ?",
            status.name, name)
    }

    @Cacheable("machine-by-name", key = "#name")
    fun findByName(name: String): Machine? {
        return jdbcTemplate.query("SELECT * FROM $TABLE_NAME WHERE name = ? LIMIT 1",
            MAPPER,
            name
        ).firstOrNull()
    }

    fun findAll(): List<Machine> {
        return jdbcTemplate.query("SELECT * FROM $TABLE_NAME",
            MAPPER
        )
    }

}
