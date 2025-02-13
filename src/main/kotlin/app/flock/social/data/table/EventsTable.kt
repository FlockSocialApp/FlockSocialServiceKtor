package app.flock.social.data.table

import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
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
data class EventDTO constructor(
    val id: String,
    @SerialName("display_name")
    val displayName: String,
    val description: String?,
    @SerialName("community_id")
    val communityId: String,
    val address: String,
    val startTime: kotlinx.datetime.LocalDateTime?,
    val endTime: kotlinx.datetime.LocalDateTime?,
    val cost: String?,
    val thumbnailUrl: String?,
)

object EventsTable : Table("events") {
    val id = uuid("id")
    val displayName = varchar("display_name", 255)
    val description = varchar("description", 1000).nullable()
    val communityId = uuid("community_id").references(CommunityTable.id, onDelete = ReferenceOption.CASCADE)
    val address = varchar("address", 255)
    val startTime = datetime("start_time").nullable()
    val endTime = datetime("end_time").nullable()
    val cost = decimal("cost", 10, 2).nullable()
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }
    val thumbnailUrl = varchar("thumbnail_url", 255).nullable()

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

class EventDao {
    fun getAllEvents(): List<EventDTO> {
        return transaction {
            EventsTable
                .selectAll()
                .map { event ->
                    mapRowToEventDTO(event)
                }
        }
    }

    fun getEventById(id: String): EventDTO? {
        return transaction {
            EventsTable
                .select { EventsTable.id eq java.util.UUID.fromString(id) }
                .map { event ->
                    mapRowToEventDTO(event)
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
                it[address] = event.address
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
                it[cost] = event.cost?.toBigDecimal()
                it[thumbnailUrl] = event.thumbnailUrl
            }
        }
    }

    fun updateEvent(event: EventDTO) {
        transaction {
            EventsTable.update({ EventsTable.id eq java.util.UUID.fromString(event.id) }) {
                it[displayName] = event.displayName
                it[description] = event.description
                it[communityId] = java.util.UUID.fromString(event.communityId)
                it[address] = event.address
                it[startTime] = event.startTime?.toJavaLocalDateTime()
                it[endTime] = event.endTime?.toJavaLocalDateTime()
                it[updatedAt] = LocalDateTime.now()
                it[cost] = event.cost?.toBigDecimal()
                it[thumbnailUrl] = event.thumbnailUrl
            }
        }
    }

    fun deleteEvent(id: String) {
        transaction {
            EventsTable.deleteWhere { EventsTable.id eq java.util.UUID.fromString(id) }
        }
    }

    @Serializable
    class EventWithAttendees(
        val event: EventDTO,
        val attendees: List<UserDTO>,
    )

    fun getEventWithAttendees(eventId: String): EventWithAttendees {
        return transaction {
            // First get the event
            val event = EventsTable
                .select { EventsTable.id eq java.util.UUID.fromString(eventId) }
                .map { row ->
                    mapRowToEventDTO(row)
                }
                .firstOrNull() ?: throw Throwable("Missing Event")

            // Then get all attendees for this event through RSVPs
            val attendees = (UsersTable innerJoin RsvpsTable)
                .select { RsvpsTable.eventId eq java.util.UUID.fromString(eventId) }
                .map { row ->
                    mapRowToUserDTO(row)
                }

            // Create an anonymous object implementing EventWithAttendees
            EventWithAttendees(
                event = event,
                attendees = attendees,
            )
        }
    }
}

fun mapRowToEventDTO(event: ResultRow): EventDTO {
    return EventDTO(
        id = event[EventsTable.id].toString(),
        displayName = event[EventsTable.displayName],
        description = event[EventsTable.description],
        communityId = event[EventsTable.communityId].toString(),
        address = event[EventsTable.address],
        startTime = event[EventsTable.startTime]?.toKotlinLocalDateTime(),
        endTime = event[EventsTable.endTime]?.toKotlinLocalDateTime(),
        cost = event[EventsTable.cost]?.toString(),
        thumbnailUrl = event[EventsTable.thumbnailUrl]
    )
}