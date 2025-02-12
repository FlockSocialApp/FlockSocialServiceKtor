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
data class EventDTO(
    val id: String,
    @SerialName("display_name")
    val displayName: String,
    val description: String?,
    @SerialName("community_id")
    val communityId: String,
)

object EventsTable : Table("events") {
    val id = uuid("id")
    val displayName = varchar("display_name", 255)
    val description = varchar("description", 1000).nullable()
    val communityId = uuid("community_id").references(CommunityTable.id, onDelete = ReferenceOption.CASCADE)
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

class EventDao {
    fun getAllEvents(): List<EventDTO> {
        return transaction {
            EventsTable
                .selectAll()
                .map { event ->
                    EventDTO(
                        id = event[EventsTable.id].toString(),
                        displayName = event[EventsTable.displayName],
                        description = event[EventsTable.description],
                        communityId = event[EventsTable.communityId].toString()
                    )
                }
        }
    }

    fun getEventById(id: String): EventDTO? {
        return transaction {
            EventsTable
                .select { EventsTable.id eq java.util.UUID.fromString(id) }
                .map { event ->
                    EventDTO(
                        id = event[EventsTable.id].toString(),
                        displayName = event[EventsTable.displayName],
                        description = event[EventsTable.description],
                        communityId = event[EventsTable.communityId].toString()
                    )
                }
                .firstOrNull()
        }
    }

    fun createEvent(event: EventDTO) {
        transaction {
            EventsTable.insert {
                it[id] = java.util.UUID.fromString(event.id)
                it[displayName] = event.displayName
                it[description] = event.description
                it[communityId] = java.util.UUID.fromString(event.communityId)
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun updateEvent(event: EventDTO) {
        transaction {
            EventsTable.update({ EventsTable.id eq java.util.UUID.fromString(event.id) }) {
                it[displayName] = event.displayName
                it[description] = event.description
                it[communityId] = java.util.UUID.fromString(event.communityId)
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun deleteEvent(id: String) {
        transaction {
            EventsTable.deleteWhere { EventsTable.id eq java.util.UUID.fromString(id) }
        }
    }
}
