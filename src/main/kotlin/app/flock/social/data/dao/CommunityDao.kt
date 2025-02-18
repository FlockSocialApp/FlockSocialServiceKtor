package app.flock.social.data.dao

import app.flock.social.data.table.CommunityDTO
import app.flock.social.data.table.CommunityTable
import app.flock.social.data.table.EventDTO
import app.flock.social.data.table.EventsTable
import app.flock.social.data.table.mapRowToCommunityDTO
import app.flock.social.data.table.mapRowToEventDTO
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

class CommunityDao {
    fun getAllCommunities(): List<CommunityDTO> {
        return transaction {
            CommunityTable
                .selectAll()
                .map { community ->
                    mapRowToCommunityDTO(community)
                }
        }
    }

    fun getCommunityById(id: String): CommunityDTO? {
        return transaction {
            CommunityTable
                .select { CommunityTable.id eq UUID.fromString(id) }
                .map { community ->
                    mapRowToCommunityDTO(community)
                }
                .firstOrNull()
        }
    }

    fun createCommunity(community: CommunityDTO) {
        transaction {
            CommunityTable.insert {
                it[id] = UUID.fromString(community.id)
                it[displayName] = community.displayName
                it[description] = community.description
                it[ownerId] = UUID.fromString(community.ownerId)
                it[requiresApplication] = community.requiresApplication
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun updateCommunity(community: CommunityDTO) {
        transaction {
            CommunityTable.update({ CommunityTable.id eq UUID.fromString(community.id) }) {
                it[displayName] = community.displayName
                it[description] = community.description
                it[ownerId] = UUID.fromString(community.ownerId)
                it[requiresApplication] = community.requiresApplication
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun deleteCommunity(id: String) {
        transaction {
            CommunityTable.deleteWhere { CommunityTable.id eq UUID.fromString(id) }
        }
    }

    @Serializable
    data class CommunityWithEvents(
        val community: CommunityDTO,
        val events: List<EventDTO>
    )

    fun getCommunityWithEvents(id: String): CommunityWithEvents? {
        return transaction {
            val community = CommunityTable
                .select { CommunityTable.id eq UUID.fromString(id) }
                .map { community ->
                    mapRowToCommunityDTO(community)
                }
                .firstOrNull() ?: return@transaction null

            val events = EventsTable
                .select { EventsTable.communityId eq UUID.fromString(id) }
                .map { event ->
                    mapRowToEventDTO(event)
                }

            CommunityWithEvents(community, events)
        }
    }
}
