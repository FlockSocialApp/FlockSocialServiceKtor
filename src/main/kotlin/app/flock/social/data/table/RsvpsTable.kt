package app.flock.social.data.table

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

@Serializable
data class RsvpDTO(
    val id: String,
    @SerialName("event_id")
    val eventId: String,
    @SerialName("user_id")
    val userId: String,
    val status: String
)

object RsvpsTable : Table("rsvps") {
    val id = uuid("id")
    val eventId = uuid("event_id").references(EventsTable.id,  onDelete = ReferenceOption.CASCADE)
    val userId = uuid("user_id").references(UsersTable.id,  onDelete = ReferenceOption.CASCADE)
    val status = varchar("status", 255)
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

class RsvpDao {
    fun getAllRsvps(): List<RsvpDTO> {
        return RsvpsTable
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

    fun getRsvpById(id: String): RsvpDTO? {
        return RsvpsTable
            .select { RsvpsTable.id eq java.util.UUID.fromString(id) }
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

    fun createRsvp(rsvp: RsvpDTO) {
        transaction {
            RsvpsTable.insert {
                it[id] = java.util.UUID.fromString(rsvp.id)
                it[eventId] = java.util.UUID.fromString(rsvp.eventId)
                it[userId] = java.util.UUID.fromString(rsvp.userId)
                it[status] = rsvp.status
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun updateRsvp(rsvp: RsvpDTO) {
        transaction {
            RsvpsTable.update({ RsvpsTable.id eq java.util.UUID.fromString(rsvp.id) }) {
                it[eventId] = java.util.UUID.fromString(rsvp.eventId)
                it[userId] = java.util.UUID.fromString(rsvp.userId)
                it[status] = rsvp.status
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun deleteRsvp(id: String) {
        transaction {
            RsvpsTable.deleteWhere { RsvpsTable.id eq java.util.UUID.fromString(id) }
        }
    }
}