package app.flock.social.data.table

import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
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
    val ownerId: String?
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
    val ownerId = uuid("owner_id").references(UsersTable.id, onDelete = ReferenceOption.CASCADE).nullable()

    override val primaryKey: PrimaryKey = PrimaryKey(id)
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
        thumbnailUrl = event[EventsTable.thumbnailUrl],
        ownerId = event[EventsTable.ownerId].toString(),
    )
}