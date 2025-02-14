package app.flock.social.data.table

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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
data class CommunityDTO(
    val id: String,
    @SerialName("display_name")
    val displayName: String,
    val description: String?,
    @SerialName("owner_id")
    val ownerId: String,
    val requiresApplication: Boolean?
)

object CommunityTable : Table("communities") {
    val id = uuid("id")
    val displayName = varchar(name = "display_name", length = 255)
    val description = varchar(name = "description", length = 1000).nullable()
    val ownerId = uuid("owner_id").references(UsersTable.id)
    val requiresApplication = bool("requires_application").nullable()
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

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
                .select { CommunityTable.id eq java.util.UUID.fromString(id) }
                .map { community ->
                    mapRowToCommunityDTO(community)
                }
                .firstOrNull()
        }
    }

    fun createCommunity(community: CommunityDTO) {
        transaction {
            CommunityTable.insert {
                it[id] = java.util.UUID.fromString(community.id)
                it[displayName] = community.displayName
                it[description] = community.description
                it[ownerId] = java.util.UUID.fromString(community.ownerId)
                it[requiresApplication] = community.requiresApplication
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun updateCommunity(community: CommunityDTO) {
        transaction {
            CommunityTable.update({ CommunityTable.id eq java.util.UUID.fromString(community.id) }) {
                it[displayName] = community.displayName
                it[description] = community.description
                it[ownerId] = java.util.UUID.fromString(community.ownerId)
                it[requiresApplication] = community.requiresApplication
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun deleteCommunity(id: String) {
        transaction {
            CommunityTable.deleteWhere { CommunityTable.id eq java.util.UUID.fromString(id) }
        }
    }
}

fun mapRowToCommunityDTO(community: ResultRow) = CommunityDTO(
    id = community[CommunityTable.id].toString(),
    displayName = community[CommunityTable.displayName],
    description = community[CommunityTable.description],
    ownerId = community[CommunityTable.ownerId].toString(),
    requiresApplication = community[CommunityTable.requiresApplication],
)