package app.flock.social.data.dao

import app.flock.social.data.table.RsvpDTO
import app.flock.social.data.table.RsvpsTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.UUID

class RsvpDao {
    fun getAllRsvps(): List<RsvpDTO> {
        return transaction {
            RsvpsTable
                .selectAll()
                .map { rsvp ->
                    RsvpDTO(
                        id = rsvp[RsvpsTable.id].toString(),
                        eventId = rsvp[RsvpsTable.eventId].toString(),
                        userId = rsvp[RsvpsTable.userId].toString(),
                        status = rsvp[RsvpsTable.status]
                    )
                }
        }
    }

    fun getRsvpById(id: String): RsvpDTO? {
        return transaction {
            RsvpsTable
                .select { RsvpsTable.id eq UUID.fromString(id) }
                .map { rsvp ->
                    RsvpDTO(
                        id = rsvp[RsvpsTable.id].toString(),
                        eventId = rsvp[RsvpsTable.eventId].toString(),
                        userId = rsvp[RsvpsTable.userId].toString(),
                        status = rsvp[RsvpsTable.status]
                    )
                }
                .firstOrNull()
        }
    }

    fun createRsvp(rsvp: RsvpDTO) {
        transaction {
            RsvpsTable.insert {
                it[id] = UUID.fromString(rsvp.id)
                it[eventId] = UUID.fromString(rsvp.eventId)
                it[userId] = UUID.fromString(rsvp.userId)
                it[status] = rsvp.status
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun updateRsvp(rsvp: RsvpDTO) {
        transaction {
            RsvpsTable.update({ RsvpsTable.id eq UUID.fromString(rsvp.id) }) {
                it[eventId] = UUID.fromString(rsvp.eventId)
                it[userId] = UUID.fromString(rsvp.userId)
                it[status] = rsvp.status
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun deleteRsvp(id: String) {
        transaction {
            RsvpsTable.deleteWhere { RsvpsTable.id eq UUID.fromString(id) }
        }
    }
}