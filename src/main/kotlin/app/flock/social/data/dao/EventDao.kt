package app.flock.social.data.dao

import app.flock.social.data.table.EventDTO
import app.flock.social.data.table.EventsTable
import app.flock.social.data.table.RsvpsTable
import app.flock.social.data.table.UserDTO
import app.flock.social.data.table.UsersTable
import app.flock.social.data.table.mapRowToEventDTO
import app.flock.social.data.table.mapRowToUserDTO
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.UUID

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
                .select { EventsTable.id eq UUID.fromString(id) }
                .map { event ->
                    mapRowToEventDTO(event)
                }
                .firstOrNull()
        }
    }

    fun createEvent(event: EventDTO) {
        transaction {
            EventsTable.insert {
                it[id] = UUID.fromString(event.id)
                it[displayName] = event.displayName
                it[description] = event.description
                it[communityId] = UUID.fromString(event.communityId)
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
            EventsTable.update({ EventsTable.id eq UUID.fromString(event.id) }) {
                it[displayName] = event.displayName
                it[description] = event.description
                it[communityId] = UUID.fromString(event.communityId)
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
            EventsTable.deleteWhere { EventsTable.id eq UUID.fromString(id) }
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
                .select { EventsTable.id eq UUID.fromString(eventId) }
                .map { row ->
                    mapRowToEventDTO(row)
                }
                .firstOrNull() ?: throw Throwable("Missing Event")

            // Then get all attendees for this event through RSVPs
            val attendees = (UsersTable innerJoin RsvpsTable)
                .select { RsvpsTable.eventId eq UUID.fromString(eventId) }
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